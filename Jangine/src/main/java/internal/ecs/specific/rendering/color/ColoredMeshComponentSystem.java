package internal.ecs.specific.rendering.color;


import internal.ecs.ECS;
import internal.ecs.ECS_ComponentSystem;
import internal.ecs.specific.position.PositionComponent;


public class ColoredMeshComponentSystem<T extends ColoredMeshComponent> extends ECS_ComponentSystem<ColoredMeshComponent> {


    @Override
    protected void _internalUpdate(ECS system) {

    }


    @Override
    protected boolean _isComponentValid(ColoredMeshComponent component) {
        return true;
    }


}