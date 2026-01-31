package internal.audio;


import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

public class AudioUtil {


    public static String[] getValidDevices() {
        String rawList;

        rawList = alcGetString(MemoryUtil.NULL, ALC_DEVICE_SPECIFIER);

        return rawList.split("\0");
    }

    public static long createDevice(String deviceIdentifier) {
        long id;

        id = alcOpenDevice(deviceIdentifier);
        if (id == 0) throw new IllegalStateException("[AUDIO UTIL ERROR] : Could not open id!\n"
        + "|-> Device Identifier : " + deviceIdentifier);

        return id;
    }
    public static long createDevice() {
        long id;

        id = alcOpenDevice((ByteBuffer) null);
        if (id == 0) throw new IllegalStateException("[AUDIO UTIL ERROR] : Could not open id!\n"
                + "|-> Device Identifier : default");

        return id;
    }

    public static HashMap<String, ?> getPCM(String filePath) {
        /*
        openAL requires:
        - pcm (ByteBuffer)
        - format
        - sampleRate
        - channels
         */

        // Receive format.
        AudioInputStream stream;
        AudioFormat sourceFormat;

        try {
            stream = AudioSystem.getAudioInputStream(new File(filePath));
            sourceFormat = stream.getFormat();
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }

        AudioFormat targetFormat;

        targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(),
                16,
                sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(),
                false
        );

        try {
            stream = AudioSystem.getAudioInputStream(targetFormat, stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        AudioFormat format;

        format = stream.getFormat();

        // Receive pcm data.
        byte[] pcmData;

        pcmData = new byte[(int) (format.getFrameSize() * stream.getFrameLength())];

        // Read data into pcm data.
        int totalRead;
        int bytesRead;

        totalRead = 0;
        
        try {
            while ((bytesRead = stream.read(pcmData, totalRead, pcmData.length - totalRead)) != -1) {
                totalRead += bytesRead;

                if (totalRead >= pcmData.length) break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Receive sample rate.
        float sampleRate;

        sampleRate = format.getSampleRate();

        // Get OpenAL format.
        int alFormat;

        switch (format.getChannels()) {
            case 1:
                alFormat = switch (format.getSampleSizeInBits()) {
                    case 8 -> AL_FORMAT_MONO8;
                    case 16 -> AL_FORMAT_MONO16;
                    default -> throw new RuntimeException("Only a sample size of 8 or 16 is supported!");
                };
                break;
            case 2:
                alFormat = switch (format.getSampleSizeInBits()) {
                    case 8 -> AL_FORMAT_STEREO8;
                    case 16 -> AL_FORMAT_STEREO16;
                    default -> throw new RuntimeException("Only a sample size of 8 or 16 is supported!");
                };
                break;
            default:
                throw new RuntimeException("Only one or two channels are supported!");
        }

        return new HashMap<>(Map.of(
                "PCM", BufferUtils.createByteBuffer(pcmData.length).put(pcmData).flip(),
                "FORMAT", format,
                "SAMPLE RATE", sampleRate,
                "CHANNELS", format.getChannels()
        ));
    }


}