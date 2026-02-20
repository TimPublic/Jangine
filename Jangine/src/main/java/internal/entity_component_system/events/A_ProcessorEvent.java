package internal.entity_component_system.events;


import internal.entity_component_system.A_Processor;
import internal.events.implementations.Event;


public abstract class A_ProcessorEvent extends Event {


    // -+- CREATION -+- //

    public A_ProcessorEvent(A_Processor processor) {
        this.processor = processor;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    public final A_Processor processor;


}