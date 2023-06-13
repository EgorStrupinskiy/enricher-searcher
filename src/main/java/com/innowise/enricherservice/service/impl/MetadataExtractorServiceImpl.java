package com.innowise.enricherservice.service.impl;

import com.innowise.enricherservice.service.MetadataExtractorService;
import com.innowise.enricherservice.service.SpotifyApiAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataExtractorServiceImpl implements MetadataExtractorService {

    private static final String SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1";
    private static final String SEARCH_TRACKS_ENDPOINT = "/search";
//    private static final Integer MAX_ITERATIONS = 50;
    private static String currentToken;
    private final SpotifyApiAuthService spotifyAuthClient;
    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Override
    public ResponseEntity<String> extractMetadataFromFile(File file) {
        try {
            var restTemplate = new RestTemplate();

            var audioFile = AudioFileIO.read(file);
            var title = audioFile.getTag().getFirst(FieldKey.TITLE);
            var album = audioFile.getTag().getFirst(FieldKey.ALBUM);
            var url = SPOTIFY_API_BASE_URL + SEARCH_TRACKS_ENDPOINT + "?q=" + title + "&type=track" +
                    "&album=" + album + "&limit=1";
            var headers = new HttpHeaders();
            if (currentToken == null) {
                currentToken = spotifyAuthClient.getAccessToken(clientId, clientSecret);
            }

            headers.set("Authorization", "Bearer " + currentToken);
            var entity = new HttpEntity<>(headers);
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            try {
                response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            } catch (HttpClientErrorException.Unauthorized e) {
                currentToken = spotifyAuthClient.getAccessToken(clientId, clientSecret);

                headers.clear();
                log.error("Token is incorrect, trying to get new");
                headers.set("Authorization", "Bearer " + currentToken);
            }
            return response;
//            var response = new ResponseEntity<String>(HttpStatus.NOT_FOUND);

//            var iterator = 0;
//            while (response.getStatusCode() == HttpStatus.NOT_FOUND && iterator < MAX_ITERATIONS) {
//                try {
//                    response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//                } catch (HttpClientErrorException.Unauthorized e) {
//                    currentToken = spotifyAuthClient.getAccessToken(clientId, clientSecret);
//
//                    headers.clear();
//                    log.error("Token is incorrect, trying to get new");
//                    headers.set("Authorization", "Bearer " + currentToken);
//                    entity = new HttpEntity<>(headers);
//
//                    log.error("Trying to get new spotify token [" + iterator + "/" + MAX_ITERATIONS + "]");
//                } catch (Exception e) {
//                    iterator++;
//                    log.error("Song was not found in Spotify, trying once more [" + iterator + "/" + MAX_ITERATIONS + "]");
//                }
//            }
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Credentials are incorrect");
            return ResponseEntity.internalServerError().body("Spotify credentials are incorrect");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error while tracing song mp3 file");
        }
    }


}
