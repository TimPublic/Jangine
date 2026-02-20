package internal.top_classes;


import internal.entity_component_system.A_Component;
import internal.entity_component_system.System;
import internal.events.EventFilter;
import internal.events.implementations.ActiveEventPort;
import internal.rendering.container.A_Scene;


public abstract class A_Entity {


    // -+- CREATION -+- //

    public A_Entity(A_Scene scene) {
        p_ECS = scene.SYSTEMS.ECS;
        _ID = p_ECS.addEntity();

        p_PORT = new ActiveEventPort(new EventFilter());
        scene.SYSTEMS.EVENT_HANDLER.register(p_PORT);
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    protected final System p_ECS;
    private final int _ID;

    protected final ActiveEventPort p_PORT;


    // -+- COMPONENT MANAGEMENT -+- //

    protected void p_addComponent(A_Component component) {
        p_ECS.addComponentToEntity(_ID, component, false);
    }


    // -+- UPDATE LOOP -+- //

    public abstract void update(double deltaTime);


    // -+- GETTERS -+- //

    public int getId() {
        return _ID;
    }


}