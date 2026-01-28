package internal.entity_component_system.specifics.hitbox;


import internal.entity_component_system.A_Component;
import internal.entity_component_system.A_Processor;
import internal.entity_component_system.System;
import internal.rendering.container.Scene;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class HitboxProcessor extends A_Processor<A_HitboxComponent> {


    // -+- CREATION -+- //

    @Override
    protected void p_init(System system, Scene scene) {

    }
    @Override
    protected void p_kill(System system, Scene scene) {

    }

    @Override
    protected void p_receiveRequiredProcessors(HashMap<Class<? extends A_HitboxComponent>, A_Processor<?>> requiredProcessors) {

    }

    // -+- UPDATE LOOP -+- //

    @Override
    protected void p_internalUpdate(Collection<A_HitboxComponent> validComponents, System system, Scene scene) {

    }


    // -+- COMPONENT MANAGEMENT -+- //

    @Override
    protected boolean p_isComponentValid(A_HitboxComponent component) {
        return true;
    }


    // -+- GETTERS -+- //

    @Override
    protected Collection<Class<? extends A_HitboxComponent>> p_getProcessedComponentClasses() {
        return List.of(A_HitboxComponent.class, RectangleHitboxComponent.class, CircleHitboxComponent.class);
    }
    @Override
    protected Collection<Class<? extends A_Component>> p_getRequiredComponentClasses() {
        return List.of();
    }


}