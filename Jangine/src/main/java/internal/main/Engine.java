package internal.main;

import internal.events.JangineEventHandler;
import internal.rendering.JangineWindow;
import internal.rendering.ShaderTest;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;


public class Engine {


    private static Engine _instance;


    private JangineEventHandler _eventHandler;

    private Set<JangineWindow> _windows;

    private boolean _shouldClose;


    private Engine() {
        _eventHandler = new JangineEventHandler();

        _windows = new HashSet<>();

        _shouldClose = false;
    }

    public static Engine get() {
        if (_instance == null) {
            _instance = new Engine();
        }

        return _instance;
    }


    public JangineEventHandler getEventHandler() {
        return _eventHandler;
    }

    public void run() {
        createWindow();
        createWindow();

        while (!_shouldClose) {
            _shouldClose = _updateWindows();
        }

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public JangineWindow createWindow() {
        JangineWindow newWindow;

        newWindow = new JangineWindow(this);

        _windows.add(newWindow);

        return newWindow;
    }


    private boolean _updateWindows() {
        Set<JangineWindow> deletionQueue;

        int windowsSize;

        deletionQueue = new HashSet<>();

        windowsSize = _windows.size();

        for (JangineWindow window : _windows) {
            if (window.update()) {continue;}

            deletionQueue.add(window);
        }

        _windows.removeAll(deletionQueue);

        if (_windows.size() < windowsSize) {return true;}

        return false;
    }


}