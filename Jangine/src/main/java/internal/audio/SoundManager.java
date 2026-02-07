package internal.audio;


import internal.util.interfaces.I_Loader;

import java.util.HashMap;


public class SoundManager implements I_Loader<Sound> {


    // -+- CREATION -+- //

    public SoundManager() {
        _SOUNDS = new HashMap<>();
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final HashMap<String, Sound> _SOUNDS;


    // -+- SOUND MANAGEMENT -+- //

    // LOADING //

    public Sound load(String path) {
        Sound sound;

        sound = _SOUNDS.get(path);
        if (sound == null) {
            sound = new Sound(path);
            _SOUNDS.put(path, sound);
        }

        return sound;
    }

    // ADDITION AND REMOVAL //

    public boolean add(String path) {
        load(path);

        return true;
    }
    public boolean add(Sound sound) {
        String path;
        Sound foundSound;

        path = sound.getPath();
        foundSound = _SOUNDS.get(path);

        if (foundSound == null) {
            _SOUNDS.put(path, sound);

            return true;
        }

        return foundSound == sound;
    }

    public boolean rmv(String path) {
        return (_SOUNDS.remove(path) != null);
    }
    public boolean rmv(Sound sound) {
        return _SOUNDS.remove(sound.getPath(), sound);
    }


}