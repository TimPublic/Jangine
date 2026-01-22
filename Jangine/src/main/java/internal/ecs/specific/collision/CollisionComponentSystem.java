package internal.ecs.specific.collision;


import internal.ecs.ECS;
import internal.ecs.ECS_Component;
import internal.ecs.ECS_ComponentSystem;
import internal.ecs.specific.collision.calculator.I_Calculator;
import internal.ecs.specific.collision.data.ComponentCollisionData;
import internal.ecs.specific.collision.data.CollisionData;
import internal.ecs.specific.collision.data.ContainerCollisionData;
import internal.ecs.specific.collision.partitioner.I_Partitioner;
import internal.ecs.specific.position.PositionComponent;
import internal.ecs.specific.position.PositionComponentSystem;
import internal.ecs.specific.size.SizeComponent;
import internal.ecs.specific.size.SizeComponentSystem;
import internal.events.collision.ComponentCollisionEvent;
import internal.events.collision.WindowCollisionEvent;
import org.joml.Vector2d;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;


public class CollisionComponentSystem<T extends CollisionComponent> extends ECS_ComponentSystem<CollisionComponent> {


    private final HashSet<Consumer<ComponentCollisionData>> _componentCollisionCallbacks;
    private final HashSet<Consumer<ContainerCollisionData>> _containerCollisionCallbacks;

    private final I_Calculator _calculator;
    private final I_Partitioner _partitioner;

    private Vector2d _containerPosition;
    private double _containerWidth, _containerHeight;

    private PositionComponentSystem<?> _positionSystem;
    private SizeComponentSystem<?> _sizeSystem;


    // -+- CREATION -+- //

    public CollisionComponentSystem(Vector2d containerPosition, double containerWidth, double containerHeight, I_Calculator calculator, I_Partitioner partitioner) {
        _componentCollisionCallbacks = new HashSet<>();
        _containerCollisionCallbacks = new HashSet<>();

        _containerPosition = containerPosition;
        _containerWidth = containerWidth;
        _containerHeight = containerHeight;

        _calculator = calculator;
        _partitioner = partitioner;

        _partitioner.init(_containerPosition, _containerWidth, _containerHeight);
    }


    // -+- UPDATE LOOP -+- //

    @Override
    protected void _internalUpdate(ECS system) {
        if (_positionSystem == null) {return;}
        if (_sizeSystem == null) {return;}

        // Create pairing hashmap-
        HashMap<CollisionComponent, HashSet<CollisionComponent>> pairs;

        pairs = new HashMap<>();

        for (CollisionComponent component : _validComponents) {
            pairs.put(component, new HashSet<>());

            if (_calculator.collidesWithContainer(component, _containerPosition, _containerWidth, _containerHeight)) {
                CollisionData.COLLISION_AXIS collisionAxis;

                collisionAxis = _calculator.getCollisionAxisWithContainer(component, _containerPosition, _containerWidth, _containerHeight);

                _pushContainerCollision(collisionAxis, component);
            }

            PositionComponent comPosition, collPosition;
            SizeComponent comSize, collSize;

            comPosition = _positionSystem.getComponent(component.owningEntity);
            comSize = _sizeSystem.getComponent(component.owningEntity);

            for (CollisionComponent collidingComponent : _partitioner.getPossibleCollisions(component)) {
                // We do not need to check for the "component" as a key, as it is the loop for the component,
                // so with this component as the key, no collision will have happened with this "collidingComponent".
                // But the "collidingComponent" could have already been checked, which is why we check, if this
                // "component" was already checked in the "collidingComponent"s' loop.
                if (pairs.containsKey(collidingComponent)) {if (pairs.get(collidingComponent).contains(component)) {continue;}}

                collPosition = _positionSystem.getComponent(collidingComponent.owningEntity);
                collSize = _sizeSystem.getComponent(collidingComponent.owningEntity);

                if (_calculator.collidesWithComponent(component, comPosition, comSize, collidingComponent, collPosition, collSize)) {
                    pairs.get(component).add(collidingComponent);
                }
            }

            for (CollisionComponent collidingComponent : pairs.get(component)) {
                CollisionData.COLLISION_AXIS collisionAxis;

                collPosition = _positionSystem.getComponent(collidingComponent.owningEntity);
                collSize = _sizeSystem.getComponent(collidingComponent.owningEntity);

                // Will obviously collide on the same axis.
                collisionAxis = _calculator.getCollisionAxisWithComponent(component, comPosition, comSize, collidingComponent, collPosition, collSize);

                _pushComponentCollision(collisionAxis, component, collidingComponent);
                _pushComponentCollision(collisionAxis, collidingComponent, component);
            }
        }
    }


