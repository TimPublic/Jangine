package internal.entity_component_system.builders;


import internal.entity_component_system.A_Processor;
import internal.entity_component_system.System;


public class SystemBuilder {


    private System _currentSystem;


    public SystemBuilder start(System system) {
        if (_currentSystem != null) {
            throw new IllegalStateException("Already building a system!");
        }

        _currentSystem = system;

        return this;
    }


    public SystemBuilder add(A_Processor processor) {
        if (_currentSystem == null) {
            throw new IllegalStateException("Currently not building a system!");
        }

        _currentSystem.addProcessor(processor);

        return this;
    }
    public SystemBuilder rmv(A_Processor processor) {
        if (_currentSystem == null) {
            throw new IllegalStateException("Currently not building a system!");
        }

        _currentSystem.rmvProcessor(processor);

        return this;
    }


}