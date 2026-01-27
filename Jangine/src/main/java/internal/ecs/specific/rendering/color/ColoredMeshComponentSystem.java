package internal.ecs.specific.rendering.color;


import internal.ecs.ECS;
import internal.ecs.ECS_Component;
import internal.ecs.A_ComponentSystem;

import java.util.Collection;
import java.util.List;


public class ColoredMeshComponentSystem<T extends ColoredMeshComponent> extends A_ComponentSystem<ColoredMeshComponent> {


    @Override
    protected void p_internalUpdate(ECS system) {

    }


    @Override
    protected boolean p_isComponentValid(ColoredMeshComponent component) {
        return true;
    }


    // -+- CALLBACKS -+- //

    @Override
    public void onComponentSystemAdded(A_ComponentSystem componentSystem) {

    }
    @Override
    public void onComponentSystemRemoved(A_ComponentSystem componentSystem) {

    }


    // -+- GETTERS -+- //

    @Override
    public Collection<Class<? extends ECS_Component>> getRequirements() {
        return List.of();
    }


}