package internal.entity_component_system.specifics.collision;


import internal.entity_component_system.A_Component;
import internal.entity_component_system.A_Processor;
import internal.entity_component_system.System;
import internal.entity_component_system.events.ProcessorAddedEvent;
import internal.entity_component_system.events.ProcessorRemovedEvent;
import internal.entity_component_system.specifics.collision.data.A_CollisionData;
import internal.entity_component_system.specifics.collision.data.ObjectData;
import internal.entity_component_system.specifics.collision.dependencies.calculator.I_CollisionCalculator;
import internal.entity_component_system.specifics.collision.dependencies.spatial_partitioner.I_SpatialPartitioner;
import internal.entity_component_system.specifics.collision.events.ContainerCollisionEvent;
import internal.entity_component_system.specifics.collision.events.ObjectCollisionEvent;
import internal.entity_component_system.specifics.hitbox.HitboxProcessor;
import internal.entity_component_system.specifics.position.PositionComponent;
import internal.entity_component_system.specifics.hitbox.A_HitboxComponent;
import internal.entity_component_system.specifics.position.PositionProcessor;
import internal.events.Event;
import internal.events.EventHandler;
import internal.events.EventListeningPort;
import internal.rendering.container.Container;
import internal.rendering.container.Scene;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class CollisionProcessor extends A_Processor<CollisionComponent> {


    // -+- CREATION -+- //

    public CollisionProcessor(I_SpatialPartitioner spatialPartitioner, I_CollisionCalculator collisionCalculator) {
        _SPATIAL_PARTITIONER = spatialPartitioner;
        _COLLISION_CALCULATOR = collisionCalculator;
    }

    @Override
    protected void p_init(System system, Scene scene) {
        _port = scene.getEventHandler().register();
        _port.registerFunction(this::onProcessorAdded, List.of(ProcessorAddedEvent.class));
        _port.registerFunction(this::onProcessorRemoved, List.of(ProcessorRemovedEvent.class));
    }
    @Override
    protected void p_kill(System system, Scene scene) {

    }

    @Override
    protected void p_receiveRequiredProcessors(HashMap<Class<? extends CollisionComponent>, A_Processor<?>> requiredProcessors) {
        _hitboxProcessor = (HitboxProcessor) requiredProcessors.get(A_HitboxComponent.class);
        _positionProcessor = (PositionProcessor) requiredProcessors.get(PositionComponent.class);
    }

    // -+- PARAMETERS -+- //

    // FINALS //

    private final I_SpatialPartitioner _SPATIAL_PARTITIONER;
    private final I_CollisionCalculator _COLLISION_CALCULATOR;

    // NON-FINALS //

    private HitboxProcessor _hitboxProcessor;
    private PositionProcessor _positionProcessor;

    private EventListeningPort _port;


    // -+- UPDATE LOOP -+- //

    @Override
    protected void p_internalUpdate(Collection<CollisionComponent> validComponents, System system, Scene scene) {
        if (_positionProcessor == null || _hitboxProcessor == null) return;

        HashMap<ObjectData, HashSet<ObjectData>> pairs;

        A_HitboxComponent hitboxComponent;
        PositionComponent positionComponent;
        ObjectData objectData;

        pairs = new HashMap<>();

        for (CollisionComponent component : validComponents) {
            hitboxComponent = _hitboxProcessor.getComponent(component.owningEntity);
            positionComponent = _positionProcessor.getComponent(component.owningEntity);
            objectData = new ObjectData(hitboxComponent, positionComponent);

            h_checkAgainstContainer(objectData, scene, scene.getEventHandler());
            h_checkAgainstObjects(objectData, _SPATIAL_PARTITIONER.getCollidingObjects(objectData), pairs, scene.getEventHandler());
        }
    }

    private void h_checkAgainstContainer(ObjectData object, Container container, EventHandler eventHandler) {
        if (_COLLISION_CALCULATOR.isCollidingWith(object, container)) {
            eventHandler.pushEvent(new ContainerCollisionEvent(_COLLISION_CALCULATOR.getCollisionAxis(object, container), object, container));
        }
    }
    private void h_checkAgainstObjects(ObjectData object, Collection<ObjectData> objects, HashMap<ObjectData, HashSet<ObjectData>> pairs, EventHandler eventHandler) {
        A_CollisionData.COLLISION_AXIS collisionAxis;

        pairs.put(object, new HashSet<>());

        for (ObjectData objectB : objects) {
            if (pairs.containsKey(objectB)) continue;
            if (!_COLLISION_CALCULATOR.isCollidingWith(object, objectB)) continue;

            pairs.get(object).add(objectB);

            collisionAxis = _COLLISION_CALCULATOR.getCollisionAxis(object, objectB);

            h_pushCollisionEvents(object, objectB, collisionAxis, eventHandler);
        }
    }
    private void h_pushCollisionEvents(ObjectData object, ObjectData collidingObject, A_CollisionData.COLLISION_AXIS collisionAxis, EventHandler eventHandler) {
        eventHandler.pushEvent(new ObjectCollisionEvent(collisionAxis, object, collidingObject));
        eventHandler.pushEvent(new ObjectCollisionEvent(collisionAxis, collidingObject, object));
    }


    // -+- COMPONENT MANAGEMENT -+- //

    @Override
    protected boolean p_isComponentValid(CollisionComponent component) {
        return _positionProcessor.hasEntity(component.owningEntity) && _hitboxProcessor.hasEntity(component.owningEntity);
    }


    // -+- PROCESSOR MANAGEMENT -+- //

    public void onProcessorAdded(Event event) {
        if (!(event instanceof ProcessorAddedEvent)) return;

        if (((ProcessorAddedEvent) event).processor instanceof HitboxProcessor) _hitboxProcessor = (HitboxProcessor) ((ProcessorAddedEvent) event).processor;
        else if (((ProcessorAddedEvent) event).processor instanceof PositionProcessor) _positionProcessor = (PositionProcessor) ((ProcessorAddedEvent) event).processor;
    }
    public void onProcessorRemoved(Event event) {
        if (!(event instanceof ProcessorRemovedEvent)) return;

        if (((ProcessorRemovedEvent) event).processor instanceof HitboxProcessor) _hitboxProcessor = null;
        else if (((ProcessorRemovedEvent) event).processor instanceof PositionProcessor) _positionProcessor = null;
    }


    // -+- GETTERS -+- //

    @Override
    protected Collection<Class<? extends CollisionComponent>> p_getProcessedComponentClasses() {
        return List.of(CollisionComponent.class);
    }
    @Override
    protected Collection<Class<? extends A_Component>> p_getRequiredComponentClasses() {
        return List.of(A_HitboxComponent.class, PositionComponent.class);
    }


}