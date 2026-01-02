package internal.input;


import internal.util.JangineLogger;

import static org.lwjgl.glfw.GLFW.*;

public class JangineMouseListener {


    private static JangineMouseListener _instance;


    private double _xPos, _yPos;
    private double _prevX, _prevY;

    private double _scrollOffsetX, _scrollOffsetY;

    private boolean _isDragging;

    private boolean _mouseButtonsPressed[] = new boolean[3];


    private JangineMouseListener() {
        _xPos = 0.0;
        _yPos = 0.0;
        _prevX = 0.0;
        _prevY = 0.0;
    }

    public static JangineMouseListener get() {
        if (_instance == null) {
            _instance = new JangineMouseListener();
        }

        return _instance;
    }


    private void _cursorPositionCallback(long windowPointer, double xPos, double yPos) {
        _prevX = _xPos;
        _prevY = _yPos;
        _xPos = xPos;
        _yPos = yPos;

        // If the mouse has moved AND any button is down, a drag is happening.
        for (int index = 0; index < _mouseButtonsPressed.length; index++) {
            if (!_mouseButtonsPressed[index]) {continue;}

            _isDragging = true;

            break;
        }
    }

    private void _mouseButtonCallback(long windowPointer, int button, int action, int mods) {
        if (action == GLFW_PRESS && _isKnownMouseButton(button)) {
            _mouseButtonsPressed[button] = true;
        }
        else if (action == GLFW_RELEASE && _isKnownMouseButton(button)) {
            _mouseButtonsPressed[button] = false;
            _isDragging = false; // Could also be only if all are not pressed.
        }
    }
    private boolean _isKnownMouseButton(int mouseButton) {
        return mouseButton < _mouseButtonsPressed.length;
    }

    private void _scrollCallback(long windowPointer, double xOffset, double yOffset) {
        _scrollOffsetX = xOffset;
        _scrollOffsetY = yOffset;
    }


    public void endFrame() {
        _scrollOffsetX = 0.0;
        _scrollOffsetY = 0.0;

        _prevX = 0.0;
        _prevY = 0.0;
    }

    public double getX() {
        return _xPos;
    }
    public double getY() {
        return _yPos;
    }

    public double getDeltaX() {
        return _prevX - _xPos;
    }
    public double getDeltaY() {
        return _prevY - _yPos;
    }

    public double getScrollX() {
        return _scrollOffsetX;
    }
    public double getScrollY() {
        return _scrollOffsetY;
    }

    public boolean isButtonPressed(int button) {
        if (button >= _mouseButtonsPressed.length) {
            JangineLogger.get().logSafe("Tried to access invalid mouse button! (" + button + ")");
            return false;
        }

        return _mouseButtonsPressed[button];
    }


}