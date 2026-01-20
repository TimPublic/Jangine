package internal.ecs.specific.position;


import internal.ecs.*;


public class PositionComponentSystem<T extends ECS_Component> extends ECS_ComponentSystem<PositionComponent> {


    @Override
    protected void _internalUpdate(ECS system) {

    }


    @Override
    protected boolean _isComponentValid(PositionComponent component) {
        return true;
    }


}