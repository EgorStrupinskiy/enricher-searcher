package com.innowise.enricherservice.service.impl;

import com.innowise.enricherservice.service.DiscoveryService;
import com.innowise.enricherservice.service.SongDownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.innowise.enricherservice.util.FileUtils.extractFileExtension;
import static com.innowise.enricherservice.util.FileUtils.extractFilenameFromContentDisposition;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongDownloadServiceImpl implements SongDownloadService {
    private final DiscoveryService discoveryService;
    private final String FILE_API_NAME = "FILE-API";

    @Override
    public File downloadFile(Long id) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        var url = discoveryService.getMicroserviceUrl(FILE_API_NAME) + "/files/download/" + id;
        var response = restTemplate.getForEntity(url, byte[].class);
        var fileBytes = response.getBody();

        if (fileBytes != null) {
            HttpHeaders headers = response.getHeaders();
            String contentDisposition = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
            String filename = extractFilenameFromContentDisposition(contentDisposition);
            String fileExtension = extractFileExtension(filename);

            File tempFile = File.createTempFile("temp", fileExtension);
            Files.write(tempFile.toPath(), fileBytes);

            log.info("File downloaded");
            return tempFile;
        } else {
            log.error("File is empty");
            throw new IllegalStateException("Received null response body");
        }
    }


}
