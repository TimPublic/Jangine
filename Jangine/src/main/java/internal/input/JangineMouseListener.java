package internal.input;


import internal.util.JangineLogger;

import static org.lwjgl.glfw.GLFW.*;

public class JangineMouseListener {


    private static JangineMouseListener _instance;


    private double _xPos, _yPos;
    private double _prevX, _prevY;

    private double _scrollOffsetX, _scrollOffsetY;

    private boolean _isDragging;
    private boolean _prevIsDragging;

    private boolean[] _mouseButtonsPressed = new boolean[3];
    private boolean[] _prevMouseButtonsPressed = new boolean[3];


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


    public static void cursorPositionCallback(long windowPointer, double xPos, double yPos) {
        get()._prevX = get()._xPos;
        get()._prevY = get()._yPos;
        get()._xPos = xPos;
        get()._yPos = yPos;

        // If the mouse has moved AND any button is down, a drag is happening.
        for (int index = 0; index < get()._mouseButtonsPressed.length; index++) {
            if (!get()._mouseButtonsPressed[index]) {continue;}

            get()._isDragging = true;

            break;
        }
    }

    public static void mouseButtonCallback(long windowPointer, int button, int action, int mods) {
        if (action == GLFW_PRESS && _isKnownMouseButton(button)) {
            get()._mouseButtonsPressed[button] = true;
        }
        else if (action == GLFW_RELEASE && _isKnownMouseButton(button)) {
            get()._mouseButtonsPressed[button] = false;
            get()._isDragging = false; // Could also be only if all are not pressed.
        }
    }
    private static boolean _isKnownMouseButton(int mouseButton) {
        return mouseButton < get()._mouseButtonsPressed.length;
    }

    public static void scrollCallback(long windowPointer, double xOffset, double yOffset) {
        get()._scrollOffsetX = xOffset;
        get()._scrollOffsetY = yOffset;
    }


    public void endFrame() {
        _scrollOffsetX = 0.0;
        _scrollOffsetY = 0.0;

        _prevX = 0.0;
        _prevY = 0.0;

        _prevIsDragging = _isDragging;

        _manageButtonEvents();
        _manageScrollEvent();
        _manageDragEvents();
        _manageMoveEvent();
    }

    private void _manageButtonEvents() {
        for (int index = 0; index < _mouseButtonsPressed.length; index++) {
            if (!_prevMouseButtonsPressed[index] && _mouseButtonsPressed[index]) {
                _pushPressed(index);
                continue;
            }
            if (_prevMouseButtonsPressed[index] && _mouseButtonsPressed[index]) {
                _pushContinued(index);
                continue;
            }
            if (_prevMouseButtonsPressed[index] && !_mouseButtonsPressed[index]) {
                _pushReleased(index);
                continue;
            }
        }

        _prevMouseButtonsPressed = _mouseButtonsPressed.clone();
    }

    private void _pushPressed(int index) {

    }
    private void _pushContinued(int index) {

    }
    private void _pushReleased(int index) {

    }

    private void _manageScrollEvent() {

    }

    private void _manageDragEvents() {

    }

    private void _manageMoveEvent() {

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