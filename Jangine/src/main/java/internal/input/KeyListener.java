package internal.input;


import internal.events.EventMaster;
import internal.events.implementations.Event;
import internal.events.input.key.KeyContinuedEvent;
import internal.events.input.key.KeyPressedEvent;
import internal.events.input.key.KeyReleasedEvent;
import internal.events.input.key.KeyEvent;
import internal.main.Engine;
import internal.rendering.container.Window;

import java.util.ArrayList;
import java.util.HashSet;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;


/**
 * Listens for key events in set-up windows, converts them to {@link KeyEvent} and pushes them to registered event handlers.
 * The key listener does not buffer events.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class KeyListener {


    private ArrayList<EventMaster> _eventHandlers;

    private HashSet<Integer> _keyPressedBuffer;
    private HashSet<Integer> _prevKeyPressedBuffer;


    public KeyListener() {
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

    public void addEventHandler(EventMaster eventHandler) {
        _eventHandlers.add(eventHandler);
    }
    public void rmvEventHandler(EventMaster eventHandler) {
        _eventHandlers.remove(eventHandler);
    }

    public void addEngine() {
        _eventHandlers.add(Engine.get().getEventHandler());
    }
    public void rmvEngine() {
        _eventHandlers.remove(Engine.get().getEventHandler());
    }

    /**
     * Detects key events by performing certain checks,
     * then issues {@link KeyEvent} pushes.
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

        _prevKeyPressedBuffer.clear();
        _prevKeyPressedBuffer.addAll(_keyPressedBuffer);
    }

    /**
     * Pushes a {@link KeyContinuedEvent} with the certified key.
     *
     * @param key key code.
     *
     * @author Tim Kloepper
     */
    private void _pushContinued(Integer key) {
        _pushEvent(new KeyContinuedEvent(key));
    }
    /**
     * Pushes a {@link KeyPressedEvent} with the certified key.
     *
     * @param key key code.
     *
     * @author Tim Kloepper
     */
    private void _pushPressed(Integer key) {
        _pushEvent(new KeyPressedEvent(key));
    }
    /**
     * Pushes a {@link KeyReleasedEvent} with the certified key.
     *
     * @param key key code.
     *
     * @author Tim Kloepper
     */
    private void _pushReleased(Integer key) {
        _pushEvent(new KeyReleasedEvent(key));
    }

    private void _pushEvent(KeyEvent event) {
        for (EventMaster eventHandler : _eventHandlers) {
            eventHandler.push(event);
        }
    }


    // -+- UPDATE-LOOP -+- //

    /**
     * Is called every frame to detect and instantly push {@link KeyEvent}.
     *
     * @author Tim Kloepper
     */
    public void update() {
        _manageKeyEvents();
    }


}