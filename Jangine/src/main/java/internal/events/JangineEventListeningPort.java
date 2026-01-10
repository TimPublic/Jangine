package internal.events;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;


/**
 * A port that is created and stored by a {@link JangineEventHandler}.
 * This port takes in a {@link Consumer} of a {@link JangineEvent} and subclasses
 * of the {@link JangineEvent}.
 * These consumers get called, when the port receives an event of a subclass
 * that were registered with those consumers.
 * The port can also be activated and deactivated.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class JangineEventListeningPort {


    private HashMap<Class<? extends JangineEvent>, ArrayList<Consumer<JangineEvent>>> _callbacks;


    private boolean _active;


    public JangineEventListeningPort() {
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
     * registered with this {@link JangineEvent} subclass.
     *
     * @param event to be distributed.
     *
     * @author Tim Kloepper
     */
    public void pushEvent(JangineEvent event) {
        if (!_active) {return;}

        for (Class<? extends JangineEvent> subClass : _callbacks.keySet()) {
            if (!(subClass.isInstance(event))) {continue;}

            for (Consumer<JangineEvent> function : _callbacks.get(subClass)) {
                function.accept(event);
            }
        }
    }


    // -+- REGISTRATION -+- //

    /**
     * Takes in a {@link Consumer} of a {@link JangineEvent} and a {@link List} of subclasses of the jangine-event class.
     * This consumer gets called whenever the port distributes a jangine-event of the subclasses this consumer
     * got registered with.
     *
     * @param function consumer to be registered.
     * @param validSubClasses subclasses of the jangine-event class that this consumer gets called for.
     *
     * @author Tim Kloepper
     */
    public void registerFunction(Consumer<JangineEvent> function, List<Class<? extends JangineEvent>> validSubClasses) {
        for (Class<? extends JangineEvent> subClass : validSubClasses) {
            if (!_callbacks.containsKey(subClass)) {
                _callbacks.put(subClass, new ArrayList<>());
            }
            _callbacks.get(subClass).add(function);
        }
    }


}