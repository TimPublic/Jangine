package internal.input;


import internal.events.JangineEvent;
import internal.events.JangineEventHandler;
import internal.events.input.key.JangineKeyContinuedEvent;
import internal.events.input.key.JangineKeyPressedEvent;
import internal.events.input.key.JangineKeyReleasedEvent;
import internal.rendering.JangineWindow;

import java.util.ArrayList;
import java.util.HashSet;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;


// Listens for key-events in windows that are set up with this key-listener.
// Then pushes the events, created on every update call, to the registered event-handlers.
// The key-listener does not buffer events.
public class JangineKeyListener {


    private ArrayList<JangineEventHandler> _eventHandlers;

    private HashSet<Integer> _keyPressedBuffer;
    private HashSet<Integer> _prevKeyPressedBuffer;


    public JangineKeyListener() {
        _eventHandlers = new ArrayList<>();

        _keyPressedBuffer = new HashSet<>();
        _prevKeyPressedBuffer = new HashSet<>();
    }


    // -+- CALLBACKS -+- //

    // Callback that is called by GLFW.
    public void keyCallback(long windowPointer, int key, int scanCode, int action, int mods) {
        switch (action) {
            case GLFW_PRESS:
                _keyPressedBuffer.add(key);
                break;
            case GLFW_RELEASE:
                _keyPressedBuffer.remove(key);
                break;
        }
    }

    // Sets the callback for a GLFW-window to the one of this key-listener and pushes
    // further events to its event-handler.
    public void setUpWindow(JangineWindow window) {
        _eventHandlers.add(window.getEventHandler());

        glfwSetKeyCallback(window.getPointer(), this::keyCallback);
    }
    // Sets the callback for a GLFW-window to an empty lambda and does not push
    // further events to its event-handler.
    public void removeWindow(JangineWindow window) {
        _eventHandlers.remove(window.getEventHandler());

        glfwSetKeyCallback(window.getPointer(), (long windowPointer, int key, int scanCode, int action, int mods) -> {});
    }


    // -+- EVENT-HANDLING -+- //

    // Adds an event-handler to this key-listener, that further events will be pushed to.
    public void addEventHandler(JangineEventHandler eventHandler) {
        _eventHandlers.add(eventHandler);
    }
    // Removes an event-handler from this key-listener, which will there not push further events
    // to this event-handler.
    public void rmvEventHandler(JangineEventHandler eventHandler) {
        _eventHandlers.remove(eventHandler);
    }

    // Detects key events and calls respective event-pushes.
    private void _manageKeyEvents() {
        for (Integer key : _keyPressedBuffer) {
            if (_prevKeyPressedBuffer.contains(key)) {continue;}

            _pushPressed(key);
            System.out.println("PRESS");
        }
        for (Integer key : _prevKeyPressedBuffer) {
            if (_keyPressedBuffer.contains(key)) {
                _pushContinued(key);
            } else {
                _pushReleased(key);
            }
        }

        _prevKeyPressedBuffer = (HashSet<Integer>) _keyPressedBuffer.clone();
    }

    // Pushes a pressed-event for the given key.
    private void _pushPressed(Integer key) {
        _pushEvent(new JangineKeyPressedEvent(key));
    }
    // Pushes a continued-event for the given key.
    private void _pushContinued(Integer key) {
        _pushEvent(new JangineKeyContinuedEvent(key));
    }
    // Pushes a released-event for the given key.
    private void _pushReleased(Integer key) {
        _pushEvent(new JangineKeyReleasedEvent(key));
    }

    // Pushes a given event to all registered event-handlers.
    private void _pushEvent(JangineEvent event) {
        for (JangineEventHandler eventHandler : _eventHandlers) {
            eventHandler.pushEvent(event);
        }
    }


    // -+- UPDATE-LOOP -+- //

    // Is called every frame to push events.
    public void update() {
        _manageKeyEvents();
    }


}