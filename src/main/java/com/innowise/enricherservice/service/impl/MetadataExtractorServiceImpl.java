package com.innowise.enricherservice.service.impl;

import com.innowise.enricherservice.service.MetadataExtractorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataExtractorServiceImpl implements MetadataExtractorService {

    private static final String SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1";
    private static final String SEARCH_TRACKS_ENDPOINT = "/search";
    private static final Integer MAX_ITERATIONS = 50;

    @Override
    public ResponseEntity<String> extractMetadataFromFile(File file) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            AudioFile audioFile = AudioFileIO.read(file);

            String title = audioFile.getTag().getFirst(FieldKey.TITLE);
            String album = audioFile.getTag().getFirst(FieldKey.ALBUM);

            String url = SPOTIFY_API_BASE_URL + SEARCH_TRACKS_ENDPOINT + "?q=" + title + "&type=track" +
                    "&album=" + album;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + "BQB4j14PzpjZ1RCIbiAEGi2VaL7-okXKbNPkImZJhUaVdQun2TpZOaIDHFRzR5rZ8xcrFcOSIX-GSr5Gh9TYjZmBykMs-_Mm9yU1bfX4SaS7fXxVG1Y");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            System.out.println(url);
            var response = new ResponseEntity<String>(HttpStatus.NOT_FOUND);
            var iterator = 0;
            while (response.getStatusCode() == HttpStatus.NOT_FOUND && iterator < MAX_ITERATIONS) {
                try {
                    response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                } catch (Exception e) {
                    iterator++;
                    log.error("Song was not found in Spotify, one more attempt");
                }
            }
            return response;
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error while tracing song mp3 file");
        }
    }
}
