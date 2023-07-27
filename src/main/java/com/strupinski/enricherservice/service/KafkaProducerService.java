package com.strupinski.enricherservice.service;

import org.springframework.http.HttpStatus;

public interface KafkaProducerService {
    HttpStatus send(String message);
}
