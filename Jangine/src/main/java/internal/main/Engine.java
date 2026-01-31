package internal.main;


import internal.events.EventHandler;
import internal.rendering.container.Window;
import internal.util.DeltaTimer;

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
public class Engine {


    private static Engine _instance;


    private EventHandler _eventHandler;

    private Set<Window> _windows;

    private boolean _shouldClose;

    private double _currentDeltaTime;


    private Engine() {
        _eventHandler = new EventHandler();

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

        while (!_shouldClose) {
            _currentDeltaTime = DeltaTimer.get().getDeltaTime();

            _shouldClose = _updateWindows();

            DeltaTimer.get().update(); // At the end, otherwise delta time is always 0.
        }

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    /**
     * Returns the singleton instance of this engine.
     *
     * @return this {@link Engine}
     *
     * @author Tim Kloepper
     */
    public static Engine get() {
        if (_instance == null) {
            _instance = new Engine();
        }

        return _instance;
    }


    // -+- GETTERS -+- //

    /**
     * Returns the engines' event handler.
     *
     * @return {@link EventHandler}
     *
     * @author Tim Kloepper
     */
    public EventHandler getEventHandler() {
        return _eventHandler;
    }


    // -+- WINDOW-MANAGEMENT -+- //

    /**
     * Creates a {@link Window} and takes it into the main update loop.
     *
     * @return the new {@link Window}
     *
     * @author Tim Kloepper
     */
    public Window createWindow() {
        Window newWindow;

        newWindow = new Window();

        _windows.add(newWindow);

        return newWindow;
    }

    /**
     * Adds a {@link Window} and takes it into the main update loop.
     *
     * @param window the window that is to be added
     *
     * @author Tim Kloepper
     */
    public void addWindow(Window window) {
        _windows.add(window);
    }

    /**
     * Updates all the windows.
     *
     * @return if any window closes
     *
     * @author Tim Kloepper
     */
    private boolean _updateWindows() {
        Set<Window> deletionQueue;

        int windowsSize;

        double deltaTime;

        deletionQueue = new HashSet<>();

        windowsSize = _windows.size();

        for (Window window : _windows) {
            if (window.update(_currentDeltaTime)) {continue;}

            deletionQueue.add(window);
        }

        _windows.removeAll(deletionQueue);

        if (_windows.size() < windowsSize) {return true;}

        return false;
    }


}