package internal.rendering.container;


import internal.entity_component_system.System;
import internal.events.EventListeningPort;
import internal.events.Event;
import internal.events.EventHandler;
import internal.main.Engine;
import internal.rendering.camera.Camera2D;
import internal.util.PathManager;
import internal.util.ResourceManager;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;


/**
 * Scenes are contained inside of {@link Window} and hold own render logic.
 * They have their own event handler, which only receives events,
 * when the scene is active.
 * Every scene also has its own entity component system.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public class Scene extends Container {


    private EventHandler _windowEventHandler;

    private final EventHandler _OWN_EVENT_HANDLER;
    private final EventListeningPort _WINDOW_EVENT_HANDLER_LISTENING_PORT;

    protected final System p_ECS;

    protected final ResourceManager p_RESOURCE_MANAGER;
    protected final PathManager p_PATH_MANAGER;

    private ArrayList<EventListeningPort> _windowListeningPorts;
    private ArrayList<EventListeningPort> _engineListeningPorts;


    private Camera2D _camera;


    public Scene(final EventHandler windowEventHandler, int width, int height, boolean active) {
        super(new Vector2d(0, 0), 1000, 1000);

        _windowEventHandler = windowEventHandler;

        _OWN_EVENT_HANDLER = new EventHandler();
        _WINDOW_EVENT_HANDLER_LISTENING_PORT = windowEventHandler.register();
        _WINDOW_EVENT_HANDLER_LISTENING_PORT.registerFunction(_OWN_EVENT_HANDLER::pushEvent, List.of(Event.class));
        _WINDOW_EVENT_HANDLER_LISTENING_PORT.setActive(active);

        p_ECS = new System(this);

        p_RESOURCE_MANAGER = new ResourceManager();
        p_PATH_MANAGER = new PathManager();

        _windowListeningPorts = new ArrayList<>();
        _engineListeningPorts = new ArrayList<>();

        _camera = new Camera2D(width, height);

        _onCreation();
    }


    public void test(Event event) {
        return;
    }


    // -+- LIFE-CYCLE -+- //

    /**
     * Gets called by the window, when a scene gets active.
     * Before this call, the event handler is not distributing events.
     *
     * @author Tim Kloepper
     */
    public final void activate() {
        _WINDOW_EVENT_HANDLER_LISTENING_PORT.setActive(true);

        _onActivation();
    }
    /**
     * Gets called by the window, when a scene is no longer active.
     * If this call is not made, the event handler will keep distributing events.
     *
     * @author Tim Kloepper
     */
    public final void deactivate() {
        _WINDOW_EVENT_HANDLER_LISTENING_PORT.setActive(false);

        _onDeactivation();
    }
    /**
     * Gets called by the window, right before this scene is deleted.
     * Handles deregistering from services such as the windows' event handler.
     *
     * @author Tim Kloepper
     */
    public final void kill() {
        _windowEventHandler.deregister(_WINDOW_EVENT_HANDLER_LISTENING_PORT);

        for (EventListeningPort port : _windowListeningPorts) {
            _windowEventHandler.deregister(port);
        }
        for (EventListeningPort port : _engineListeningPorts) {
            Engine.get().getEventHandler().deregister(port);
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
     * Gets called by the {@link Window} on every frame, as long as this scene is active.
     *
     * @param deltaTime time passed, since the last frame
     *
     * @author Tim Kloepper
     */
    public final void update(double deltaTime) {
        _onUpdate(deltaTime);

        p_ECS.update();

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
        // test.run();

        _onRender();
    }

    /**
     * OVERWRITE
     * <p>
     * Gets called every frame in the {@link Scene#_render()} method.
     * Overwrite it, to implement custom render logic.
     *
     * @author Tim Kloepper
     */
    protected void _onRender() {}


    // -+- EVENT-HANDLING -+- //

    /**
     * Returns a listening port of the windows' event handler.
     *
     * @return {@link EventListeningPort}
     *
     * @author Tim Kloepper
     */
    public final EventListeningPort getWindowEventListeningPort() {
        EventListeningPort port;

        port = _windowEventHandler.register();

        _windowListeningPorts.add(port);

        return port;
    }
    /**
     * Removes a listing port of the windows' event handler.
     *
     * @param port {@link EventListeningPort} to be deleted
     *
     * @author Tim Kloepper
     */
    public final void removeWindowEventListeningPort(EventListeningPort port) {
        _windowListeningPorts.remove(port);

        _windowEventHandler.deregister(port);
    }
    /**
     * Returns this scenes' event handler.
     *
     * @return {@link EventHandler} of this scene
     *
     * @author Tim Kloepper
     */
    public final EventHandler getEventHandler() {
        return _OWN_EVENT_HANDLER;
    }


    // -+- CAMERA-LOGIC -+- //

    /**
     * Sets the resolution of the camera to the specified width and height in 32-by-32 pixels.
     * (See {@link Camera2D#adjustProjection(int, int)})
     *
     * @param width
     * @param height
     *
     * @author Tim Kloepper
     */
    public void setRes(int width, int height) {
        _camera.adjustProjection(width, height);
    }


    // -+- GETTERS -+- //

    public System getECS() {
        return p_ECS;
    }


}