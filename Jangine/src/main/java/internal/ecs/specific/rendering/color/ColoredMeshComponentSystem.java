package internal.ecs.specific.rendering.color;


import internal.ecs.ECS;
import internal.ecs.ECS_Component;
import internal.ecs.ECS_ComponentSystem;

import java.util.Collection;
import java.util.List;


public class ColoredMeshComponentSystem<T extends ColoredMeshComponent> extends ECS_ComponentSystem<ColoredMeshComponent> {


    @Override
    protected void _internalUpdate(ECS system) {

    }


    @Override
    protected boolean _isComponentValid(ColoredMeshComponent component) {
        return true;
    }


    // -+- CALLBACKS -+- //

    @Override
    public void onComponentSystemAdded(ECS_ComponentSystem componentSystem) {

    }
    @Override
    public void onComponentSystemRemoved(ECS_ComponentSystem componentSystem) {

    }


    // -+- GETTERS -+- //

    @Override
    public Collection<Class<? extends ECS_Component>> getRequirements() {
        return List.of();
    }


}