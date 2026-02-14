package internal.audio;


import internal.resource.A_Resource;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;


public class Sound extends A_Resource {


    // -+- CREATION -+- //

    public Sound(String path, boolean loops) {
        super(path);

        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(getPath(), channelsBuffer, sampleRateBuffer);

        if (rawAudioBuffer == null) {
            stackPop();
            stackPop();

            throw new IllegalStateException("[SOUND ERROR] : Could not load sound with the specified file path!\n" +
                    "|-> File Path : " + getPath());
        }

        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();

        stackPop();
        stackPop();

        int format = -1;

        switch (channels) {
            case 1 -> format = AL_FORMAT_MONO16;
            case 2 -> format = AL_FORMAT_STEREO16;
        }

        _BUFFER_ID = alGenBuffers();
        alBufferData(_BUFFER_ID, format, rawAudioBuffer, sampleRate);

        _SOURCE_ID = alGenSources();
        alSourcei(_SOURCE_ID, AL_BUFFER, _BUFFER_ID);
        alSourcei(_SOURCE_ID, AL_LOOPING, loops ? 1 : 0);
        alSourcei(_SOURCE_ID, AL_POSITION, 0);
        alSourcef(_SOURCE_ID, AL_GAIN, 0.3f);

        free(rawAudioBuffer);
    }

    @Override
    protected void p_dispose() {
        alDeleteBuffers(_BUFFER_ID);
        alDeleteSources(_SOURCE_ID);
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final int _BUFFER_ID;
    private final int _SOURCE_ID;

    // NON-FINALS //

    private boolean _isPlaying;


    // -+- SOUND MANAGEMENT -+- //

    public void play() {
        int state = alGetSourcei(_SOURCE_ID, AL_SOURCE_STATE);

        if (state == AL_STOPPED) {
            _isPlaying = false;

            alSourcei(_SOURCE_ID, AL_POSITION, 0);
        }

        if (!_isPlaying) {
            _isPlaying = true;

            alSourcePlay(_SOURCE_ID);
        }
    }
    public void stop() {
        _isPlaying = false;

        alSourceStop(_SOURCE_ID);
    }


    // -+- CHECKERS -+- //

    public boolean isPlaying() {
        int state = alGetSourcei(_SOURCE_ID, AL_SOURCE_STATE);

        if (state == AL_STOPPED) {
            _isPlaying = false;
        }

        return _isPlaying;
    }


}