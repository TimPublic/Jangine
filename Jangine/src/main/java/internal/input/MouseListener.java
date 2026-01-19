package internal.input;


import internal.events.Event;
import internal.events.EventHandler;
import internal.events.input.mouse.*;
import internal.main.Engine;
import internal.rendering.container.Window;

import java.util.ArrayList;
import java.util.HashSet;

import static org.lwjgl.glfw.GLFW.*;


/**
 * Listens for mouse events in set-up windows, converts them to {@link MouseEvent} and pushes them to registered event handlers.
 * The mouse listener does not buffer events.
 * <p>
 * Currently, the mouse listener only supports three buttons:
 * Left, Right, Middle
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class MouseListener {


    private ArrayList<EventHandler> _eventHandlers;


    private double _xPos, _yPos;
    private double _prevX, _prevY;

    private double _scrollOffsetX, _scrollOffsetY;

    private boolean _isDragging;
    private boolean _prevIsDragging;

    private HashSet<Integer> _mouseButtonsPressed;
    private HashSet<Integer> _prevMouseButtonsPressed;


    public MouseListener() {
        _eventHandlers = new ArrayList<>();

        _xPos = 0.0;
        _yPos = 0.0;
        _prevX = 0.0;
        _prevY = 0.0;

        _mouseButtonsPressed = new HashSet<>();
        _prevMouseButtonsPressed = new HashSet<>();
    }


    // -+- CALLBACKS -+- //

    /**
     * Callback that is called, when the position of the cursor changes.
     *
     * @param windowPointer window the event occurred in
     * @param xPos new position of the cursor on the x-axis
     * @param yPos new position of the cursor y-axis
     *
     * @author Tim Kloepper
     */
    public void cursorPositionCallback(long windowPointer, double xPos, double yPos) {
        _xPos = xPos;
        _yPos = yPos;

        // If the mouse has moved AND any button is down, a drag is happening.
        if (!_prevMouseButtonsPressed.isEmpty()) {_isDragging = true;}
    }
    /**
     * Callback for when a mouse button is pressed.
     *
     * @param windowPointer window the event occurred in
     * @param button the key code of the pressed button
     * @param action the action made with this mouse button
     * @param ignoredMods ?
     *
     * @author Tim Kloepper
     */
    public void mouseButtonCallback(long windowPointer, int button, int action, int ignoredMods) {
        switch (action) {
            case GLFW_PRESS:
                _mouseButtonsPressed.add(button);
                break;
            case GLFW_RELEASE:
                _mouseButtonsPressed.remove(button);
                _isDragging = false;
                break;
        }
    }
    /**
     * Callback for when a scroll occurs.
     *
     * @param windowPointer window the event occurred in
     * @param xOffset distance of the scroll on the x-axis
     * @param yOffset distance of the scroll on the y-axis
     *
     * @author Tim Kloepper
     */
    public void scrollCallback(long windowPointer, double xOffset, double yOffset) {
        _scrollOffsetX = xOffset;
        _scrollOffsetY = yOffset;
    }

    /**
     * Sets the callbacks for mouse events of this window, to the ones of this key listener.
     *
     * @param window
     *
     * @author Tim Kloepper
     */
    public void setUpWindow(Window window) {
        _eventHandlers.add(window.getEventHandler());

        glfwSetCursorPosCallback(window.getPointer(), this::cursorPositionCallback);
        glfwSetMouseButtonCallback(window.getPointer(), this::mouseButtonCallback);
        glfwSetScrollCallback(window.getPointer(), this::scrollCallback);
    }
    /**
     * Sets the callback for mouse events of this window, to an empty lambda,
     * removing it from any further event handling.
     *
     * @param window
     *
     * @author Tim Kloepper
     */
    public void removeWindow(Window window) {
        _eventHandlers.remove(window.getEventHandler());

        glfwSetCursorPosCallback(window.getPointer(), (long windowPointer, double xPos, double yPos) -> {});
        glfwSetMouseButtonCallback(window.getPointer(), (long windowPointer, int button, int action, int mods) -> {});
        glfwSetScrollCallback(window.getPointer(), (long windowPointer, double xOffset, double yOffset) -> {});
    }


    // -+- UPDATE-LOOP -+- //

    /**
     * Called every frame to identify and push {@link MouseEvent} instantly.
     *
     * @author Tim Kloepper
     */
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

    /**
     * Adds a {@link EventHandler} to receive events created and pushed by this mouse listener.
     *
     * @param eventHandler
     *
     * @author Tim Kloepper
     */
    public void addEventHandler(EventHandler eventHandler) {
        _eventHandlers.add(eventHandler);
    }
    /**
     * Removes a {@link EventHandler} to no longer receive events created and pushed by this mouse listener.
     *
     * @param eventHandler
     *
     * @author Tim Kloepper
     */
    public void rmvEventHandler(EventHandler eventHandler) {
        _eventHandlers.remove(eventHandler);
    }

    /**
     * Adds the {@link EventHandler} of the {@link Engine} to this mouse listener (see {@link MouseListener#addEventHandler(EventHandler)}).
     *
     * @author Tim Kloepper
     */
    public void addEngine() {
        _eventHandlers.add(Engine.get().getEventHandler());
    }
    /**
     * Removes the {@link EventHandler} of the {@link Engine} to this mouse listener (see {@link KeyListener#rmvEventHandler(EventHandler)}).
     *
     * @author Tim Kloepper
     */
    public void rmvEngine() {
        _eventHandlers.remove(Engine.get().getEventHandler());
    }

    /**
     * Scans for mouse button events and issues respective pushes of {@link MouseButtonEvent}.
     *
     * @author Tim Kloepper
     */
    private void _manageButtonEvents() {
        for (Integer key : _mouseButtonsPressed) {
            if (_prevMouseButtonsPressed.contains(key)) {continue;}

            _pushPressed(key);
        }
        for (Integer key : _prevMouseButtonsPressed) {
            if (_mouseButtonsPressed.contains(key)) {
                _pushContinued(key);
            } else {
                _pushReleased(key);
            }
        }

        _prevMouseButtonsPressed.clear();
        _prevMouseButtonsPressed.addAll(_mouseButtonsPressed);
    }
    /**
     * Scans for scroll events and issues respective pushes of {@link MouseScrollEvent}.
     *
     * @author Tim Kloepper
     */
    private void _manageScrollEvent() {
        if (_scrollOffsetX == 0 && _scrollOffsetY == 0) {return;}

        _pushEvent(new MouseScrollEvent(_scrollOffsetX, _scrollOffsetY));
    }
    /**
     * Scans for dragging events and issues respective pushes of {@link MouseDraggingEvent}.
     *
     * @author Tim Kloepper
     */
    private void _manageDragEvents() {
        if (_prevIsDragging && _isDragging) {
            _pushEvent(new MouseDraggingEvent());
            return;
        }
        if (!_prevIsDragging && _isDragging) {
            _pushEvent(new MouseDraggingStartedEvent());
            return;
        }
        if (_prevIsDragging) {
            _pushEvent(new MouseDraggingEndedEvent());
        }
    }

    /**
     * Scans for move events and issues respective pushes of {@link MouseMovedEvent}.
     *
     * @author Tim Kloepper
     */
    private void _manageMoveEvent() {
        if (_prevY == _yPos && _prevX == _xPos) {return;}

        _pushEvent(new MouseMovedEvent(_prevX, _prevY, _xPos, _yPos));
    }

    /**
     * Pushes a {@link MouseButtonPressedEvent} for the specified key.
     *
     * @param index index of the key
     *
     * @author Tim Kloepper
     */
    private void _pushPressed(int index) {
        _pushEvent(new MouseButtonPressedEvent(index));
    }

    /**
     * Pushes a {@link MouseButtonContinuedEvent} for the specified key.
     *
     * @param index index of the key
     *
     * @author Tim Kloepper
     */
    private void _pushContinued(int index) {
        _pushEvent(new MouseButtonContinuedEvent(index));
    }

    /**
     * Pushes a {@link MouseButtonReleasedEvent} for the specified key.
     *
     * @param index index of the key
     *
     * @author Tim Kloepper
     */
    private void _pushReleased(int index) {
        _pushEvent(new MouseButtonReleasedEvent(index));
    }

    /**
     * Pushes the given event to all registered {@link EventHandler}.
     *
     * @param event event to be pushed
     *
     * @author Tim Kloepper
     */
    private void _pushEvent(MouseEvent event) {
        for (EventHandler eventHandler : _eventHandlers) {
            eventHandler.pushEvent(event);
        }
    }


}