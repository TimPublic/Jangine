package internal.events;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;


/**
 * A port that is created and stored by a {@link EventHandler}.
 * This port takes in a {@link Consumer} of a {@link Event} and subclasses
 * of the {@link Event}.
 * These consumers get called, when the port receives an event of a subclass
 * that were registered with those consumers.
 * The port can also be activated and deactivated.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class EventListeningPort {


    private HashMap<Class<? extends Event>, ArrayList<Consumer<Event>>> _callbacks;


    private boolean _active;


    public EventListeningPort() {
        _callbacks = new HashMap<>();

        _active = true;
    }


    // -+- ACTIVATION-MANAGEMENT -+- //

    /**
     * Sets the status of the port.
     *
     * @param bool new status.
     *
     * @author Tim Kloepper
     */
    public void setActive(boolean bool) {
        _active = bool;
    }

    /**
     * Returns the current status of the port.
     *
     * @return current status.
     *
     * @author Tim Kloepper
     */
    public boolean isActive() {
        return _active;
    }


    // -+- EVENT-DISTRIBUTION -+- //

    /**
     * Gets called by the owning event-handler and distributes the given event to the callbacks,
     * registered with this {@link Event} subclass.
     *
     * @param event to be distributed.
     *
     * @author Tim Kloepper
     */
    public void pushEvent(Event event) {
        if (!_active) {return;}

        for (Class<? extends Event> subClass : _callbacks.keySet()) {
            if (!(subClass.isInstance(event))) {continue;}

            for (Consumer<Event> function : _callbacks.get(subClass)) {
                function.accept(event);
            }
        }
    }


    // -+- REGISTRATION -+- //

    /**
     * Takes in a {@link Consumer} of a {@link Event} and a {@link List} of subclasses of the jangine-event class.
     * This consumer gets called whenever the port distributes a jangine-event of the subclasses this consumer
     * got registered with.
     *
     * @param function consumer to be registered.
     * @param validSubClasses subclasses of the jangine-event class that this consumer gets called for.
     *
     * @author Tim Kloepper
     */
    public void registerFunction(Consumer<Event> function, List<Class<? extends Event>> validSubClasses) {
        for (Class<? extends Event> subClass : validSubClasses) {
            if (!_callbacks.containsKey(subClass)) {
                _callbacks.put(subClass, new ArrayList<>());
            }
            _callbacks.get(subClass).add(function);
        }
    }
    public void registerFunction(Consumer<Event> function) {
        if (!_callbacks.containsKey(Event.class)) _callbacks.put(Event.class, new ArrayList<>());

        _callbacks.get(Event.class).add(function);
    }


}