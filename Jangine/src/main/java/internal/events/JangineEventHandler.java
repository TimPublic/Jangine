package internal.events;


import java.util.ArrayList;


/**
 * The event-handler distributes events.
 * In order to this completely anonymously, he gives out
 * ports.
 * These can be obtained, by calling the register method of the
 * event-handler.
 * In order to remove the port, it has to be deregistered with
 * the respective method.
 * <p>
 * If a port should not be deregistered, although the object
 * using it is freed, it will result in a memory leak.
 *
 * @author Tim Klöpper
 * @version 1.0
 */
public class JangineEventHandler {


    private ArrayList<JangineEventListeningPort> _ports;


    public JangineEventHandler() {
        _ports = new ArrayList<>();
    }


    // -+- EVENT-DISTRIBUTION -+- //

    /**
     * Distributes the given event to the registered and active ports to be handled individually.
     *
     * @param event Event to be pushed to the ports.
     *
     * @author Tim Kloepper
     */
    public void pushEvent(JangineEvent event) {
        for (JangineEventListeningPort port : _ports) {
            port.pushEvent(event);
        }
    }


    // -+- PORT-MANAGEMENT -+- //

    /**
     * Returns a port, that is also saved in this event-handler.
     * Please hold a reference to this port at all times.
     * Upon not using this port anymore, please call the deregister method.
     *
     * @return {@link JangineEventListeningPort} to receive events.
     *
     * @author Tim Kloepper
     */
    public JangineEventListeningPort register() {
        JangineEventListeningPort newPort;

        newPort = new JangineEventListeningPort();

        _ports.add(newPort);

        return newPort;
    }
    /**
     * Takes in a port to be removed from event-distribution.
     *
     * @param port {@link JangineEventListeningPort} to be removed.
     *
     * @author Tim Kloepper
     */
    public void deregister(JangineEventListeningPort port) {
        _ports.remove(port);
    }


}