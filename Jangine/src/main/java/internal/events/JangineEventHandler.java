package internal.events;


import java.util.ArrayList;


public class JangineEventHandler {


    private ArrayList<EventListeningPort> _ports;


    public JangineEventHandler() {
        _ports = new ArrayList<>();
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