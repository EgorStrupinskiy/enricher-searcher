package com.innowise.enricherservice.service.impl;

import com.innowise.enricherservice.service.OutputSqsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutputSqsServiceImpl implements OutputSqsService {

    private static final String queueName = "song-api-queue";
    private final SqsClient sqsClient;

    @Override
    public void addIdInQueue(String data) {
        GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();
        GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(getQueueUrlRequest);
        String queueUrl = getQueueUrlResponse.queueUrl();

        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(data)
                .build();

        sqsClient.sendMessage(sendMessageRequest);
        log.info("Message was uploaded in queue");
    }
}
