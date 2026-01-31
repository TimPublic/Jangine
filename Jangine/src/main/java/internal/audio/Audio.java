package internal.audio;


import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;

public class Audio {


    public Audio(long contextId, String filePath) {
        _CONTEXT_ID = contextId;
        _FILE_PATH = filePath;

        _SOURCE_ID = alGenSources();
    }


    private final long _CONTEXT_ID;
    private final String _FILE_PATH;

    private final int _SOURCE_ID;


    public void play() {
        alcMakeContextCurrent(_CONTEXT_ID);

        alSourcePlay(_SOURCE_ID);
    }


    public String getPath() {
        return _FILE_PATH;
    }


}