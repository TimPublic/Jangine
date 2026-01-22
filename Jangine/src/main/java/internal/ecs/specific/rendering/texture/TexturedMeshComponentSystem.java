package internal.ecs.specific.rendering.texture;


import internal.ecs.ECS;
import internal.ecs.ECS_Component;
import internal.ecs.ECS_ComponentSystem;
import internal.ecs.specific.position.PositionComponent;

import java.util.Collection;
import java.util.List;


public class TexturedMeshComponentSystem<T extends TexturedMeshComponent> extends ECS_ComponentSystem<TexturedMeshComponent> {


    @Override
    protected void _internalUpdate(ECS system) {

    }


    @Override
    protected boolean _isComponentValid(TexturedMeshComponent component) {
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