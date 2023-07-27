package com.strupinski.enricherservice.service.impl;

import com.strupinski.enricherservice.client.SpotifyClient;
import com.strupinski.enricherservice.service.KafkaConsumerService;
import com.strupinski.enricherservice.service.KafkaProducerService;
import com.strupinski.enricherservice.service.MetadataExtractorService;
import com.strupinski.enricherservice.service.SongDownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final MetadataExtractorService metadataExtractorService;
    private final SongDownloadService songDownloadService;
    private final SpotifyClient spotifyClient;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public void listenFromReceivingTopic(String message) {
        log.info("Received: " + message);
        log.info("Message received");

        try {
            var file = songDownloadService.downloadFile(Long.valueOf(message));
            var songData = metadataExtractorService.extractMetadataFromFile(file);
            var apiResponse = spotifyClient.search(songData);
            kafkaProducerService.send(apiResponse.getBody());
            log.info("Song info from spotify:");
            log.info(apiResponse.getBody());
        } catch (Exception e) {
            log.error("Error while file processing");
            e.printStackTrace();
        }
    }
}
