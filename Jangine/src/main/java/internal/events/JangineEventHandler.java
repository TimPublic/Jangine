package internal.events;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;


public class JangineEventHandler {


    private static JangineEventHandler _instance;


    private ArrayList<EventListeningPort> _ports;


    private JangineEventHandler() {
        _ports = new ArrayList<>();
    }

    public static JangineEventHandler get() {
        if (_instance == null) {
            _instance = new JangineEventHandler();
        }

        return _instance;
    }


    public void pushEvent(JangineEvent jangineEvent) {
        for (EventListeningPort port : _ports) {
            port.pushEvent(jangineEvent);
        }
    }

    public EventListeningPort register() {
        EventListeningPort newPort;

        newPort = new EventListeningPort();

        _ports.add(newPort);

        return newPort;
    }
    public void deregister(EventListeningPort port) {
        _ports.remove(port);
    }


}


class EventListeningPort {


    private HashMap<Class<? extends JangineEvent>, ArrayList<Consumer<JangineEvent>>> _callbacks;


    public EventListeningPort() {
        _callbacks = new HashMap<>();
    }


    public void pushEvent(JangineEvent jangineEvent) {
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