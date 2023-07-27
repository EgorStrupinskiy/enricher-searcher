package com.strupinski.enricherservice.service;

import org.springframework.kafka.annotation.KafkaListener;

public interface KafkaConsumerService {

    @KafkaListener(id = "file-api-group", topics = "file-api-topic")
    void listenFromReceivingTopic(String message);
}
