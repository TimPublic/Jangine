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


public class JangineKeyListener {


    private ArrayList<JangineEventHandler> _eventHandlers;

    private HashSet<Integer> _keyPressedBuffer;
    private HashSet<Integer> _prevKeyPressedBuffer;


    public JangineKeyListener() {
        _eventHandlers = new ArrayList<>();

        _keyPressedBuffer = new HashSet<>();
        _prevKeyPressedBuffer = new HashSet<>();
    }


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


    public void update() {
        _manageKeyEvents();
    }
    public void setUpWindow(JangineWindow window) {
        _eventHandlers.add(window.getEventHandler());

        glfwSetKeyCallback(window.getPointer(), this::keyCallback);
    }
    public void removeWindow(JangineWindow window) {
        _eventHandlers.remove(window.getEventHandler());

        glfwSetKeyCallback(window.getPointer(), (long windowPointer, int key, int scanCode, int action, int mods) -> {});
    }

    public void addEventHandler(JangineEventHandler eventHandler) {
        _eventHandlers.add(eventHandler);
    }
    public void rmvEventHandler(JangineEventHandler eventHandler) {
        _eventHandlers.remove(eventHandler);
    }


    private void _pushEvent(JangineEvent event) {
        for (JangineEventHandler eventHandler : _eventHandlers) {
            eventHandler.pushEvent(event);
        }
    }

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

    private void _pushPressed(Integer key) {
        _pushEvent(new JangineKeyPressedEvent(key));
    }
    private void _pushContinued(Integer key) {
        _pushEvent(new JangineKeyContinuedEvent(key));
    }
    private void _pushReleased(Integer key) {
        _pushEvent(new JangineKeyReleasedEvent(key));
    }


}