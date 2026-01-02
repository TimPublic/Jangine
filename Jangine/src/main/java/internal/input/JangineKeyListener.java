package internal.input;


import internal.util.JangineLogger;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;


public class JangineKeyListener {


    private static JangineKeyListener _instance;


    private HashSet<Integer> _keyPressedBuffer;
    private HashSet<Integer> _prevKeyPressedBuffer;
    private HashSet<Integer> _framePressedBuffer;
    private HashSet<Integer> _frameReleasedBuffer;


    private JangineKeyListener() {
        _keyPressedBuffer = new HashSet<>();
        _prevKeyPressedBuffer = new HashSet<>();
        _framePressedBuffer = new HashSet<>();
        _frameReleasedBuffer = new HashSet<>();
    }

    public static JangineKeyListener get() {
        if (_instance == null) {
            _instance = new JangineKeyListener();
        }

        return _instance;
    }


    public static void keyCallback(long windowPointer, int key, int scanCode, int action, int mods) {
        switch (action) {
            case GLFW_PRESS:
                get()._framePressedBuffer.add(key);
                get()._frameReleasedBuffer.remove(key); // Prevents multiple actions per frame.
                break;
            case GLFW_RELEASE:
                get()._frameReleasedBuffer.add(key);
                get()._framePressedBuffer.remove(key); // Prevents multiple actions per frame.
                break;
        }
    }


    public void endFrame() {
        _manageKeyEvents();
    }


    private void _manageKeyEvents() {
        // System.out.println(_keyPressedBuffer);
        for (Integer key : _framePressedBuffer) {
            if (!_keyPressedBuffer.add(key)) {continue;}

            _pushPressed(key);
        }

        for (Integer key : _frameReleasedBuffer) {
            if (!_keyPressedBuffer.remove(key)) {continue;}

            _pushReleased(key);
        }

        for (Integer key : _prevKeyPressedBuffer) {
            if (!_keyPressedBuffer.contains(key)) {continue;}

            _pushContinued(key);
        }

        _prevKeyPressedBuffer = (HashSet<Integer>) _keyPressedBuffer.clone();
        _framePressedBuffer.clear();
        _frameReleasedBuffer.clear();
    }

    private void _pushPressed(Integer key) {
        JangineLogger.get().log("PRESS");
    }
    private void _pushContinued(Integer key) {
        JangineLogger.get().log("CONTINUED");
    }
    private void _pushReleased(Integer key) {
        JangineLogger.get().log("RELEASED");
    }


    public boolean isButtonPressed(int button) {
        return _keyPressedBuffer.contains(button);
    }


}