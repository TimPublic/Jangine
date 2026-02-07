package internal.audio;


import internal.util.FormatChecker;


public class Sound {


    // -+- CREATION -+- //

    public Sound(String path) {
        FormatChecker.assertFormat(path, ".wav");

        _SOURCE_ID = h_genSource();
        _PATH = path;
    }

    private long h_genSource() {
        return -1L;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final long _SOURCE_ID;
    private final String _PATH;


    // -+- SOUND MANAGEMENT -+- //

    public void play() {

    }
    public void stop() {

    }


    // -+- GETTERS -+- //

    public int getLenghtMin() {
        return -1;
    }

    public String getPath() {
        return _PATH;
    }


}