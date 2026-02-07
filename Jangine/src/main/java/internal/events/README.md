# Event System

Jangines' event system is focused on being anonymous and easy to use. <br>
That is why, there are only two "handling" classes and all in all only three.

## Classes

### 🏣 Event Handler

The event handler is the main class, managing event and the later explained listening ports. <br>

An event handler provides ports and takes in events. <br>
When you push an event, this event is pushed to all active ports,
the event handler basically functions as a distribution API, enabling you to push to all listeners
with only one call, letting the event handler and the listening ports handle all the other stuff.

### 👂 Event Listening Port

An event listening port is just like an ear, with filtering build in. <br>
You can provide callbacks and subclasses of events, on which the specified callbacks should be called. <br>

In order to receive a port, just register at the event handler and do not forget to deregister it if
the object holding it, gets destroyed.

A port can also be activated and deactivated. <br>
As long as it is not active, it will not call any callbacks.

### ✉️ Event

An event can asically be anything. <br>
It just needs to extend the event class and from this moment onwards,
you can push it to any event handler you would like.

## Architecture Integration

The event system is integraded in the engines hierarchy as follows: <br>
Every engine layer, meaning the engine itself, the windows and the scenes, <br>
holds an own event handler.

Every layer pushes events, that occurr inside of it, only to their own event handler. <br>
But they all have ports registered at each other, some active, some passive: <br>
- Engine:
Holds a port to event and scene and pushes all occurring events into its own event handler.
- Window:
Holds a port both to the engine and its own scenes, but only actively pushes the events occurring in the scenes
to its own event handler.
- Scene:
Holds a port both to the engine and its owning window, but does not actively push event of those into its own
event handler.

This way, you can theoretically receive events from a completely different window or scene inside your scene,
without this scene having to actually know about the other layer instances.

## Code examples

### Setting up an event handler

This is normally not necessary, as every layer instance already has an event handler and occurring event will
not be pushed to your custom instance automatically. <br>
That said, creation is really straight forward and does not require any special steps or any arguments at all.

~~~
EventHandler eventHandler;

eventHandler = new EventHandler();
~~~

### Pushing an event

To push an event, you also just have to call one method, there are no other methods for this action.

~~~
// This is not an actual engine class and is just for demonstration purposes.
CustomEvent event;

event = new CustomEvent(someData);

eventHandler.pushEvent(event);
~~~

### Getting an event listening port and removing it.

In order to receive an event listening port, you also just need to call one single method. <br>
Upon creation, your port is set to being active. <br>

Removing a port is a matter of calling one function and removing your reference. <br>
This action is to be emphasized heavily, as without it, memory leaks occurr.

~~~
EventListeningPort port;

// Getting an event listening port.
port = eventHandler.register();

// Removing an event listening port.
eventHandler.deregister(port);
port = null;
~~~

### Push a callback

The callback needs to be a consumer, taking in the base event class. <br>
It is guaranteed, that the event will be of your requested class or one of your requested classes,
but it will be pushed as an event, so you probably need to perform custom instance checks for your
IntelliSense.

~~~
public void customCallback(Event event) {
    // Not needed if you registered for all events.
    // The correct event class is actually guaranteed,
    // but as the event is pushed as the base class,
    // you need a check in order for your IntelliSense being happy.
    if (!(event instance of CustomEvent)) return;

    // Do stuff.
}

// This will result in the callback being called for any occuring event.
port.registerFunction(customCallback);

// This will result in the callback being called for any specified event that occurrs.
port.registerFunction(customCallback, List.of(CustomEvent.class, ...));
~~~

### Remove a callback

You can either remove a specified callback, or you can remove all callbacks of a specific class of 
the event subclass.

~~~
boolean result;

result = port.rmvFunction(customCallback);
if (!result) {
    // This means, the callback was not registered inside of this event listening port.
}

result = port.rmvClass(CustomEvent.class);
if (!result) {
    // This means, there were no callbacks registered to be called
    // upon an occuring event of this class of the event subclass.
}
~~~

### Activate or deactivate a port

~~~
// Deactivate
port.setActive(false);
// Activate
port.setActive(true);

boolean active;

active = port.isActive();
~~~

## Common Mistakes

### Recursion and event duplication

Be aware, that if you listen to the engine and to your own scenes' event handler,
you will receive every event two times. <br>
The same is true on the window layer.

### Removing a port - Memory leak

If you "destroy" an object that holds a port, without removing the port first, a memory leak will occurr in your
application, as the port will still hold references to callbacks and will try to call them. <br>
The garbage collector won't help here either, as technically there is still someone referencing or holding the
callbacks: The event listening port.

### Removing a port - Reference clearing

If you deregistered a port, you are not done yet. <br>
This step does not really affect you if you will destroy the holding object,
but it is still good practice to do it, just in case. <br>
In order to really remove a port, you also need to set your port reference to "null" or some other
port. <br>
Otherwise you still hold a valid reference to that port, which will block the garbage collector
from destroying it.

### Port as an event pusher

You cannot push event through the port. In fact, the port does not event know about the holding event handler. <br>
This means, that you still need a reference to the event handler, if you want to push events.
