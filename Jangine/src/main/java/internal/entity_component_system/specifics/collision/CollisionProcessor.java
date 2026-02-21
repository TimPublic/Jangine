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
import internal.events.EventFilter;
import internal.events.EventMaster;
import internal.events.I_Event;
import internal.events.implementations.ActiveEventPort;
import internal.events.implementations.Event;
import internal.rendering.container.A_Scene;
import internal.rendering.container.A_Container;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class CollisionProcessor extends A_Processor<CollisionComponent> {


    // -+- CREATION -+- //

    public CollisionProcessor(I_SpatialPartitioner spatialPartitioner, I_CollisionCalculator collisionCalculator) {
        _SPATIAL_PARTITIONER = spatialPartitioner;
        _COLLISION_CALCULATOR = collisionCalculator;

        _OBJECTS = new HashMap<>();
    }

    @Override
    protected void p_init(System system, A_Scene scene) {
        _processorAddedPort = new ActiveEventPort(new EventFilter());
        _processorAddedPort.filter.addInterest(ProcessorAddedEvent.class);
        _processorAddedPort.addCallback(this::onProcessorAdded);
        scene.SYSTEMS.EVENT_HANDLER.register(_processorAddedPort);

        _processorRemovedPort = new ActiveEventPort(new EventFilter());
        _processorRemovedPort.filter.addInterest(ProcessorRemovedEvent.class);
        _processorRemovedPort.addCallback(this::onProcessorRemoved);
        scene.SYSTEMS.EVENT_HANDLER.register(_processorRemovedPort);
    }
    @Override
    protected void p_kill(System system, A_Scene scene) {

    }

    @Override
    protected void p_receiveRequiredProcessors(HashMap<Class<? extends A_Component>, A_Processor<?>> requiredProcessors) {
        _hitboxProcessor = (HitboxProcessor) requiredProcessors.get(A_HitboxComponent.class);
        _positionProcessor = (PositionProcessor) requiredProcessors.get(PositionComponent.class);
    }

    // -+- PARAMETERS -+- //

    // FINALS //

    private final I_SpatialPartitioner _SPATIAL_PARTITIONER;
    private final I_CollisionCalculator _COLLISION_CALCULATOR;

    private final HashMap<CollisionComponent, ObjectData> _OBJECTS;

    // NON-FINALS //

    private HitboxProcessor _hitboxProcessor;
    private PositionProcessor _positionProcessor;

    private ActiveEventPort _processorAddedPort;
    private ActiveEventPort _processorRemovedPort;


    // -+- UPDATE LOOP -+- //

    @Override
    protected void p_internalUpdate(Collection<CollisionComponent> validComponents, System system, A_Scene scene) {
        if (_positionProcessor == null || _hitboxProcessor == null) return;

        HashMap<Integer, HashSet<ObjectData>> pairs;

        A_HitboxComponent hitboxComponent;
        PositionComponent positionComponent;
        ObjectData objectData;

        _SPATIAL_PARTITIONER.update(scene);

        pairs = new HashMap<>();

        for (CollisionComponent component : validComponents) {
            objectData = _OBJECTS.get(component);

            if (objectData == null) continue;

            h_checkAgainstContainer(objectData, scene, scene.SYSTEMS.EVENT_HANDLER);
            h_checkAgainstObjects(objectData, _SPATIAL_PARTITIONER.getCollidingObjects(objectData), pairs, scene.SYSTEMS.EVENT_HANDLER);
        }
    }

    private void h_checkAgainstContainer(ObjectData object, A_Container container, EventMaster eventHandler) {
        if (_COLLISION_CALCULATOR.isCollidingWith(object, container)) {
            eventHandler.push(new ContainerCollisionEvent(_COLLISION_CALCULATOR.getCollisionAxis(object, container), object, container));
        }
    }
    private void h_checkAgainstObjects(ObjectData object, Collection<ObjectData> objects, HashMap<Integer, HashSet<ObjectData>> pairs, EventMaster eventHandler) {
        A_CollisionData.COLLISION_AXIS collisionAxis;

        pairs.put(object.hitboxComponent.owningEntity, new HashSet<>());

        for (ObjectData objectB : objects) {
            if (pairs.containsKey(objectB.hitboxComponent.owningEntity)) continue;
            if (!_COLLISION_CALCULATOR.isCollidingWith(object, objectB)) continue;

            pairs.get(object.hitboxComponent.owningEntity).add(objectB);

            collisionAxis = _COLLISION_CALCULATOR.getCollisionAxis(object, objectB);

            h_pushCollisionEvents(object, objectB, collisionAxis, eventHandler);
        }
    }
    private void h_pushCollisionEvents(ObjectData object, ObjectData collidingObject, A_CollisionData.COLLISION_AXIS collisionAxis, EventMaster eventHandler) {
        eventHandler.push(new ObjectCollisionEvent(collisionAxis, object, collidingObject));
        eventHandler.push(new ObjectCollisionEvent(collisionAxis, collidingObject, object));
    }


    // -+- COMPONENT MANAGEMENT -+- //

    @Override
    protected boolean p_isComponentValid(CollisionComponent component) {
        return _positionProcessor.hasEntity(component.owningEntity) && _hitboxProcessor.hasEntity(component.owningEntity);
    }

    @Override
    protected void p_onComponentAdded(CollisionComponent component) {
        ObjectData objectData;

        if (_hitboxProcessor == null || _positionProcessor == null) return;

        objectData = new ObjectData(_hitboxProcessor.getComponent(component.owningEntity), _positionProcessor.getComponent(component.owningEntity));

        _SPATIAL_PARTITIONER.addObject(objectData);
        _OBJECTS.put(component, objectData);
    }
    @Override
    protected void p_onComponentRemoved(CollisionComponent component) {
        _SPATIAL_PARTITIONER.rmvObject(_OBJECTS.get(component));
        _OBJECTS.remove(component);
    }
    @Override
    protected void p_onComponentActivated(CollisionComponent component) {

    }
    @Override
    protected void p_onComponentDeactivated(CollisionComponent component) {

    }


    // -+- PROCESSOR MANAGEMENT -+- //

    public void onProcessorAdded(I_Event event) {
        if (((ProcessorAddedEvent) event).processor instanceof HitboxProcessor) _hitboxProcessor = (HitboxProcessor) ((ProcessorAddedEvent) event).processor;
        else if (((ProcessorAddedEvent) event).processor instanceof PositionProcessor) _positionProcessor = (PositionProcessor) ((ProcessorAddedEvent) event).processor;
    }
    public void onProcessorRemoved(I_Event event) {
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