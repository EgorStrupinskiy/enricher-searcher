package com.innowise.enricherservice.service;

import org.springframework.stereotype.Service;

@Service
public interface SpotifyApiAuthService {
    String getAccessToken(String clientId, String clientSecret);
}
