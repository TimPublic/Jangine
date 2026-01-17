package internal.input;


import internal.events.Event;
import internal.events.EventHandler;
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


    private ArrayList<EventHandler> _eventHandlers;

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

    /**
     * Adds a {@link EventHandler} to receive events created and pushed by this key listener.
     *
     * @param eventHandler
     *
     * @author Tim Kloepper
     */
    public void addEventHandler(EventHandler eventHandler) {
        _eventHandlers.add(eventHandler);
    }
    /**
     * Removes a {@link EventHandler} to no longer receive events created and pushed by this key listener.
     *
     * @param eventHandler
     *
     * @author Tim Kloepper
     */
    public void rmvEventHandler(EventHandler eventHandler) {
        _eventHandlers.remove(eventHandler);
    }

    /**
     * Adds the {@link EventHandler} of the {@link Engine} to this key listener (see {@link KeyListener#addEventHandler(EventHandler)}).
     *
     * @author Tim Kloepper
     */
    public void addEngine() {
        _eventHandlers.add(Engine.get().getEventHandler());
    }
    /**
     * Removes the {@link EventHandler} of the {@link Engine} to this key listener (see {@link KeyListener#rmvEventHandler(EventHandler)}).
     *
     * @author Tim Kloepper
     */
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

        _prevKeyPressedBuffer = (HashSet<Integer>) _keyPressedBuffer.clone();
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

    /**
     * Pushes the specifies {@link Event} to all registered {@link EventHandler}.
     *
     * @param event {@link Event} event to be pushed
     *
     * @author Tim Kloepper
     */
    private void _pushEvent(Event event) {
        for (EventHandler eventHandler : _eventHandlers) {
            eventHandler.pushEvent(event);
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