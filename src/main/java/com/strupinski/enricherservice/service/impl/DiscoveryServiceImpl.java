package com.strupinski.enricherservice.service.impl;

import com.strupinski.enricherservice.service.DiscoveryService;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscoveryServiceImpl implements DiscoveryService {
    private final DiscoveryClient discoveryClient;

    @Override
    public String getMicroserviceUrl(String name) {
        log.info("Searching for a microservice with name: " + name);
        var instances = discoveryClient.getInstances(name);
        if (instances.isEmpty()) {
            log.info("No such microservice");
            throw new InternalServerErrorException("Service with name " + name + " is unavailable");
        }
        var instance = instances.get(0);
        var url = "http://" + instance.getHost() + ":" + instance.getPort();
        log.info("Url for this microservice access: " + url);
        return url;
    }

}
