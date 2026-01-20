package internal.ecs.specific.texture;


import internal.ecs.ECS;
import internal.ecs.ECS_ComponentSystem;


public class TextureComponentSystem<T extends TextureComponent> extends ECS_ComponentSystem<TextureComponent> {


    @Override
    protected void _internalUpdate(ECS system) {

    }


    @Override
    protected boolean _isComponentValid(TextureComponent component) {
        return true;
    }


}