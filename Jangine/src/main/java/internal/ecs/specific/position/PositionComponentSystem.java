package internal.ecs.specific.position;


import internal.ecs.*;

import java.util.Collection;
import java.util.List;


public class PositionComponentSystem<T extends ECS_Component> extends ECS_ComponentSystem<PositionComponent> {


    @Override
    protected void _internalUpdate(ECS system) {

    }


    @Override
    protected boolean _isComponentValid(PositionComponent component) {
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