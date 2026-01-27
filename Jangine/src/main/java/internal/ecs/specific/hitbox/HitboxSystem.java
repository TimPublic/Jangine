package internal.ecs.specific.hitbox;


import internal.ecs.A_ComponentSystem;
import internal.ecs.ECS;
import internal.ecs.ECS_Component;

import java.util.Collection;
import java.util.List;


public class HitboxSystem extends A_ComponentSystem<A_HitboxComponent> {


    // -+- PARAMETERS -+- //


    // -+- UPDATE LOOP -+- //

    @Override
    protected void p_internalUpdate(ECS system) {

    }


    // -+- COMPONENT MANAGEMENT -+- //

    // VALIDATION //

    @Override
    protected boolean p_isComponentValid(A_HitboxComponent component) {
        return true;
    }

    @Override
    protected void p_onComponentValidated(A_HitboxComponent component) {
        super.p_onComponentValidated(component);
    }
    @Override
    protected void p_onComponentInvalidated(A_HitboxComponent component) {
        super.p_onComponentInvalidated(component);
    }

    // ADDITION AND REMOVAL //

    @Override
    protected void p_onComponentAdded(A_HitboxComponent component) {
        super.p_onComponentAdded(component);
    }
    @Override
    protected void p_onComponentRemoved(A_HitboxComponent component) {
        super.p_onComponentRemoved(component);
    }


    // -+- COMPONENT SYSTEM MANAGEMENT -+- //

    @Override
    protected void onComponentSystemAdded(A_ComponentSystem componentSystem) {

    }
    @Override
    protected void onComponentSystemRemoved(A_ComponentSystem componentSystem) {

    }


    // -+- GETTERS -+- //

    @Override
    public Collection<Class<? extends ECS_Component>> getRequirements() {
        return List.of();
    }


}