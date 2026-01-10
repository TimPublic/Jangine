package internal.input;


import internal.events.JangineEvent;
import internal.events.JangineEventHandler;
import internal.events.input.mouse.*;
import internal.main.JangineEngine;
import internal.rendering.JangineWindow;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;


// Listens on registered windows for mouse-events and pushes those to registered event-handlers.
// Those events are generated and pushed once per frame.
// The mouse-listener does not buffer events.
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


    // -+- CALLBACKS -+- //

    // Callback for when the cursor-position changes.
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
    // Callback for when a mouse-button is pressed.
    public void mouseButtonCallback(long windowPointer, int button, int action, int mods) {
        if (action == GLFW_PRESS && _isKnownMouseButton(button)) {
            _mouseButtonsPressed[button] = true;
        }
        else if (action == GLFW_RELEASE && _isKnownMouseButton(button)) {
            _mouseButtonsPressed[button] = false;
            _isDragging = false; // Could also be only if all are not pressed.
        }
    }
    // Callback for when a scroll happens.
    public void scrollCallback(long windowPointer, double xOffset, double yOffset) {
        _scrollOffsetX = xOffset;
        _scrollOffsetY = yOffset;
    }

    // Checks if the mouse-button is knows to the mouse-listener.
    // This mouse-listener is only set up with three mouse-buttons:
    // Left, right and middle.
    private boolean _isKnownMouseButton(int mouseButton) {
        return mouseButton < _mouseButtonsPressed.length;
    }

    // Sets the callback for a GLFW-window to the one of this mouse-listener and pushes
    // further events to its event-handler.
    public void setUpWindow(JangineWindow window) {
        _eventHandlers.add(window.getEventHandler());

        glfwSetCursorPosCallback(window.getPointer(), this::cursorPositionCallback);
        glfwSetMouseButtonCallback(window.getPointer(), this::mouseButtonCallback);
        glfwSetScrollCallback(window.getPointer(), this::scrollCallback);
    }
    // Sets the callback for a GLFW-window to an empty lambda and does not push
    // further events to its event-handler.
    public void removeWindow(JangineWindow window) {
        _eventHandlers.remove(window.getEventHandler());

        glfwSetCursorPosCallback(window.getPointer(), (long windowPointer, double xPos, double yPos) -> {});
        glfwSetMouseButtonCallback(window.getPointer(), (long windowPointer, int button, int action, int mods) -> {});
        glfwSetScrollCallback(window.getPointer(), (long windowPointer, double xOffset, double yOffset) -> {});
    }


    // -+- UPDATE-LOOP -+- //

    // Called on every frame.
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


    // -+- EVENT-MANAGEMENT -+- //

    // Adds an event-handler to this mouse-listener, that further events will be pushed to.
    public void addEventHandler(JangineEventHandler eventHandler) {
        _eventHandlers.add(eventHandler);
    }
    // Removes an event-handler from this mouse-listener, which will there not push further events
    // to this event-handler.
    public void rmvEventHandler(JangineEventHandler eventHandler) {
        _eventHandlers.remove(eventHandler);
    }

    public void setUpEngine() {
        _eventHandlers.add(JangineEngine.get().getEventHandler());
    }

    // Registers mouse-button events and calls respective functions to push those events.
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
            }
        }

        _prevMouseButtonsPressed = _mouseButtonsPressed.clone();
    }
    // Detects scroll-events and pushes them.
    private void _manageScrollEvent() {
        if (_scrollOffsetX == 0 && _scrollOffsetY == 0) {return;}

        _pushEvent(new JangineMouseScrollEvent(_scrollOffsetX, _scrollOffsetY));
    }
    // Detects drag-events and pushes them.
    private void _manageDragEvents() {
        if (_prevIsDragging && _isDragging) {
            _pushEvent(new JangineMouseDraggingEvent());
            return;
        }
        if (!_prevIsDragging && _isDragging) {
            _pushEvent(new JangineMouseDraggingStartedEvent());
            return;
        }
        if (_prevIsDragging) {
            _pushEvent(new JangineMouseDraggingEndedEvent());
        }
    }
    // Detects move-events and pushes them.
    private void _manageMoveEvent() {
        if (_prevY == _yPos && _prevX == _xPos) {return;}

        _pushEvent(new JangineMouseMovedEvent(_prevX, _prevY, _xPos, _yPos));
    }

    // Pushes a pressed-event for the given key.
    private void _pushPressed(int index) {
        _pushEvent(new JangineMouseButtonPressedEvent(index));
    }
    // Pushes a continued-event for the given key.
    private void _pushContinued(int index) {
        _pushEvent(new JangineMouseButtonContinuedEvent(index));
    }
    // Pushes a continued-event for the given key.
    private void _pushReleased(int index) {
        _pushEvent(new JangineMouseButtonReleasedEvent(index));
    }

    // Pushes the given event to the registered event-handlers.
    private void _pushEvent(JangineEvent event) {
        for (JangineEventHandler eventHandler : _eventHandlers) {
            eventHandler.pushEvent(event);
        }
    }


}