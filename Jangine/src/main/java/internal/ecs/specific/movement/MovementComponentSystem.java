package internal.ecs.specific.movement;


import internal.ecs.ECS;
import internal.ecs.ECS_Component;
import internal.ecs.A_ComponentSystem;
import internal.ecs.specific.position.PositionComponent;
import internal.ecs.specific.position.PositionComponentSystem;

import java.util.Collection;
import java.util.List;


public class MovementComponentSystem<T extends ECS_Component> extends A_ComponentSystem<MovementComponent> {


    private PositionComponentSystem<?> _positionSystem;


    @Override
    protected void p_internalUpdate(ECS system) {
        if (_positionSystem == null) {return;}

        for (MovementComponent component : p_components.values()) {
            PositionComponent positionComponent;

            positionComponent = _positionSystem.getComponent(component.owningEntity);
            if (positionComponent == null) {continue;}

            positionComponent.position.add(component.direction.x * component.speed, component.direction.y * component.speed);
        }
    }


    @Override
    protected boolean p_isComponentValid(MovementComponent component) {
        return true;
    }


    // -+- CALLBACKS -+- //


    @Override
    public void onComponentSystemAdded(A_ComponentSystem componentSystem) {
        if (componentSystem == null) {return;}

        if (componentSystem instanceof PositionComponentSystem<?>) {
            _positionSystem = (PositionComponentSystem<?>) componentSystem;
        }
    }
    @Override
    public void onComponentSystemRemoved(A_ComponentSystem componentSystem) {
        if (componentSystem == null) {return;}

        if (componentSystem instanceof PositionComponentSystem<?>) {
            _positionSystem = null;
        }
    }


    // -+- GETTERS -+- //

    @Override
    public Collection<Class<? extends ECS_Component>> getRequirements() {
        return List.of(PositionComponent.class);
    }


}