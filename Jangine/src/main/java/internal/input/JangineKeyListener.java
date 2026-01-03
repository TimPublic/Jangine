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


    private JangineKeyListener() {
        _keyPressedBuffer = new HashSet<>();
        _prevKeyPressedBuffer = new HashSet<>();
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
                get()._keyPressedBuffer.add(key);
                break;
            case GLFW_RELEASE:
                get()._keyPressedBuffer.remove(key);
                break;
        }
    }


    public void endFrame() {
        _manageKeyEvents();
    }


    private void _manageKeyEvents() {
        int sizeDifference; // The difference in size, of the two buffers.
        int optimizationCounter; // Counts how many pressed events where already pushed, to be checked against the size difference.

        sizeDifference = _keyPressedBuffer.size() - _prevKeyPressedBuffer.size();
        optimizationCounter = 0;

        for (Integer key : _keyPressedBuffer) {
            if (optimizationCounter >= sizeDifference) {break;}
            if (_prevKeyPressedBuffer.contains(key)) {continue;}

            _pushPressed(key);
        }
        for (Integer key : _prevKeyPressedBuffer) {
            if (_keyPressedBuffer.contains(key)) {
                _pushContinued(key);
            } else {
                _pushPressed(key);
            }
        }
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