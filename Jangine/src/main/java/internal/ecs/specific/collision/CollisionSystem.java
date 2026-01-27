package internal.ecs.specific.collision;


import internal.ecs.A_ComponentSystem;
import internal.ecs.ECS;
import internal.ecs.ECS_Component;
import internal.ecs.specific.collision.data.object.CircleObject;
import internal.ecs.specific.collision.data.object.CollisionObject;
import internal.ecs.specific.collision.data.object.RectangleObject;
import internal.ecs.specific.position.PositionComponentSystem;
import internal.ecs.specific.size.SizeComponentSystem;
import internal.rendering.container.Container;

import java.util.Collection;
import java.util.List;


public class CollisionSystem extends A_ComponentSystem<CollisionComponent> {


    // -+- CREATION -+- //

    public CollisionSystem(Container container) {
        super();

        _CONTAINER = container;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final Container _CONTAINER;

    // NON-FINAL //

    private PositionComponentSystem<?> _positionSystem;


    // -+- UPDATE LOOP -+- //

    @Override
    protected void p_internalUpdate(ECS system) {
        if (_positionSystem == null || _sizeSystem == null) return;

        for (CollisionComponent component : p_validComponents) {
            CollisionObject objA, objB;
        }
    }

    private CollisionObject h_createObject(CollisionComponent component) {
        int entityId;

        entityId = component.owningEntity;

        switch (component.collisionType) {
            case RECTANGLE:
                return new RectangleObject(entityId, _positionSystem.getComponent(entityId).position, _sizeSystem.getComponent(entityId).width, _sizeSystem.getComponent(entityId).height);
            case CIRCLE:
                return new CircleObject(entityId, _positionSystem.getComponent(entityId).position, )
        }
        return null;
    }


    // -+- COMPONENT MANAGEMENT -+- //

    // VALIDATION //

    @Override
    protected void p_onComponentValidated(CollisionComponent component) {
        super.p_onComponentValidated(component);
    }
    @Override
    protected void p_onComponentInvalidated(CollisionComponent component) {
        super.p_onComponentInvalidated(component);
    }

    // ADDITION AND REMOVAL //

    @Override
    protected void p_onComponentAdded(CollisionComponent component) {
        super.p_onComponentAdded(component);
    }
    @Override
    protected void p_onComponentRemoved(CollisionComponent component) {
        super.p_onComponentRemoved(component);
    }

    // CHECKERS //

    @Override
    protected boolean p_isComponentValid(CollisionComponent component) {
        return false;
    }


    // -+- COMPONENT SYSTEM MANAGEMENT -+- //

    // ADDITION AND REMOVAL //

    @Override
    public void onComponentSystemAdded(A_ComponentSystem componentSystem) {
        if (componentSystem == null) return;

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
    public void onComponentSystemRemoved(A_ComponentSystem componentSystem) {
        if (componentSystem == null) return;

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
        return List.of();
    }


}