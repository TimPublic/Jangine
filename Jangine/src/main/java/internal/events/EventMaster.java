package internal.events;


import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;


public class EventMaster {


    // -+- CREATION -+- //

    public EventMaster() {
        _PORTS = new ConcurrentLinkedDeque<>();
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final ConcurrentLinkedDeque<WeakReference<A_EventPort>> _PORTS;


    // -+- PORT MANAGEMENT -+- //

    public void register(A_EventPort port) {
        if (contains(port)) return;

        _PORTS.add(new WeakReference<>(port));
    }
    public void registerFast(A_EventPort port) {
        _PORTS.add(new WeakReference<>(port));
    }

    public void deregister(A_EventPort port) {
        Iterator<WeakReference<A_EventPort>> iterator;

        iterator = _PORTS.iterator();

        while (iterator.hasNext()) {
            A_EventPort currentPort;

            currentPort = iterator.next().get();

            if (currentPort != null) {
                if (currentPort == port) iterator.remove();

                continue;
            }

            iterator.remove();
        }
    }

    public boolean contains(A_EventPort port) {
        Iterator<WeakReference<A_EventPort>> iterator;

        iterator = _PORTS.iterator();

        while (iterator.hasNext()) {
            A_EventPort currentPort;

            currentPort = iterator.next().get();

            if (currentPort != null) {
                if (currentPort == port) return true;

                continue;
            }

            iterator.remove();
        }

        return false;
    }


    // -+- EVENT MANAGEMENT -+- //

    public void push(I_Event event) {
        Iterator<WeakReference<A_EventPort>> iterator;

        iterator = _PORTS.iterator();

        while (iterator.hasNext()) {
            A_EventPort currentPort;

            currentPort = iterator.next().get();

            if (currentPort != null) {
                currentPort.p_push(event);

                continue;
            }

            iterator.remove();
        }
    }
    public void push(Collection<I_Event> events) {
        Iterator<WeakReference<A_EventPort>> iterator;

        iterator = _PORTS.iterator();

        while (iterator.hasNext()) {
            A_EventPort currentPort;

            currentPort = iterator.next().get();

            if (currentPort != null) {
                currentPort.p_push(events);

                continue;
            }

            iterator.remove();
        }
    }


}