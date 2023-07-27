package com.strupinski.enricherservice.service.impl;

import com.strupinski.enricherservice.service.OutputSqsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutputSqsServiceImpl implements OutputSqsService {

    private static final String queueName = "song-api-queue";
    private final SqsClient sqsClient;

    @Override
    public void addIdInQueue(String data) {
        try {
            GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();
            GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(getQueueUrlRequest);
            String queueUrl = getQueueUrlResponse.queueUrl();
            sendMessageToQueue(queueUrl, data);
        } catch (QueueDoesNotExistException e) {
            CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();
            String queueUrl = sqsClient.createQueue(createQueueRequest).queueUrl();
            sendMessageToQueue(queueUrl, data);
        }
        log.info("Message was uploaded in queue");
    }

    private void sendMessageToQueue(String queueUrl, String data) {
        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(data)
                .build();

        sqsClient.sendMessage(sendMessageRequest);
    }
}
