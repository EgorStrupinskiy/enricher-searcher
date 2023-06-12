package com.innowise.enricherservice.service.impl;

import com.innowise.enricherservice.route.SqsService;
import com.innowise.enricherservice.service.MetadataExtractorService;
import com.innowise.enricherservice.service.QueueListenerService;
import com.innowise.enricherservice.service.SongDownloadService;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QueueListenerServiceImpl implements QueueListenerService {
    private static final String queueName = "file-api-queue";
    private final SqsClient sqsClient;
    private final SqsService sqsService;
    private final MetadataExtractorService metadataExtractorService;
    private final SongDownloadService songDownloadService;

    @GetMapping("/send")
    public ResponseEntity<String> hello() {
        try {
            sqsService.addIdInQueue(100L);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public void get() {
        String queueUrl = "http://localhost:4566/000000000000/file-api-queue";

        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .build();
        try {
            while (true) {
                ReceiveMessageResponse receiveResponse = sqsClient.receiveMessage(receiveRequest);
                List<Message> messages = receiveResponse.messages();
                if (!messages.isEmpty()) {
                    for (Message message : messages) {
                        String messageId = message.messageId();
                        String body = message.body();
                        System.out.println("Message ID: " + messageId);
                        System.out.println("Message Body: " + body);
                        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(message.receiptHandle())
                                .build();
                        try {
                            var file = songDownloadService.downloadFile(Long.valueOf(body));
                            metadataExtractorService.extractMetadataFromFile(file);
                        } catch (Exception e) {
                            log.error("Error while file parsing");
                            throw new InternalServerErrorException("Error while file parsing");
                        }
                        sqsClient.deleteMessage(deleteRequest);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
