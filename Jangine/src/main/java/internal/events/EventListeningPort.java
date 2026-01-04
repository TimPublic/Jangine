package internal.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EventListeningPort {


    private HashMap<Class<? extends JangineEvent>, ArrayList<Consumer<JangineEvent>>> _callbacks;


    private boolean _active;


    public EventListeningPort() {
        _callbacks = new HashMap<>();

        _active = true;
    }


    public void setActive(boolean bool) {
        _active = bool;
    }
    public boolean isActive() {
        return _active;
    }


    public void pushEvent(JangineEvent jangineEvent) {
        if (!_active) {return;}

        for (Class<? extends JangineEvent> subClass : _callbacks.keySet()) {
            if (!(subClass.isInstance(jangineEvent))) {continue;}

            for (Consumer<JangineEvent> function : _callbacks.get(subClass)) {
                function.accept(jangineEvent);
            }
        }
    }

    public void registerFunction(Consumer<JangineEvent> function, List<Class<? extends JangineEvent>> validSubClasses) {
        for (Class<? extends JangineEvent> subClass : validSubClasses) {
            if (!_callbacks.containsKey(subClass)) {
                _callbacks.put(subClass, new ArrayList<>());
            }
            _callbacks.get(subClass).add(function);
        }
    }


}
