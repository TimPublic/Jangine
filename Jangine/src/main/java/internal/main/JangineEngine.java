package internal.main;


import internal.events.JangineEventHandler;
import internal.rendering.JangineWindow;
import internal.util.JangineDeltaTimer;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;


/**
 * Is a singleton class which manages the windows and the main update loop.
 * If this class gets destroyed, Jangine is no longer running and therefore
 * any game that uses it.
 * <p>
 * This class stands on the top of all hierarchy of Jangine.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class JangineEngine {


    private static JangineEngine _instance;


    private JangineEventHandler _eventHandler;

    private Set<JangineWindow> _windows;

    private boolean _shouldClose;

    private double _currentDeltaTime;


    private JangineEngine() {
        _eventHandler = new JangineEventHandler();

        _windows = new HashSet<>();

        _shouldClose = false;

        _currentDeltaTime = 0;
    }


    // -+- MAIN UPDATE-LOOP -+- //

    /**
     * Starts the main loop.
     * If this function "ends", the engine closes.
     *
     * @author Tim Kloepper
     */
    public void run() {
        createWindow();
        createWindow();

        while (!_shouldClose) {
            _currentDeltaTime = JangineDeltaTimer.get().getDeltaTime();

            _shouldClose = _updateWindows();

            JangineDeltaTimer.get().update(); // At the end, otherwise delta time is always 0.
        }

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    /**
     * Returns the singleton instance of this engine.
     *
     * @return this {@link JangineEngine}
     *
     * @author Tim Kloepper
     */
    public static JangineEngine get() {
        if (_instance == null) {
            _instance = new JangineEngine();
        }

        return _instance;
    }


    // -+- GETTERS -+- //

    /**
     * Returns the engines' event handler.
     *
     * @return {@link JangineEventHandler}
     *
     * @author Tim Kloepper
     */
    public JangineEventHandler getEventHandler() {
        return _eventHandler;
    }


    // -+- WINDOW-MANAGEMENT -+- //

    /**
     * Creates a {@link JangineWindow} and takes it into the main update loop.
     *
     * @return the new {@link JangineWindow}
     *
     * @author Tim Kloepper
     */
    public JangineWindow createWindow() {
        JangineWindow newWindow;

        newWindow = new JangineWindow();

        _windows.add(newWindow);

        return newWindow;
    }

    /**
     * Updates all the windows.
     *
     * @return if any window closes
     *
     * @author Tim Kloepper
     */
    private boolean _updateWindows() {
        Set<JangineWindow> deletionQueue;

        int windowsSize;

        double deltaTime;

        deletionQueue = new HashSet<>();

        windowsSize = _windows.size();

        for (JangineWindow window : _windows) {
            if (window.update(_currentDeltaTime)) {continue;}

            deletionQueue.add(window);
        }

        _windows.removeAll(deletionQueue);

        if (_windows.size() < windowsSize) {return true;}

        return false;
    }


}