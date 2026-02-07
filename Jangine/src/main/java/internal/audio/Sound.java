package internal.audio;


import internal.resource.A_Resource;
import internal.util.FormatChecker;


public class Sound extends A_Resource {


    // -+- CREATION -+- //

    public Sound(String path) {
        super(path);

        FormatChecker.assertFormat(path, ".wav");

        _SOURCE_ID = h_genSource();
    }

    @Override
    protected void p_dispose() {

    }

    private long h_genSource() {
        return -1L;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final long _SOURCE_ID;


    // -+- SOUND MANAGEMENT -+- //

    public void play() {

    }
    public void stop() {

    }


    // -+- GETTERS -+- //

    public int getLenghtMin() {
        return -1;
    }


}