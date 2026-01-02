package internal.input;


import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;


public class JangineKeyListener {


    private static JangineKeyListener _instance;


    private Set<Integer> _keyPressedBuffer;
    private Set<Integer> _framePressedBuffer;
    private Set<Integer> _frameReleasedBuffer;


    private JangineKeyListener() {

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
                get()._keyPressedBuffer.remove(key); // Prevents multiple actions per frame.
                break;
        }
    }


    public void endFrame() {
        _manageKeyEvents();
    }


    private void _manageKeyEvents() {
        for (Integer key : _framePressedBuffer) {
            if (_keyPressedBuffer.add(key)) {
                _pushPressed(key);
                continue;
            }

            _pushContinued(key);
        }

        for (Integer key : _frameReleasedBuffer) {
            if (!_keyPressedBuffer.remove(key)) {continue;}

            _pushReleased(key);
        }
    }

    private void _pushPressed(Integer key) {

    }
    private void _pushContinued(Integer key) {

    }
    private void _pushReleased(Integer key) {

    }


    public boolean isButtonPressed(int button) {
        return _keyPressedBuffer.contains(button);
    }


}