package internal.main;


import internal.events.JangineEventHandler;
import internal.rendering.JangineWindow;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;


// JangineEngine is a singleton-class which manages the windows and the main update-loop.
public class JangineEngine {


    private static JangineEngine _instance;


    private JangineEventHandler _eventHandler;

    private Set<JangineWindow> _windows;

    private boolean _shouldClose;


    private JangineEngine() {
        _eventHandler = new JangineEventHandler();

        _windows = new HashSet<>();

        _shouldClose = false;
    }


    // -+- MAIN UPDATE-LOOP -+- //

    // Starts the main loop. If this function ends, the engine closes.
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


    // -+- GETTERS -+- //

    // Returns the engines' event-handler.
    public JangineEventHandler getEventHandler() {
        return _eventHandler;
    }

    // Returns the singleton-instance of this engine.
    public static JangineEngine get() {
        if (_instance == null) {
            _instance = new JangineEngine();
        }

        return _instance;
    }


    // -+- WINDOW-MANAGEMENT -+- //

    // Creates a window and takes it into the main update-loop.
    public JangineWindow createWindow() {
        JangineWindow newWindow;

        newWindow = new JangineWindow();

        _windows.add(newWindow);

        return newWindow;
    }

    // Updates the windows.
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