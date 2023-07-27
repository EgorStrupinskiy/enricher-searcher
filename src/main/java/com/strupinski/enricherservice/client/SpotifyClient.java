package com.strupinski.enricherservice.client;

import com.strupinski.enricherservice.model.SongData;
import org.springframework.http.ResponseEntity;

public interface SpotifyClient {
    ResponseEntity<String> search(SongData songData);
}
