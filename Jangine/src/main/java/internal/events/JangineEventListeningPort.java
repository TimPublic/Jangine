package internal.events;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;


// Listening ports get created and stored in event-handlers.
// They are used to connect completely anonymously to an event-handler.
// Listening-Ports take in callbacks and filter events for subclasses to then push them
// to respective callback, who requested those events.
public class JangineEventListeningPort {


    private HashMap<Class<? extends JangineEvent>, ArrayList<Consumer<JangineEvent>>> _callbacks;


    private boolean _active;


    public JangineEventListeningPort() {
        _callbacks = new HashMap<>();

        _active = true;
    }


    // -+- ACTIVATION-MANAGEMENT -+- //

    public void setActive(boolean bool) {
        _active = bool;
    }
    public boolean isActive() {
        return _active;
    }


    // -+- EVENT-DISTRIBUTION -+- //

    // Gets usually called by an event-handler and distributes the given event to the respective callbacks.
    public void pushEvent(JangineEvent jangineEvent) {
        if (!_active) {return;}

        for (Class<? extends JangineEvent> subClass : _callbacks.keySet()) {
            if (!(subClass.isInstance(jangineEvent))) {continue;}

            for (Consumer<JangineEvent> function : _callbacks.get(subClass)) {
                function.accept(jangineEvent);
            }
        }
    }

    // Saves a callback that needs to take in an event.
    // Also takes in a list of event-subclasses.
    // Only if the event, the port distributes is of on of those subclasses, will the callback be called.
    public void registerFunction(Consumer<JangineEvent> function, List<Class<? extends JangineEvent>> validSubClasses) {
        for (Class<? extends JangineEvent> subClass : validSubClasses) {
            if (!_callbacks.containsKey(subClass)) {
                _callbacks.put(subClass, new ArrayList<>());
            }
            _callbacks.get(subClass).add(function);
        }
    }


}