package com.innowise.enricherservice.client;

import com.innowise.enricherservice.model.SongData;
import org.springframework.http.ResponseEntity;

public interface SpotifyClient {

    ResponseEntity<String> search(SongData songData);
}
