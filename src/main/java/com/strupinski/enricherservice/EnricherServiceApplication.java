package com.strupinski.enricherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EnricherServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnricherServiceApplication.class, args);
    }
}
