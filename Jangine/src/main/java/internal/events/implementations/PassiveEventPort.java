package internal.events.implementations;


import internal.events.A_EventPort;
import internal.events.EventFilter;
import internal.events.I_Event;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedDeque;


public class PassiveEventPort extends A_EventPort {


    // -+- CREATION -+- //
    
    public PassiveEventPort(EventFilter filter, int maxEventsInQueue) {
        super(filter);

        _maxEventsInQueue = maxEventsInQueue;
        _EVENTS = new ConcurrentLinkedDeque<>();
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    private int _maxEventsInQueue;

    // FINALS //

    private final ConcurrentLinkedDeque<I_Event> _EVENTS;


    // -+- EVENT MANAGEMENT -+- //

    @Override
    protected void p_pushValidEvent(I_Event event) {
        while (_EVENTS.size() >= _maxEventsInQueue) _EVENTS.poll();

        _EVENTS.add(event);
    }

    public void setMaxEventsInQueue(int value) {
        if (value <= 0) throw new IllegalArgumentException("You need to allow at least one event in the queue at a time!");

        while (_EVENTS.size() > value) _EVENTS.poll();

        _maxEventsInQueue = value;
    }

    public ArrayDeque<I_Event> grab() {
        ArrayDeque<I_Event> queue;

        queue = new ArrayDeque<>(_EVENTS);
        _EVENTS.clear();

        return queue;
    }
    public ArrayDeque<I_Event> grab(int maxAmount) {
        ArrayDeque<I_Event> queue;

        queue = new ArrayDeque<>();

        while (maxAmount > 0) {
            I_Event event;

            event = _EVENTS.poll();
            if (event == null) return queue;

            queue.add(event);

            maxAmount--;
        }

        return queue;
    }
    public I_Event grabSingle() {
        return _EVENTS.poll();
    }

    public ArrayDeque<I_Event> peek() {
        return new ArrayDeque<>(_EVENTS);
    }
    public ArrayDeque<I_Event> peek(int maxAmount) {
        ArrayDeque<I_Event> middleQueue, queue;

        middleQueue = new ArrayDeque<>(_EVENTS);
        queue = new ArrayDeque<>();

        while (maxAmount > 0) {
            I_Event event;

            event = middleQueue.poll();
            if (event == null) return queue;

            queue.add(event);

            maxAmount--;
        }

        return queue;
    }
    public I_Event peekSingle() {
        return _EVENTS.peek();
    }


}