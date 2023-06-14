package com.innowise.enricherservice.service.impl;

import com.innowise.enricherservice.client.SpotifyClient;
import com.innowise.enricherservice.service.MetadataExtractorService;
import com.innowise.enricherservice.service.OutputSqsService;
import com.innowise.enricherservice.service.SongDownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Slf4j
@Component
@RequiredArgsConstructor
@RestController()
public class QueueListenerServiceImpl {
    private static final String QUEUE_URL = "http://localhost:4566/000000000000/file-api-queue";
    private final SqsClient sqsClient;
    private final OutputSqsService outputSqsService;
    private final MetadataExtractorService metadataExtractorService;
    private final SongDownloadService songDownloadService;
    private final SpotifyClient spotifyClient;

    //    @SqsListener(value = "file-api-queue")
    @GetMapping("/get")
    public void get() {

        var receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .maxNumberOfMessages(10)
                .build();
        var iterator = 0;
        try {
            while (true) {
                var receiveResponse = sqsClient.receiveMessage(receiveRequest);
                var messages = receiveResponse.messages();
                if (!messages.isEmpty()) {
                    for (Message message : messages) {
                        var messageId = message.messageId();
                        var body = message.body();
                        log.info("Message received");
                        log.info("Message ID: " + messageId);
                        log.info("Message Body: " + body);
                        var deleteRequest = DeleteMessageRequest.builder()
                                .queueUrl(QUEUE_URL)
                                .receiptHandle(message.receiptHandle())
                                .build();
                        try {
                            var file = songDownloadService.downloadFile(Long.valueOf(body));
                            var songData = metadataExtractorService.extractMetadataFromFile(file);
                            var apiResponse = spotifyClient.search(songData);
                            sqsClient.deleteMessage(deleteRequest);
                            outputSqsService.addIdInQueue(String.valueOf(iterator++));
//                            outputSqsService.addIdInQueue(apiResponse.getBody());
//                            return apiResponse;
                        } catch (Exception e) {
                            log.error("Error while file processing");
                            e.printStackTrace();
//                            throw new InternalServerErrorException("Error while song file processing");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
//            throw new InternalServerErrorException("Internal server error");
        }
    }
}
