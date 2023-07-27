package com.strupinski.enricherservice.service.impl;


import com.strupinski.enricherservice.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService{
    private final KafkaTemplate<Object, Object> template;

    @Override
    public HttpStatus send(String message) {
        try {
            log.info("producing message to Kafka, topic=file-api-topic");
            this.template.send("song-api-topic", message);
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.OK;
    }
}
