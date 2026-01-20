package internal.ecs.specific.movement;


import internal.ecs.ECS;
import internal.ecs.ECS_Component;
import internal.ecs.ECS_ComponentSystem;
import internal.ecs.specific.position.PositionComponent;
import internal.ecs.specific.position.PositionComponentSystem;


public class MovementComponentSystem<T extends ECS_Component> extends ECS_ComponentSystem<MovementComponent> {


    @Override
    protected void _internalUpdate(ECS system) {
        ECS_ComponentSystem<? extends ECS_Component> componentSystem;
        PositionComponentSystem<?> positionSystem;

        componentSystem = system.getComponentSystem(PositionComponent.class);
        if (componentSystem == null) {return;}
        if (!(componentSystem instanceof PositionComponentSystem<?>)) {return;}

        positionSystem = (PositionComponentSystem<?>) componentSystem;

        for (MovementComponent component : _components.values()) {
            PositionComponent positionComponent;

            positionComponent = positionSystem.getComponent(component.owningEntity);
            if (positionComponent == null) {continue;}

            positionComponent.position.add(component.direction.x * component.speed, component.direction.y * component.speed);
        }
    }


}