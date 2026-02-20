package internal.entity_component_system.specifics.render;


import internal.batch.BatchSystem;
import internal.entity_component_system.A_Component;
import internal.entity_component_system.A_Processor;
import internal.entity_component_system.System;
import internal.entity_component_system.events.ProcessorAddedEvent;
import internal.entity_component_system.events.ProcessorRemovedEvent;
import internal.entity_component_system.specifics.position.PositionComponent;
import internal.entity_component_system.specifics.position.PositionProcessor;
import internal.events.EventFilter;
import internal.events.I_Event;
import internal.events.implementations.ActiveEventPort;
import internal.events.implementations.Event;
import internal.rendering.container.A_Scene;
import internal.rendering.mesh.A_Mesh;
import org.joml.Vector2d;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class RenderProcessor extends A_Processor<RenderComponent> {


    // -+- PARAMETERS -+- //

    // FINALS //

    private BatchSystem _system;

    // NON-FINALS //

    private ActiveEventPort _port;
    private PositionProcessor _positionProcessor;


    @Override
    protected void p_init(System system, A_Scene scene) {
        _port = new ActiveEventPort(new EventFilter());
        scene.SYSTEMS.EVENT_HANDLER.register(_port);
        _port.addCallback(this::onSystemAdded);
        _port.addCallback(this::onSystemRemoved);

        _system = new BatchSystem(scene.getCamera());
    }
    @Override
    protected void p_kill(System system, A_Scene scene) {
        scene.SYSTEMS.EVENT_HANDLER.deregister(_port);
        _port = null;
    }


    @Override
    protected void p_receiveRequiredProcessors(HashMap<Class<? extends A_Component>, A_Processor<?>> requiredProcessors) {
        _positionProcessor = (PositionProcessor) requiredProcessors.get(PositionComponent.class);
    }


    @Override
    protected void p_internalUpdate(Collection<RenderComponent> validComponents, System system, A_Scene scene) {
        for (RenderComponent component : validComponents) {
            if (component.positionDependent) h_updatePosition(component.renderMesh, _positionProcessor.getComponent(component.owningEntity).position);

            _system.updateMesh(component.renderMesh, component.shaderPath);
        }

        _system.update();
    }

    private void h_updatePosition(A_Mesh mesh, Vector2d to) {
        Vector2d origin;

        origin = new Vector2d(mesh.vertices[0], mesh.vertices[1]);

        double xDist, yDist;

        xDist = to.x - origin.x;
        yDist = to.y - origin.y;

        if (xDist == 0 && yDist == 0) return;

        for (int index = 1; index < mesh.vertices.length; index += 5) {
            mesh.vertices[index - 1] += xDist;
            mesh.vertices[index] += yDist;
        }
    }


    @Override
    protected boolean p_isComponentValid(RenderComponent component) {
        if (component.positionDependent) {
            if (_positionProcessor == null) return false;

            return _positionProcessor.hasEntity(component.owningEntity);
        }

        return true;
    }


    @Override
    protected void p_onComponentActivated(RenderComponent component) {
        _system.addMesh(component.renderMesh, component.shaderPath);
    }
    @Override
    protected void p_onComponentDeactivated(RenderComponent component) {
        _system.rmvMesh(component.renderMesh);
    }

    @Override
    protected void p_onComponentAdded(RenderComponent component) {
        _system.addMesh(component.renderMesh, component.shaderPath);
    }
    @Override
    protected void p_onComponentRemoved(RenderComponent component) {
        _system.rmvMesh(component.renderMesh);
    }


    public void onSystemAdded(I_Event event) {
        if (!(event instanceof ProcessorAddedEvent)) return;

        if (((ProcessorAddedEvent) event).processor instanceof PositionProcessor) {
            _positionProcessor = (PositionProcessor) ((ProcessorAddedEvent) event).processor;
        }
    }
    public void onSystemRemoved(I_Event event) {
        if (!(event instanceof ProcessorRemovedEvent)) return;

        if (((ProcessorRemovedEvent) event).processor instanceof PositionProcessor) {
            _positionProcessor = null;
        }
    }


    @Override
    protected Collection<Class<? extends RenderComponent>> p_getProcessedComponentClasses() {
        return List.of(RenderComponent.class);
    }
    @Override
    protected Collection<Class<? extends A_Component>> p_getRequiredComponentClasses() {
        return List.of(PositionComponent.class);
    }

    public BatchSystem getBatchSystem() {
        return _system;
    }


}