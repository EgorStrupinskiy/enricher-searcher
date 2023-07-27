package com.strupinski.enricherservice.service;

import com.strupinski.enricherservice.model.SongData;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public interface MetadataExtractorService {
    SongData extractMetadataFromFile(File file);
}
