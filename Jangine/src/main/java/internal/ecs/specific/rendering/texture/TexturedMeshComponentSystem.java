package internal.ecs.specific.rendering.texture;


import internal.ecs.ECS;
import internal.ecs.ECS_ComponentSystem;
import internal.ecs.specific.position.PositionComponent;


public class TexturedMeshComponentSystem<T extends TexturedMeshComponent> extends ECS_ComponentSystem<TexturedMeshComponent> {


    @Override
    protected void _internalUpdate(ECS system) {

    }


    @Override
    protected boolean _isComponentValid(TexturedMeshComponent component) {
        return true;
    }


}