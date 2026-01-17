package internal.input;


import internal.events.JangineEvent;
import internal.events.JangineEventHandler;
import internal.events.input.key.JangineKeyContinuedEvent;
import internal.events.input.key.JangineKeyPressedEvent;
import internal.events.input.key.JangineKeyReleasedEvent;
import internal.main.JangineEngine;
import internal.rendering.Window;

import java.util.ArrayList;
import java.util.HashSet;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;


/**
 * Listens for key events in set-up windows, converts them to {@link internal.events.input.key.JangineKeyEvent} and pushes them to registered event handlers.
 * The key listener does not buffer events.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
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

    /**
     * Callback that is called by GLFW, if anything revolving around a key happens.
     *
     * @param windowPointer the window the event occurred in
     * @param key the key code of the key causing this event
     * @param scanCode ?
     * @param action which action was made with this key
     * @param mods ?
     */
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

    /**
     * Sets the callbacks for key events of this window, to the ones of this key listener.
     *
     * @param window
     *
     * @author Tim Kloepper
     */
    public void setUpWindow(Window window) {
        _eventHandlers.add(window.getEventHandler());

        glfwSetKeyCallback(window.getPointer(), this::keyCallback);
    }

    /**
     * Sets the callback for key events of this window, to an empty lambda,
     * removing it from any further event handling.
     *
     * @param window
     *
     * @author Tim Kloepper
     */
    public void removeWindow(Window window) {
        _eventHandlers.remove(window.getEventHandler());

        glfwSetKeyCallback(window.getPointer(), (long windowPointer, int key, int scanCode, int action, int mods) -> {});
    }


    // -+- EVENT-HANDLING -+- //

    /**
     * Adds a {@link JangineEventHandler} to receive events created and pushed by this key listener.
     *
     * @param eventHandler
     *
     * @author Tim Kloepper
     */
    public void addEventHandler(JangineEventHandler eventHandler) {
        _eventHandlers.add(eventHandler);
    }
    /**
     * Removes a {@link JangineEventHandler} to no longer receive events created and pushed by this key listener.
     *
     * @param eventHandler
     *
     * @author Tim Kloepper
     */
    public void rmvEventHandler(JangineEventHandler eventHandler) {
        _eventHandlers.remove(eventHandler);
    }

    /**
     * Adds the {@link JangineEventHandler} of the {@link JangineEngine} to this key listener (see {@link JangineKeyListener#addEventHandler(JangineEventHandler)}).
     *
     * @author Tim Kloepper
     */
    public void addEngine() {
        _eventHandlers.add(JangineEngine.get().getEventHandler());
    }
    /**
     * Removes the {@link JangineEventHandler} of the {@link JangineEngine} to this key listener (see {@link JangineKeyListener#rmvEventHandler(JangineEventHandler)}).
     *
     * @author Tim Kloepper
     */
    public void rmvEngine() {
        _eventHandlers.remove(JangineEngine.get().getEventHandler());
    }

    /**
     * Detects key events by performing certain checks,
     * then issues {@link internal.events.input.key.JangineKeyEvent} pushes.
     *
     * @author Tim Kloepper
     */
    private void _manageKeyEvents() {
        for (Integer key : _keyPressedBuffer) {
            if (_prevKeyPressedBuffer.contains(key)) {continue;}

            _pushPressed(key);
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

    /**
     * Pushes a {@link JangineKeyContinuedEvent} with the certified key.
     *
     * @param key key code.
     *
     * @author Tim Kloepper
     */
    private void _pushContinued(Integer key) {
        _pushEvent(new JangineKeyContinuedEvent(key));
    }
    /**
     * Pushes a {@link JangineKeyPressedEvent} with the certified key.
     *
     * @param key key code.
     *
     * @author Tim Kloepper
     */
    private void _pushPressed(Integer key) {
        _pushEvent(new JangineKeyPressedEvent(key));
    }
    /**
     * Pushes a {@link JangineKeyReleasedEvent} with the certified key.
     *
     * @param key key code.
     *
     * @author Tim Kloepper
     */
    private void _pushReleased(Integer key) {
        _pushEvent(new JangineKeyReleasedEvent(key));
    }

    /**
     * Pushes the specifies {@link JangineEvent} to all registered {@link JangineEventHandler}.
     *
     * @param event {@link JangineEvent} event to be pushed
     *
     * @author Tim Kloepper
     */
    private void _pushEvent(JangineEvent event) {
        for (JangineEventHandler eventHandler : _eventHandlers) {
            eventHandler.pushEvent(event);
        }
    }


    // -+- UPDATE-LOOP -+- //

    /**
     * Is called every frame to detect and instantly push {@link internal.events.input.key.JangineKeyEvent}.
     *
     * @author Tim Kloepper
     */
    public void update() {
        _manageKeyEvents();
    }


}