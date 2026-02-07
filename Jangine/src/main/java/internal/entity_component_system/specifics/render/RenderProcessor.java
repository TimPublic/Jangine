package internal.entity_component_system.specifics.render;


import internal.batch.A_BatchProcessor;
import internal.batch.BatchSystem;
import internal.entity_component_system.A_Component;
import internal.entity_component_system.A_Processor;
import internal.entity_component_system.System;
import internal.entity_component_system.events.ProcessorAddedEvent;
import internal.entity_component_system.events.ProcessorRemovedEvent;
import internal.entity_component_system.specifics.position.PositionComponent;
import internal.entity_component_system.specifics.position.PositionProcessor;
import internal.events.Event;
import internal.events.EventListeningPort;
import internal.rendering.container.A_Scene;
import internal.rendering.mesh.A_Mesh;
import org.joml.Vector2d;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class RenderProcessor extends A_Processor<RenderComponent> {


    public RenderProcessor(Collection<? extends A_BatchProcessor<?>> processors) {
        super();

        _SYSTEM = new BatchSystem();

        for (A_BatchProcessor<?> processor : processors) {
            _SYSTEM.addProcessor(processor, false);
        }
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final BatchSystem _SYSTEM;

    // NON-FINALS //

    private EventListeningPort _PORT;
    private PositionProcessor _positionProcessor;


    @Override
    protected void p_init(System system, A_Scene scene) {
        _PORT = scene.SYSTEMS.EVENT_HANDLER.register();

        _PORT.registerFunction(this::onSystemAdded, List.of(ProcessorAddedEvent.class));
        _PORT.registerFunction(this::onSystemRemoved, List.of(ProcessorRemovedEvent.class));
    }
    @Override
    protected void p_kill(System system, A_Scene scene) {
        scene.SYSTEMS.EVENT_HANDLER.deregister(_PORT);
    }


    @Override
    protected void p_receiveRequiredProcessors(HashMap<Class<? extends A_Component>, A_Processor<?>> requiredProcessors) {
        _positionProcessor = (PositionProcessor) requiredProcessors.get(PositionComponent.class);
    }


    @Override
    protected void p_internalUpdate(Collection<RenderComponent> validComponents, System system, A_Scene scene) {
        for (RenderComponent component : validComponents) {
            if (component.positionDependent) h_updatePosition(component.renderMesh, _positionProcessor.getComponent(component.owningEntity).position);

            _SYSTEM.updateMesh(component.renderMesh, component.shaderPath);
        }

        _SYSTEM.update();
    }

    private void h_updatePosition(A_Mesh mesh, Vector2d to) {
        Vector2d origin;

        origin = new Vector2d(mesh.vertices[0], mesh.vertices[1]);

        double xDist, yDist;

        xDist = origin.distance(to);
        yDist = origin.distance(to);

        if (xDist == 0 && yDist == 0) return;

        for (int index = 1; index < mesh.vertices.length; index++) {
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
        _SYSTEM.addMesh(component.renderMesh, component.shaderPath);
    }
    @Override
    protected void p_onComponentDeactivated(RenderComponent component) {
        _SYSTEM.rmvMesh(component.renderMesh);
    }

    @Override
    protected void p_onComponentAdded(RenderComponent component) {
        _SYSTEM.addMesh(component.renderMesh, component.shaderPath);
    }
    @Override
    protected void p_onComponentRemoved(RenderComponent component) {
        _SYSTEM.rmvMesh(component.renderMesh);
    }


    public void onSystemAdded(Event event) {
        if (((ProcessorAddedEvent) event).processor instanceof PositionProcessor) {
            _positionProcessor = (PositionProcessor) ((ProcessorAddedEvent) event).processor;
        }
    }
    public void onSystemRemoved(Event event) {
        if (((ProcessorAddedEvent) event).processor instanceof PositionProcessor) {
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


}