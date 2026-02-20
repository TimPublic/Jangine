package internal.events;


import java.util.Collection;


/**
 * <p>
 * Provides some way to deal with events. <br>
 * You can instantiate any kind of port and then push
 * it to one or more {@link EventMaster} to receive events from these.
 * </p>
 * <p>
 * A port always contains an {@link EventFilter} in order to filter
 * for specific events. Meaning, by providing a filter, you can be sure that
 * any event that this port takes in from that point on, will have passed
 * your provided filter.
 * </p>
 * <p>
 * With the active parameter, a port can easily be disabled from receiving any events,
 * without needing to remove them from any event master.
 * </p>
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public abstract class A_EventPort {


    // -+- CREATION -+- //

    public A_EventPort(EventFilter filter) {
        if (filter == null) filter = new EventFilter();
        this.filter = filter;

        active = true;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    /**
     * Is the {@link EventFilter} for this {@link A_EventPort}. <br>
     * It is used to filter any {@link I_Event} that gets pushed to this port.
     * Only if the event passes through this filter, the push gets delegated to
     * the implementation through the {@link A_EventPort#p_pushValidEvent(I_Event)} method.
     */
    public EventFilter filter;
    /**
     * If the value of this property is set to {@code false}, the port will no longer
     * take in any events, until this property is set to {@code true} again.
     */
    public boolean active;


    // -+- EVENT MANAGEMENT -+- //

    /**
     * 'Pushes' an event to the port, the exact definition of a push
     * is left to the implementation of this port, with the help
     * of the {@link A_EventPort#p_pushValidEvent(I_Event)}. <br>
     * This method is simply a wrapper for the previously stated function,
     * with the addition of already running the event through the filter.
     *
     * @param event {@link I_Event} which gets pushed to the port, but is not guaranteed to pass the filter.
     *
     * @author Tim Kloepper
     */
    protected void p_push(I_Event event) {
        if (!active) return;
        if (!filter.check(event)) return;

        p_pushValidEvent(event);
    }
    /**
     * Is simply a wrapper for the {@link A_EventPort#p_push(I_Event)} method,
     * which filters out invalid events, according to the filter and then pushes
     * the valid event to the {@link A_EventPort#p_pushValidEvent(I_Event)} method.
     *
     * @param events {@link Collection<I_Event>} Which get pushed one after another to the {@link A_EventPort#p_push(I_Event)} method.
     *
     * @author Tim Kloepper
     */
    protected void p_push(Collection<I_Event> events) {
        events.forEach(this::p_push);
    }
    /**
     * Is overwritten by implementations of the {@link A_EventPort} class. <br>
     * It provides you with any {@link I_Event} that got pushed to this port and passed
     * the set {@link EventFilter}.
     *
     * @param event {@link I_Event} which got pushed to the port. This event is guaranteed to have passed the filter.
     */
    protected abstract void p_pushValidEvent(I_Event event);


}