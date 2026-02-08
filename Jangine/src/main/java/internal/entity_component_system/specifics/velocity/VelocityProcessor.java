package internal.entity_component_system.specifics.velocity;


import internal.entity_component_system.A_Component;
import internal.entity_component_system.events.ProcessorAddedEvent;
import internal.entity_component_system.events.ProcessorRemovedEvent;
import internal.entity_component_system.specifics.position.PositionComponent;
import internal.entity_component_system.specifics.position.PositionProcessor;
import internal.entity_component_system.A_Processor;
import internal.entity_component_system.System;
import internal.events.Event;
import internal.events.EventListeningPort;
import internal.rendering.container.A_Scene;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class VelocityProcessor extends A_Processor<VelocityComponent> {


    @Override
    protected void p_init(System system, A_Scene scene) {
        _port = scene.SYSTEMS.EVENT_HANDLER.register();

        _port.registerFunction(this::onProcessorAdded, List.of(ProcessorAddedEvent.class));
        _port.registerFunction(this::onProcessorRemoved, List.of(ProcessorRemovedEvent.class));
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
    protected void p_internalUpdate(Collection<VelocityComponent> validComponents, System system, A_Scene scene) {
        PositionComponent positionComponent;

        for (VelocityComponent component : validComponents) {
            positionComponent = _positionProcessor.getComponent(component.owningEntity);

            positionComponent.position.add(component.VELOCITY);
        }
    }


    public void onProcessorAdded(Event event) {
        ProcessorAddedEvent pae;

        pae = (ProcessorAddedEvent) event;

        if (pae.processor instanceof PositionProcessor) {
            _positionProcessor = (PositionProcessor) pae.processor;
        }
    }
    public void onProcessorRemoved(Event event) {
        ProcessorAddedEvent pae;

        pae = (ProcessorAddedEvent) event;

        if (pae.processor instanceof PositionProcessor) {
            _positionProcessor = null;
        }
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    private PositionProcessor _positionProcessor;

    private EventListeningPort _port;


    @Override
    protected boolean p_isComponentValid(VelocityComponent component) {
        if (_positionProcessor == null) return false;

        return _positionProcessor.getComponent(component.owningEntity) != null;
    }

    @Override
    protected void p_onComponentAdded(VelocityComponent component) {

    }
    @Override
    protected void p_onComponentRemoved(VelocityComponent component) {

    }

    @Override
    protected void p_onComponentActivated(VelocityComponent component) {

    }
    @Override
    protected void p_onComponentDeactivated(VelocityComponent component) {

    }


    @Override
    protected Collection<Class<? extends VelocityComponent>> p_getProcessedComponentClasses() {
        return List.of(VelocityComponent.class);
    }
    @Override
    protected Collection<Class<? extends A_Component>> p_getRequiredComponentClasses() {
        return List.of(PositionComponent.class);
    }


}