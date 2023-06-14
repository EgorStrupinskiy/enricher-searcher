package com.innowise.enricherservice.service.impl;

import com.innowise.enricherservice.model.SongData;
import com.innowise.enricherservice.service.MetadataExtractorService;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataExtractorServiceImpl implements MetadataExtractorService {

    @Override
    public SongData extractMetadataFromFile(File file) {
        try {
            var audioFile = AudioFileIO.read(file);
            var title = audioFile.getTag().getFirst(FieldKey.TITLE);
            var album = audioFile.getTag().getFirst(FieldKey.ALBUM);

            return new SongData(title, album);
        } catch (Exception e) {
            throw new BadRequestException("Error while parsing song mp3 file");
        }
    }


}
