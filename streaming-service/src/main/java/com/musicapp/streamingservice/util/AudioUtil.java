package com.musicapp.streamingservice.util;

import com.musicapp.streamingservice.exception.AudioProcessingException;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

@Component
public class AudioUtil {
    public boolean isMp3File(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        return contentType != null && contentType.equals("audio/mpeg") &&
                filename != null && filename.toLowerCase().endsWith(".mp3");
    }

    public long getMp3Duration(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            AudioInputStream audioInputStream = new MpegAudioFileReader().getAudioInputStream(inputStream);
            AudioFormat format = audioInputStream.getFormat();
            long frames = audioInputStream.getFrameLength();
            double durationInSeconds = (frames + 0.0) / format.getFrameRate();
            return (long) durationInSeconds;
        } catch (IOException | UnsupportedAudioFileException e) {
            throw new AudioProcessingException(e);
        }
    }
}
