package internal.rendering;


import internal.events.JangineEventHandler;
import internal.input.JangineKeyListener;
import internal.input.JangineMouseListener;
import internal.main.JangineEngine;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.HashSet;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


/**
 * A window is a simple desktop-window which is created and managed by the {@link JangineEngine}.
 * It contains an amount of {@link JangineScene} from which only one is active at one time, which
 * is the only one that gets updated.
 * A window also always contains a {@link JangineKeyListener} and a {@link JangineMouseListener}
 * that push their events to the windows' and the engines' {@link JangineEventHandler}.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class JangineWindow {


    private int _width, _height;
    private String _title;

    private JangineEventHandler _eventHandler;
    private JangineKeyListener _keyListener;
    private JangineMouseListener _mouseListener;

    private long _glfw_windowPointer;


    private HashSet<JangineScene> _scenes;
    private JangineScene _activeScene;


    public JangineWindow() {
        _width = 960;
        _height = 540;

        _title = "Jangine Window";

        _eventHandler = new JangineEventHandler();

        _init();

        _setUpKeyListener();
        _setUpMouseListener();

        _setUpEngine();

        _scenes = new HashSet<>();

        activateScene(createScene());
    }


    // -+- UPDATE-LOOP -+- //

    /**
     * Main update-loop method, that is called by the {@link JangineEngine}.
     *
     * @return will keep running
     *
     * @author Tim Kloepper
     */
    public boolean update() {
        if (glfwWindowShouldClose(_glfw_windowPointer)) {
            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(_glfw_windowPointer);
            glfwDestroyWindow(_glfw_windowPointer);

            return false;
        }

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        glfwMakeContextCurrent(_glfw_windowPointer);

        _updateScene();

        glfwSwapBuffers(_glfw_windowPointer); // swap the color buffers

        _keyListener.update();
        _mouseListener.update();

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        return true;
    }

    /**
     * Updates the currently active {@link JangineScene}.
     *
     * @author Tim Kloepper
     */
    private void _updateScene() {
        if (_activeScene == null) {return;}

        _activeScene.update(44); // 44 is just a placeholder.
    }


    // -+- INITIALIZATION -+- //

    /**
     * Initializes the window on the openGL-side.
     * Copied from the official JWLGL-Website.
     *
     * @author Tim Kloepper.
     */
    private void _init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        _glfw_windowPointer = glfwCreateWindow(_width, _height, "Hello World!", NULL, NULL);
        if (_glfw_windowPointer == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(_glfw_windowPointer, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    _glfw_windowPointer,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(_glfw_windowPointer);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(_glfw_windowPointer);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    // Connects the engines' event-handler to the listeners and keeps a reference of the event-handler.
    private void _setUpEngine() {
        _keyListener.addEngine();
        _mouseListener.addEngine();
    }
    // Creates the key-listener and connects this window with it.
    private void _setUpKeyListener() {
        _keyListener = new JangineKeyListener();

        _keyListener.setUpWindow(this);
    }
    // Creates the mouse-listener and connect this window with it.
    private void _setUpMouseListener() {
        _mouseListener = new JangineMouseListener();

        _mouseListener.setUpWindow(this);
    }


    // -+- SCENE-MANAGEMENT -+- //

    // Creates a new scene in this window and returns it.
    protected JangineScene createScene() {
        JangineScene newScene;

        newScene = new JangineScene(_eventHandler, _width, _height, false);

        _scenes.add(newScene);

        return newScene;
    }
    // Activates a scene.
    // The scene must be home to this window, if not, the engine will crash.
    // If another scene is currently active, it will be deactivated.
    protected JangineScene activateScene(JangineScene scene) {
        if (!_scenes.contains(scene)) {
            System.err.println("[WINDOW ERROR] : Tried to make scene active, which is not home to this window!");

            System.exit(1);
        }

        if (_activeScene != null) {
            _activeScene.deactivate();
        }

        _activeScene = scene;

        _activeScene.activate();

        return _activeScene;
    }
    // Deactivates a scene.
    // The scene must be home to this window, if not, the engine will crash.
    // After this call, no scene will be active.
    protected JangineScene deactivateScene(JangineScene scene) {
        if (!_scenes.contains(scene)) {
            System.err.println("[WINDOW ERROR] : Tried to deactivate scene, which is not home to this window!");

            System.exit(1);
        }

        _activeScene.deactivate();

        _activeScene = null;

        return scene;
    }
    // Deactivates a scene and activates another one.
    // Both scenes must be home to this window, if not, the engine will crash.
    protected void deactivateScene(JangineScene scene, JangineScene newActiveScene) {
        deactivateScene(scene);
        activateScene(scene);
    }
    // Transfers a scene to another window.
    // The scene must be home to this window, if not, the engine will crash.
    // After this call, the scene is home to the specified window and not to this window anymore.
    public JangineScene transferScene(JangineScene scene, JangineWindow to) {
        if (!_scenes.contains(scene)) {
            System.err.println("[WINDOW ERROR] : Tried to transfer scene, which is not home to this window!");
        }

        if (_activeScene == scene) {
            _activeScene.deactivate();
            _activeScene = null;
        }

        _scenes.remove(scene);

        to._addScene(scene);

        return scene;
    }

    // Adds a scene to this window and therefore makes it home to this window.
    // A scene should only ever be home to one window!
    private void _addScene(JangineScene scene) {
        _scenes.add(scene);
    }


    // -+- GETTERS -+- //

    protected JangineScene getActiveScene() {
        return _activeScene;
    }

    // Returns the GLFW-pointer to this window.
    public long getPointer() {
        return _glfw_windowPointer;
    }

    // Returns this windows' event-handler.
    public JangineEventHandler getEventHandler() {
        return _eventHandler;
    }

    // Returns the width of this window.
    public int getWidth() {
        return _width;
    }
    // Returns the height of this window.
    public int getHeight() {
        return _height;
    }
    // Returns the title of this window.
    public String getTitle() {
        return _title;
    }


}