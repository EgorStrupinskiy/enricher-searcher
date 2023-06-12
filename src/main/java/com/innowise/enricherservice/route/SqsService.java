package com.innowise.enricherservice.route;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsService {
//    @Override
//    public void configure() throws Exception {
//        from("aws2-sqs://file-api-queue")
//                .log("Received Message: ${body}")
//                .process(exchange -> {
//                    String message = exchange.getIn().getBody(String.class);
//                    System.out.println(message);
//                    // Ваша логика обработки сообщения
//                    // ...
//                    // Опционально: Можете установить новое значение сообщения перед его возвратом
//                    exchange.getIn().setBody("Новое сообщение");
//                })
//                .to("log:output");
//    }
    private static final String queueName = "file-api-queue";
    private final SqsClient sqsClient;

    public void addIdInQueue(Long id) {
        GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();
        GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(getQueueUrlRequest);
        String queueUrl = getQueueUrlResponse.queueUrl();
        if (queueUrl == null) {
            CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();
            CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);
            queueUrl = createQueueResponse.queueUrl();
            log.info("New queue created: " + queueUrl);
        } else {
            log.info("Queue already exists: " + queueUrl);
        }

        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(id.toString())
                .build();

        sqsClient.sendMessage(sendMessageRequest);
        log.info("Message was uploaded in queue");
    }
}
