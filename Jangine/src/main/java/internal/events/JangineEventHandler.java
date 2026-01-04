package internal.events;


import java.util.ArrayList;


// An event-handler distributes events.
// For that, he gives out listening-ports, that offer an anonymous connection
// to the event-handler.
// Those ports can be obtained by calling the register method.
// If a port is no longer needed, a deregister call must be made, which invalidates the port,
// which excludes it from all further event-pushes.
public class JangineEventHandler {


    private ArrayList<EventListeningPort> _ports;


    public JangineEventHandler() {
        _ports = new ArrayList<>();
    }


    // -+- EVENT-DISTRIBUTION -+- //

    // Distributes the given event to the registered ports to there be handled further.
    public void pushEvent(JangineEvent jangineEvent) {
        for (EventListeningPort port : _ports) {
            port.pushEvent(jangineEvent);
        }
    }


    // -+- PORT-MANAGEMENT -+- //

    // Returns a port, that is also saved in this event-handler, where you can then
    // register callbacks for events.
    public EventListeningPort register() {
        EventListeningPort newPort;

        newPort = new EventListeningPort();

        _ports.add(newPort);

        return newPort;
    }
    // Removes a port, which will therefore take no more events.
    public void deregister(EventListeningPort port) {
        _ports.remove(port);
    }


}