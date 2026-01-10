package internal.rendering;


import internal.events.JangineEventListeningPort;
import internal.events.JangineEvent;
import internal.events.JangineEventHandler;
import internal.main.JangineEngine;

import java.util.ArrayList;
import java.util.List;


// Scenes manage render-objects and are always home to exactly one window.
// They have their own event-handler, which only receives events, when
// the scene is active.
public class JangineScene {


    private final JangineEventHandler _windowEventHandler;

    private final JangineEventHandler _ownEventHandler;
    private final JangineEventListeningPort _ownEventHandlerListeningPort;

    private ArrayList<JangineEventListeningPort> _windowListeningPorts;
    private ArrayList<JangineEventListeningPort> _engineListeningPorts;


    private JangineCamera2D _camera;


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

        _onCreation();
    }


    // -+- LIFE-CYCLE -+- //

    // Gets called by the window, when a scene gets active.
    // Before this call, the event-handler is not distributing events.
    public final void activate() {
        _ownEventHandlerListeningPort.setActive(true);

        _onActivation();
    }
    // Gets called by the window, when a scene is no longer active.
    // If this call is not made, the event-handler will keep distributing Events.
    public final void deactivate() {
        _ownEventHandlerListeningPort.setActive(false);

        _onDeactivation();
    }
    // Gets called by the window, right before it is deleted.
    // Handles deregistering from services such as the windows' event-handler.
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

    // OVERWRITE |-> Gets called, every time the scene is activated.
    protected void _onActivation() {}
    // OVERWRITE |-> Gets called, every time the scene is deactivated.
    protected void _onDeactivation() {}
    // OVERWRITE |-> Gets called once, when the scene is created.
    protected void _onCreation() {}
    // OVERWRITE |-> Gets called once, when the scene is created.
    protected void _onKill() {}


    // -+- UPDATE-LOOP -+- //

    // Can be overwritten by children to implement custom behaviour on every frame.
    public final void update(double deltaTime) {
        _onUpdate(deltaTime);

        _render();
    }

    // OVERWRITE |-> Gets called every frame inside the update method, to implement custom update logic.
    protected void _onUpdate(double deltaTime) {}


    // -+- RENDERING -+- //

    // Gets called in the update method, to render general changes to the screen.
    private void _render() {
        test.run();

        _onRender();
    }

    // OVERWRITE |-> Gets called every frame in the render method, to implement custom rendering logic.
    protected void _onRender() {}


    // -+- EVENT-HANDLING -+- //

    // Returns a listening-port of the windows' event-handler and keeps a reference to that port,
    // to delete it, if the scene gets killed.
    public final JangineEventListeningPort getWindowEventListeningPort() {
        JangineEventListeningPort port;

        port = _windowEventHandler.register();

        _windowListeningPorts.add(port);

        return port;
    }
    // Removes a listening-port of the windows' event-handler, also deletes the owned reference.
    public final void removeWindowEventListeningPort(JangineEventListeningPort port) {
        _windowListeningPorts.remove(port);

        _windowEventHandler.deregister(port);
    }
    // Returns scenes' event-handler.
    public final JangineEventHandler getEventHandler() {
        return _ownEventHandler;
    }


    // -+- CAMERA-LOGIC -+- //

    // Sets the resolution of the camera to the specified width and height.
    public void setRes(int width, int height) {
        _camera.adjustProjection(width, height);
    }


}