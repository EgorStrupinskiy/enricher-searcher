package com.innowise.enricherservice.service;

import org.springframework.stereotype.Service;

@Service
public interface OutputSqsService {
    void addIdInQueue(String data);
}
