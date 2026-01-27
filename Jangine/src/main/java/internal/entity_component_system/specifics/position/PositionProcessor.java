package internal.entity_component_system.specifics.position;


import internal.entity_component_system.A_Component;
import internal.entity_component_system.A_Processor;
import internal.entity_component_system.A_System;
import internal.rendering.container.Scene;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class PositionProcessor extends A_Processor<PositionComponent> {


    // -+- CREATION -+- //

    @Override
    protected void p_init(A_System system, Scene scene) {

    }
    @Override
    protected void p_kill(A_System system, Scene scene) {

    }

    @Override
    protected void p_receiveRequiredProcessors(HashMap<Class<? extends PositionComponent>, A_Processor<?>> requiredProcessors) {

    }

    // -+- UPDATE LOOP -+- //

    @Override
    protected void p_internalUpdate(Collection<PositionComponent> validComponents, A_System system, Scene scene) {

    }


    // -+- COMPONENT MANAGEMENT -+- //

    @Override
    protected boolean p_isComponentValid(PositionComponent component) {
        return true;
    }


    // -+- GETTERS -+- //

    @Override
    protected Collection<Class<? extends PositionComponent>> p_getProcessedComponentClasses() {
        return List.of(PositionComponent.class);
    }
    @Override
    protected Collection<Class<? extends A_Component>> p_getRequiredComponentClasses() {
        return List.of();
    }


}