package internal.ecs.specific.size;


import internal.ecs.ECS;
import internal.ecs.ECS_ComponentSystem;


public class SizeComponentSystem<T extends SizeComponent> extends ECS_ComponentSystem<SizeComponent> {


    @Override
    protected void _internalUpdate(ECS system) {

    }


    @Override
    protected boolean _isComponentValid(SizeComponent component) {
        return true;
    }


}