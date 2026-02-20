package internal.main;


import internal.events.A_EventPort;
import internal.events.EventFilter;
import internal.events.EventMaster;
import internal.events.implementations.ActiveEventPort;
import internal.rendering.container.Window;
import internal.util.DeltaTimer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;


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


    private EventMaster _eventHandler;

    private Set<Window> _windows;
    private final HashMap<Window, ActiveEventPort> _WINDOW_PORTS;

    private boolean _shouldClose;

    private double _currentDeltaTime;


    private Engine() {
        _eventHandler = new EventMaster();

        _windows = new HashSet<>();
        _WINDOW_PORTS = new HashMap<>();

        _shouldClose = false;

        _currentDeltaTime = 0;

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");
    }


    // -+- MAIN UPDATE-LOOP -+- //

    /**
     * Starts the main loop.
     * If this function "ends", the engine closes.
     *
     * @author Tim Kloepper
     */
    public void run() {
        if (_windows.isEmpty()) return;

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

    public EventMaster getEventHandler() {
        return _eventHandler;
    }


    // -+- WINDOW-MANAGEMENT -+- //

    /**
     * Adds a {@link Window} and takes it into the main update loop.
     *
     * @param window the window that is to be added
     *
     * @author Tim Kloepper
     */
    public void addWindow(Window window) {
        ActiveEventPort port;

        _windows.add(window);

        port = new ActiveEventPort(new EventFilter());
        window.getEventHandler().register(port);
        _WINDOW_PORTS.put(window, port);
        _WINDOW_PORTS.get(window).addCallback(
                event -> _eventHandler.push(event)
        );
    }
    public void rmvWindow(Window window) {
        if (!_windows.remove(window)) return;

        window.getEventHandler().deregister(_WINDOW_PORTS.get(window));
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