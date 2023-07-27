package com.strupinski.enricherservice.client.impl;

import com.strupinski.enricherservice.client.SpotifyClient;
import com.strupinski.enricherservice.model.SongData;
import com.strupinski.enricherservice.service.SpotifyApiAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyClientImpl implements SpotifyClient {
    private static final String SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1";
    private static final String SEARCH_TRACKS_ENDPOINT = "/search";
    private static String currentToken;
    private final SpotifyApiAuthService spotifyAuthClient;
    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Override
    public ResponseEntity<String> search(SongData songData) {
        try {
            var restTemplate = new RestTemplate();
            var url = SPOTIFY_API_BASE_URL + SEARCH_TRACKS_ENDPOINT + "?q=" + songData.getTitle() + "&type=track" +
                    "&album=" + songData.getAlbum() + "&limit=1";
            var headers = new HttpHeaders();
            if (currentToken == null) {
                currentToken = spotifyAuthClient.getAccessToken(clientId, clientSecret);
            }

            headers.set("Authorization", "Bearer " + currentToken);
            var entity = new HttpEntity(headers);
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
        } catch (
                HttpClientErrorException.Unauthorized e) {
            log.error("Credentials are incorrect");
            return ResponseEntity.internalServerError().body("Spotify credentials are incorrect");
        }
    }
}
