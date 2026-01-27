package internal.ecs.specific.position;


import internal.ecs.*;

import java.util.Collection;
import java.util.List;


public class PositionComponentSystem<T extends ECS_Component> extends A_ComponentSystem<PositionComponent> {


    @Override
    protected void p_internalUpdate(ECS system) {

    }


    @Override
    protected boolean p_isComponentValid(PositionComponent component) {
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