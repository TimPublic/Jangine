package internal.input;


import internal.events.JangineEvent;
import internal.events.JangineEventHandler;
import internal.events.input.mouse.*;
import internal.rendering.JangineWindow;
import internal.util.JangineLogger;

import java.util.ArrayList;
import java.util.EventListener;

import static org.lwjgl.glfw.GLFW.*;


public class JangineMouseListener {


    private ArrayList<JangineEventHandler> _eventHandlers;


    private double _xPos, _yPos;
    private double _prevX, _prevY;

    private double _scrollOffsetX, _scrollOffsetY;

    private boolean _isDragging;
    private boolean _prevIsDragging;

    private boolean[] _mouseButtonsPressed = new boolean[3];
    private boolean[] _prevMouseButtonsPressed = new boolean[3];


    public JangineMouseListener() {
        _eventHandlers = new ArrayList<>();

        _xPos = 0.0;
        _yPos = 0.0;
        _prevX = 0.0;
        _prevY = 0.0;
    }


    public void cursorPositionCallback(long windowPointer, double xPos, double yPos) {
        _xPos = xPos;
        _yPos = yPos;

        // If the mouse has moved AND any button is down, a drag is happening.
        for (int index = 0; index < _mouseButtonsPressed.length; index++) {
            if (!_mouseButtonsPressed[index]) {continue;}

            _isDragging = true;

            break;
        }
    }

    public void mouseButtonCallback(long windowPointer, int button, int action, int mods) {
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

    public void scrollCallback(long windowPointer, double xOffset, double yOffset) {
        _scrollOffsetX = xOffset;
        _scrollOffsetY = yOffset;
    }


    public void update() {
        _scrollOffsetX = 0.0;
        _scrollOffsetY = 0.0;

        _prevX = _xPos;
        _prevY = _yPos;

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
        _pushEvent(new JangineMouseButtonPressedEvent(index));
    }
    private void _pushContinued(int index) {
        _pushEvent(new JangineMouseButtonContinuedEvent(index));
    }
    private void _pushReleased(int index) {
        _pushEvent(new JangineMouseButtonReleasedEvent(index));
    }

    private void _manageScrollEvent() {
        if (_scrollOffsetX == 0 && _scrollOffsetY == 0) {return;}

        _pushEvent(new JangineMouseScrollEvent(_scrollOffsetX, _scrollOffsetY));
    }

    private void _manageDragEvents() {
        if (_prevIsDragging && _isDragging) {
            _pushEvent(new JangineMouseDraggingEvent());
            return;
        }
        if (!_prevIsDragging && _isDragging) {
            _pushEvent(new JangineMouseDraggingStartedEvent());
            return;
        }
        if (_prevIsDragging && !_isDragging) {
            _pushEvent(new JangineMouseDraggingEndedEvent());
            return;
        }
    }

    private void _manageMoveEvent() {
        if (_prevY == _yPos && _prevX == _xPos) {return;}

        _pushEvent(new JangineMouseMovedEvent(_prevX, _prevY, _xPos, _yPos));
    }


    private void _pushEvent(JangineEvent event) {
        for (JangineEventHandler eventHandler : _eventHandlers) {
            eventHandler.pushEvent(event);
        }
    }


    public void setUpWindow(JangineWindow window) {
        _eventHandlers.add(window.getEventHandler());

        glfwSetCursorPosCallback(window.getPointer(), this::cursorPositionCallback);
        glfwSetMouseButtonCallback(window.getPointer(), this::mouseButtonCallback);
        glfwSetScrollCallback(window.getPointer(), this::scrollCallback);
    }
    public void removeWindow(JangineWindow window) {
        _eventHandlers.remove(window.getEventHandler());

        glfwSetCursorPosCallback(window.getPointer(), (long windowPointer, double xPos, double yPos) -> {});
        glfwSetMouseButtonCallback(window.getPointer(), (long windowPointer, int button, int action, int mods) -> {});
        glfwSetScrollCallback(window.getPointer(), (long windowPointer, double xOffset, double yOffset) -> {});
    }

    public void addEventHandler(JangineEventHandler eventHandler) {
        _eventHandlers.add(eventHandler);
    }
    public void rmvEventHandler(JangineEventHandler eventHandler) {
        _eventHandlers.remove(eventHandler);
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