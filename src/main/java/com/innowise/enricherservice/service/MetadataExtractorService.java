package com.innowise.enricherservice.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public interface MetadataExtractorService {

    ResponseEntity<String> extractMetadataFromFile(File file);
}
