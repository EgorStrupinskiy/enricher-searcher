package com.innowise.enricherservice.service;

import com.innowise.enricherservice.model.SongData;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public interface MetadataExtractorService {
    SongData extractMetadataFromFile(File file);
}
