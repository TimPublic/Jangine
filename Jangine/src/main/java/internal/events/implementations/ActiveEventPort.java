package internal.events.implementations;


import internal.events.A_EventPort;
import internal.events.EventFilter;
import internal.events.I_Event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;


public class ActiveEventPort extends A_EventPort {


    // -+- CREATION -+- //

    public ActiveEventPort(EventFilter filter) {
        super(filter);

        _CALLBACKS = new ArrayList<>();
    }


    // -+- PROPERTIES -+- //

    // FINALS //

    private final ArrayList<WeakReference<Consumer<I_Event>>> _CALLBACKS;


    // -+- EVENT MANAGEMENT -+- //

    @Override
    protected void p_pushValidEvent(I_Event event) {
        Iterator<WeakReference<Consumer<I_Event>>> iterator;

        iterator = _CALLBACKS.iterator();

        while (iterator.hasNext()) {
            Consumer<I_Event> callback;

            callback = iterator.next().get();
            if (callback == null) {
                iterator.remove();

                continue;
            }

            callback.accept(event);
        }
    }


    // -+- CALLBACK MANAGEMENT -+- //

    public void addCallback(Consumer<I_Event> callback) {
        if (contains(callback)) return;

        _CALLBACKS.add(new WeakReference<>(callback));
    }
    public void rmvCallback(Consumer<I_Event> callback) {
        Iterator<WeakReference<Consumer<I_Event>>> iterator;

        iterator = _CALLBACKS.iterator();

        while (iterator.hasNext()) {
            Consumer<I_Event> currentCallback;

            currentCallback = iterator.next().get();

            if (currentCallback == null || currentCallback == callback) iterator.remove();
        }
    }

    public boolean contains(Consumer<I_Event> callback) {
        Iterator<WeakReference<Consumer<I_Event>>> iterator;

        iterator = _CALLBACKS.iterator();

        while (iterator.hasNext()) {
            Consumer<I_Event> currentCallback;

            currentCallback = iterator.next().get();
            if (currentCallback == null) {
                iterator.remove();

                continue;
            }

            if (currentCallback == callback) return true;
        }

        return false;
    }


}