    // -+- NOTIFICATION MANAGEMENT -+- //

    public void registerComponentCollisionCallback(Consumer<ComponentCollisionData> callback) {
        if (callback != null) {_componentCollisionCallbacks.add(callback);}
    }
    public void deregisterComponentCollisionCallback(Consumer<ComponentCollisionData> callback) {
        _componentCollisionCallbacks.remove(callback);
    }
    public void registerContainerCollisionCallback(Consumer<ContainerCollisionData> callback) {
        if (callback != null) {_containerCollisionCallbacks.add(callback);}
    }
    public void deregisterContainerCollisionCallback(Consumer<ContainerCollisionData> callback) {
        _containerCollisionCallbacks.remove(callback);
    }

    protected void _pushComponentCollision(CollisionData.COLLISION_AXIS collisionAxis, CollisionComponent component, CollisionComponent collidingComponent) {
        ComponentCollisionData componentData;

        componentData = new ComponentCollisionData(component, collisionAxis, collidingComponent);

        for (Consumer<ComponentCollisionData> callback : _componentCollisionCallbacks) {callback.accept(componentData);}

        _eventHandler.pushEvent(new ComponentCollisionEvent(componentData));
    }
    protected void _pushContainerCollision(CollisionData.COLLISION_AXIS collisionAxis, CollisionComponent component) {
        ContainerCollisionData windowData;

        windowData = new ContainerCollisionData(component, collisionAxis);

        for (Consumer<ContainerCollisionData> callback : _containerCollisionCallbacks) {callback.accept(windowData);}

        _eventHandler.pushEvent(new WindowCollisionEvent(windowData));
    }


    // -+- COMPONENT MANAGEMENT -+- //


    @Override
    protected void _onComponentValidated(CollisionComponent component) {
        PositionComponent positionComponent;
        SizeComponent sizeComponent;

        positionComponent = _positionSystem.getComponent(component.owningEntity);
        sizeComponent = _sizeSystem.getComponent(component.owningEntity);

        _partitioner.addComponent(component, positionComponent, sizeComponent);
    }
    @Override
    protected void _onComponentInvalidated(CollisionComponent component) {
        _partitioner.rmvComponent(component);
    }

    protected boolean _isComponentValid(CollisionComponent component) {
        if (_positionSystem.getComponent(component.owningEntity) == null) {return false;}
        if (_sizeSystem.getComponent(component.owningEntity) == null) {return false;}

        return true;
    }


    // -+- CALLBACKS -+- //


    @Override
    public void onComponentSystemAdded(ECS_ComponentSystem componentSystem) {
        if (componentSystem == null) {return;}

        if (componentSystem instanceof PositionComponentSystem<?>) {
            _positionSystem = (PositionComponentSystem<?>) componentSystem;

            return;
        }
        if (componentSystem instanceof SizeComponentSystem<?>) {
            _sizeSystem = (SizeComponentSystem<?>) componentSystem;

            return;
        }
    }
    @Override
    public void onComponentSystemRemoved(ECS_ComponentSystem componentSystem) {
        if (componentSystem == null) {return;}

        if (componentSystem instanceof PositionComponentSystem<?>) {
            _positionSystem = null;

            return;
        }
        if (componentSystem instanceof SizeComponentSystem<?>) {
            _sizeSystem = null;

            return;
        }
    }


    // -+- GETTERS -+- //

    @Override
    public Collection<Class<? extends ECS_Component>> getRequirements() {
        return List.of(PositionComponent.class, SizeComponent.class);
    }


}