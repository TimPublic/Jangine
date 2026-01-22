package internal.ecs.specific.texture;


import internal.ecs.ECS;
import internal.ecs.ECS_Component;
import internal.ecs.ECS_ComponentSystem;

import java.util.Collection;
import java.util.List;


public class TextureComponentSystem<T extends TextureComponent> extends ECS_ComponentSystem<TextureComponent> {


    @Override
    protected void _internalUpdate(ECS system) {

    }


    @Override
    protected boolean _isComponentValid(TextureComponent component) {
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