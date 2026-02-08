package internal.rendering.container;


import internal.entity_component_system.System;
import internal.events.EventHandler;
import internal.events.EventListeningPort;
import internal.input.KeyListener;
import internal.input.MouseListener;
import internal.main.Engine;
import internal.rendering.camera.Camera2D;
import internal.rendering.texture.Texture;
import internal.rendering.texture.dependencies.I_TextureLoader;
import internal.rendering.texture.dependencies.implementations.STBI_TextureLoader;
import internal.resource.ResourceManager;
import internal.util.PathManager;
import org.joml.Vector2d;


public abstract class A_Scene extends A_Container {


    // -+- CREATION -+- //

    public A_Scene(Vector2d position, double width, double height) {
        super(position, width, height);

        UTIL = new Util(p_getTextureLoaderImplementation());
        SYSTEMS = new Systems(this);

        setActive(false);

        _CAMERA = new Camera2D((int) width, (int) height);
    }

    public void init(Window window) {
        if (SYSTEMS.window_event_handler != null) {
            SYSTEMS.window_event_handler.deregister(SYSTEMS.window_port);
        }

        SYSTEMS.window_event_handler = window.getEventHandler();
        SYSTEMS.window_port = SYSTEMS.window_event_handler.register();

        SYSTEMS.window_port.setActive(false);
    }
    public void kill() {
        if (SYSTEMS.window_event_handler != null) {
            SYSTEMS.window_event_handler.deregister(SYSTEMS.window_port);

            SYSTEMS.window_event_handler = null;
            SYSTEMS.window_port = null;
        }
    }

    protected abstract I_TextureLoader p_getTextureLoaderImplementation();


    // -+- PARAMETERS -+- //

    private boolean _active;

    // FINALS //

    public final Util UTIL;
    public final Systems SYSTEMS;

    private final Camera2D _CAMERA;


    // -+- CLASSES -+- //

    public class Util {


        // -+- CREATION -+- //

        public Util(I_TextureLoader loader) {
            PATH_MANAGER = new PathManager();
            TEXTURE_LOADER = new ResourceManager<>(
                    path -> new Texture(path, new STBI_TextureLoader())
            );
        }


        // -+- PARAMETERS -+- //

        // FINALS //

        public final PathManager PATH_MANAGER;

        public final ResourceManager<Texture> TEXTURE_LOADER;


    }
    public class Systems {


        public Systems(A_Scene scene) {
            ECS = new System(scene);

            EVENT_HANDLER = new EventHandler();

            ENGINE_PORT = Engine.get().getEventHandler().register();
        }


        // -+- PARAMETERS -+- //

        // FINALS //

        public final System ECS;

        public final EventHandler EVENT_HANDLER;

        // NON-FINALS //

        public EventHandler window_event_handler;
        public EventListeningPort window_port;

        public final EventListeningPort ENGINE_PORT;


        // -+- UPDATE LOOP -+- //

        protected void p_update() {
            ECS.update();
        }


    }


    // -+- SCENE MANAGEMENT -+- //

    public void setActive(boolean value) {
        if (_active == value) return;

        _active = value;

        SYSTEMS.window_port.setActive(value);

        if (value) p_onActivation();
        else p_onDeactivation();
    }

    protected abstract void p_onActivation();
    protected abstract void p_onDeactivation();

    protected abstract void p_onAdded(Window window);
    protected abstract void p_onRemoved(Window window);


    // -+- UPDATE LOOP -+- //

    public abstract void update(double deltaTime);


    // -+- GETTERS -+- //

    public Camera2D getCamera() {
        return _CAMERA;
    }


    // -+- CHECKERS -+- //

    public boolean isActive() {
        return _active;
    }


}