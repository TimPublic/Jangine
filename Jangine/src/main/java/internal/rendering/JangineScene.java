package internal.rendering;


import internal.ecs.JangineECS_System;
import internal.events.JangineEventListeningPort;
import internal.events.JangineEvent;
import internal.events.JangineEventHandler;
import internal.main.JangineEngine;

import java.util.ArrayList;
import java.util.List;


/**
 * Scenes are contained inside of {@link JangineWindow} and hold own render logic.
 * They have their own event handler, which only receives events,
 * when the scene is active.
 * Every scene also has its own entity component system.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public class JangineScene {


    private final JangineEventHandler _windowEventHandler;

    private final JangineEventHandler _ownEventHandler;
    private final JangineEventListeningPort _ownEventHandlerListeningPort;

    private ArrayList<JangineEventListeningPort> _windowListeningPorts;
    private ArrayList<JangineEventListeningPort> _engineListeningPorts;


    private JangineCamera2D _camera;


    private JangineECS_System _ecsSystem;


    ShaderTest test;


    // private ArrayList<RenderObject> _renderObjects;


    public JangineScene(final JangineEventHandler windowEventHandler, int width, int height, boolean active) {
        _windowEventHandler = windowEventHandler;

        _ownEventHandler = new JangineEventHandler();
        _ownEventHandlerListeningPort = _windowEventHandler.register();
        _ownEventHandlerListeningPort.registerFunction(_ownEventHandler::pushEvent, List.of(JangineEvent.class));
        _ownEventHandlerListeningPort.setActive(active);

        _windowListeningPorts = new ArrayList<>();
        _engineListeningPorts = new ArrayList<>();

        _camera = new JangineCamera2D(width, height);

        test = new ShaderTest();

        _ecsSystem = new JangineECS_System();

        _onCreation();
    }


    // -+- LIFE-CYCLE -+- //

    /**
     * Gets called by the window, when a scene gets active.
     * Before this call, the event handler is not distributing events.
     *
     * @author Tim Kloepper
     */
    public final void activate() {
        _ownEventHandlerListeningPort.setActive(true);

        _onActivation();
    }
    /**
     * Gets called by the window, when a scene is no longer active.
     * If this call is not made, the event handler will keep distributing events.
     *
     * @author Tim Kloepper
     */
    public final void deactivate() {
        _ownEventHandlerListeningPort.setActive(false);

        _onDeactivation();
    }
    /**
     * Gets called by the window, right before this scene is deleted.
     * Handles deregistering from services such as the windows' event handler.
     *
     * @author Tim Kloepper
     */
    public final void kill() {
        _windowEventHandler.deregister(_ownEventHandlerListeningPort);

        for (JangineEventListeningPort port : _windowListeningPorts) {
            _windowEventHandler.deregister(port);
        }
        for (JangineEventListeningPort port : _engineListeningPorts) {
            JangineEngine.get().getEventHandler().deregister(port);
        }

        _onKill();
    }

    /**
     * OVERWRITE
     * <p>
     * Gets called every time, the scene is activated.
     *
     * @author Tim Kloepper
     */
    protected void _onActivation() {}
    /**
     * OVERWRITE
     * <p>
     * Gets called every time, the scene is deactivated.
     *
     * @author Tim Kloepper
     */
    protected void _onDeactivation() {}
    /**
     * OVERWRITE
     * <p>
     * Gets called every time, the scene is created.
     *
     * @author Tim Kloepper
     */
    protected void _onCreation() {}
    /**
     * OVERWRITE
     * <p>
     * Gets called every time, the scene is killed.
     *
     * @author Tim Kloepper
     */
    protected void _onKill() {}


    // -+- UPDATE-LOOP -+- //

    /**
     * Gets called by the {@link JangineWindow} on every frame, as long as this scene is active.
     *
     * @param deltaTime time passed, since the last frame
     *
     * @author Tim Kloepper
     */
    public final void update(double deltaTime) {
        _onUpdate(deltaTime);

        _ecsSystem.update(deltaTime);

        _render();
    }

    /**
     * OVERWRITE
     * <p>
     * Gets called every frame, as long as this scene is active.
     *
     * @param deltaTime time passed, since the last frame
     *
     * @author Tim Kloepper
     */
    protected void _onUpdate(double deltaTime) {}


    // -+- RENDERING -+- //

    /**
     * Gets called every frame and handles basic render logic that every scene does.
     *
     * @author Tim Kloepper
     */
    private void _render() {
        test.run();

        _onRender();
    }

    /**
     * OVERWRITE
     * <p>
     * Gets called every frame in the {@link JangineScene#_render()} method.
     * Overwrite it, to implement custom render logic.
     *
     * @author Tim Kloepper
     */
    protected void _onRender() {}


    // -+- EVENT-HANDLING -+- //

    /**
     * Returns a listening port of the windows' event handler.
     *
     * @return {@link JangineEventListeningPort}
     *
     * @author Tim Kloepper
     */
    public final JangineEventListeningPort getWindowEventListeningPort() {
        JangineEventListeningPort port;

        port = _windowEventHandler.register();

        _windowListeningPorts.add(port);

        return port;
    }
    /**
     * Removes a listing port of the windows' event handler.
     *
     * @param port {@link JangineEventListeningPort} to be deleted
     *
     * @author Tim Kloepper
     */
    public final void removeWindowEventListeningPort(JangineEventListeningPort port) {
        _windowListeningPorts.remove(port);

        _windowEventHandler.deregister(port);
    }
    /**
     * Returns this scenes' event handler.
     *
     * @return {@link JangineEventHandler} of this scene
     *
     * @author Tim Kloepper
     */
    public final JangineEventHandler getEventHandler() {
        return _ownEventHandler;
    }


    // -+- CAMERA-LOGIC -+- //

    /**
     * Sets the resolution of the camera to the specified width and height in 32-by-32 pixels.
     * (See {@link JangineCamera2D#adjustProjection(int, int)})
     *
     * @param width
     * @param height
     *
     * @author Tim Kloepper
     */
    public void setRes(int width, int height) {
        _camera.adjustProjection(width, height);
    }


}