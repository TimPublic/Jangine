package internal.top_classes;


import internal.entity_component_system.A_Component;
import internal.entity_component_system.System;
import internal.events.EventListeningPort;
import internal.rendering.container.A_Scene;


public abstract class A_Entity {


    // -+- CREATION -+- //

    public A_Entity(A_Scene scene) {
        _ECS = scene.SYSTEMS.ECS;
        _ID = _ECS.addEntity();

        p_PORT = scene.SYSTEMS.EVENT_HANDLER.register();
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final System _ECS;
    private final int _ID;

    protected final EventListeningPort p_PORT;


    // -+- COMPONENT MANAGEMENT -+- //

    protected void p_addComponent(A_Component component) {
        _ECS.addComponentToEntity(_ID, component, false);
    }


    // -+- UPDATE LOOP -+- //

    public abstract void update(double deltaTime);


    // -+- GETTERS -+- //

    public int getId() {
        return _ID;
    }


}