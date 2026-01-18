package internal.rendering.container;


import internal.ecs.ECS;
import internal.ecs.specific.rendering.RenderComponent;
import internal.ecs.specific.rendering.RenderComponentSystem;
import internal.ecs.specific.rendering.texture.TexturedMeshComponent;
import internal.ecs.specific.rendering.texture.TexturedMeshComponentSystem;
import internal.ecs.specific.texture.TextureComponent;
import internal.ecs.specific.texture.TextureComponentSystem;
import internal.events.EventListeningPort;
import internal.events.Event;
import internal.events.EventHandler;
import internal.main.Engine;
import internal.rendering.camera.Camera2D;
import internal.rendering.mesh.TexturedMesh;
import internal.rendering.shader.ShaderTest;

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
public class Scene {


    private final EventHandler _windowEventHandler;

    private final EventHandler _ownEventHandler;
    private final EventListeningPort _ownEventHandlerListeningPort;

    private ArrayList<EventListeningPort> _windowListeningPorts;
    private ArrayList<EventListeningPort> _engineListeningPorts;


    private Camera2D _camera;


    ShaderTest test;
    ECS ecs;
    int entityID;


    // private ArrayList<RenderObject> _renderObjects;


    public Scene(final EventHandler windowEventHandler, int width, int height, boolean active) {
        _windowEventHandler = windowEventHandler;

        _ownEventHandler = new EventHandler();
        _ownEventHandlerListeningPort = _windowEventHandler.register();
        _ownEventHandlerListeningPort.registerFunction(_ownEventHandler::pushEvent, List.of(Event.class));
        _ownEventHandlerListeningPort.setActive(active);

        _windowListeningPorts = new ArrayList<>();
        _engineListeningPorts = new ArrayList<>();

        _camera = new Camera2D(width, height);

        test = new ShaderTest();

        float[] vertices = new float[]{
                /* Position */    /* uvCoordinates */  /* textureIndex */
                0.0f,   0.0f,   0.0f, 0.0f,          0.0f,
                100.0f,   0.0f,   1.0f, 0.0f,          1.0f,
                100.0f, 100.0f,   1.0f, 1.0f,          1.0f,
                0.0f, 100.0f,   0.0f, 1.0f,          0.0f,
        };
        int[] indices = new int[]{
                0, 1, 2,
                2, 3, 0,
        };

        ecs = new ECS();

        ecs.addComponentSystem(new RenderComponentSystem<>(), RenderComponent.class);
        ecs.addComponentSystem(new TextureComponentSystem<>(), TextureComponent.class);
        ecs.addComponentSystem(new TexturedMeshComponentSystem<>(), TexturedMeshComponent.class);

        entityID = ecs.addEntity();

        ecs.addComponent(entityID, new RenderComponent(RenderComponent.RENDER_TYPE.TEXTURE));
        ecs.addComponent(entityID, new TextureComponent("assets/test_image.png"));
        ecs.addComponent(entityID, new TexturedMeshComponent(new TexturedMesh(vertices, indices)));

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

        ecs.update();

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
        return _ownEventHandler;
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


}