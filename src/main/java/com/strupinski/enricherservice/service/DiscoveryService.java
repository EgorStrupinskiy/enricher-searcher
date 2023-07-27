package com.strupinski.enricherservice.service;

import org.springframework.stereotype.Service;

@Service
public interface DiscoveryService {
    String getMicroserviceUrl(String name);
}
