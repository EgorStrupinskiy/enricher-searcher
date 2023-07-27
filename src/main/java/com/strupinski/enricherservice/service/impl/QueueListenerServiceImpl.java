package com.strupinski.enricherservice.service.impl;

import com.strupinski.enricherservice.client.SpotifyClient;
import com.strupinski.enricherservice.service.MetadataExtractorService;
import com.strupinski.enricherservice.service.OutputSqsService;
import com.strupinski.enricherservice.service.SongDownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;

@Slf4j
@Component
@RequiredArgsConstructor
@RestController
public class QueueListenerServiceImpl {
    private static final String QUEUE_URL = "http://localhost:4566/000000000000/file-api-queue";
    private final SqsClient sqsClient;
    private final OutputSqsService outputSqsService;
    private final MetadataExtractorService metadataExtractorService;
    private final SongDownloadService songDownloadService;
    private final SpotifyClient spotifyClient;

    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }

    @SqsListener(value = "file-api-queue")
    public void get(String message) {

        try {
            if (!message.isEmpty()) {
                log.info("Message received");
                log.info("Message Body: " + message);
                try {
                    var file = songDownloadService.downloadFile(Long.valueOf(message));
                    var songData = metadataExtractorService.extractMetadataFromFile(file);
                    var apiResponse = spotifyClient.search(songData);
                    var deleteRequest = DeleteMessageRequest.builder()
                            .queueUrl(QUEUE_URL)
                            .receiptHandle(message)
                            .build();
                    sqsClient.deleteMessage(deleteRequest);
                    outputSqsService.addIdInQueue(apiResponse.getBody());
                } catch (Exception e) {
                    log.error("Error while file processing");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
