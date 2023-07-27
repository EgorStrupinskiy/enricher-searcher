package com.strupinski.enricherservice.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public interface SongDownloadService {
    File downloadFile(Long id) throws IOException;
}
