package internal.rendering;


import internal.events.JangineEventHandler;
import internal.input.JangineKeyListener;
import internal.input.JangineMouseListener;
import internal.main.Engine;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class JangineWindow {


    private int _width, _height;
    private String _title;

    private JangineEventHandler _eventHandler;
    private JangineKeyListener _keyListener;
    private JangineMouseListener _mouseListener;

    private long _glfw_windowPointer;

    ShaderTest test;


    public JangineWindow(Engine engine) {
        _width = 960;
        _height = 540;

        _title = "Jangine Window";

        _eventHandler = new JangineEventHandler();

        _init();

        _setUpKeyListener();
        _setUpMouseListener();

        _setUpEngine(engine);

        test = new ShaderTest();
    }

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


    public boolean update() {
        if (glfwWindowShouldClose(_glfw_windowPointer)) {
            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(_glfw_windowPointer);
            glfwDestroyWindow(_glfw_windowPointer);

            return false;
        }

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        test.run();

        glfwMakeContextCurrent(_glfw_windowPointer);

        glfwSwapBuffers(_glfw_windowPointer); // swap the color buffers

        _keyListener.update();
        _mouseListener.update();

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        return true;
    }

    private void _setUpEngine(Engine engine) {
        _keyListener.addEventHandler(engine.getEventHandler());
        _mouseListener.addEventHandler(engine.getEventHandler());
    }
    private void _setUpKeyListener() {
        _keyListener = new JangineKeyListener();

        _keyListener.setUpWindow(this);
    }
    private void _setUpMouseListener() {
        _mouseListener = new JangineMouseListener();

        _mouseListener.setUpWindow(this);
    }

    public long getPointer() {
        return _glfw_windowPointer;
    }
    public JangineEventHandler getEventHandler() {
        return _eventHandler;
    }


    public int getWidth() {
        return _width;
    }
    public int getHeight() {
        return _height;
    }
    public String getTitle() {
        return _title;
    }


}