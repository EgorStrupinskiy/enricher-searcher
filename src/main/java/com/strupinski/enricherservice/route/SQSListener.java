package com.strupinski.enricherservice.route;

import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;

@Component
@Configuration
public class SQSListener {
    private static final String queueUrl = "file-api-queue";
    private final SqsClient sqsClient;

    public SQSListener(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @SqsListener(value = "file-api-queue")
    public void startListening() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .build();

        ReceiveMessageResponse receiveResponse = sqsClient.receiveMessage(receiveRequest);

        List<Message> messages = receiveResponse.messages();
        for (Message message : messages) {

            String messageId = message.messageId();
            String body = message.body();
            System.out.println("Message ID: " + messageId);
            System.out.println("Message Body: " + body);
        }
    }
}
