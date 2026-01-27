package internal.entity_component_system.events;


import internal.entity_component_system.A_Processor;

public class ProcessorAddedEvent extends A_ProcessorEvent {


    // -+- CREATION -+- //

    public ProcessorAddedEvent(A_Processor processor) {
        super(processor);
    }


}