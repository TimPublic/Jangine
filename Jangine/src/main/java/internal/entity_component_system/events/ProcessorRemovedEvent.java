package internal.entity_component_system.events;


import internal.entity_component_system.A_Processor;

public class ProcessorRemovedEvent extends A_ProcessorEvent {


    // -+- CREATION -+- //

    public ProcessorRemovedEvent(A_Processor processor) {
        super(processor);
    }


}