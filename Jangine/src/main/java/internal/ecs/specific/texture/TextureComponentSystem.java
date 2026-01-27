package internal.ecs.specific.texture;


import internal.ecs.ECS;
import internal.ecs.ECS_Component;
import internal.ecs.A_ComponentSystem;

import java.util.Collection;
import java.util.List;


public class TextureComponentSystem<T extends TextureComponent> extends A_ComponentSystem<TextureComponent> {


    @Override
    protected void p_internalUpdate(ECS system) {

    }


    @Override
    protected boolean p_isComponentValid(TextureComponent component) {
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