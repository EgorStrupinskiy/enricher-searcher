package com.strupinski.enricherservice.model;

import lombok.Data;

import java.io.InputStream;

@Data
public class DownloadedSong {

    private String id;

    private String key;

    private String fileName;

    private Long contentLength;

    private InputStream inputStream;
}
