# Jangine
Jangine is a small open-source game-engine, written in Java.


## Features

#### Multi-Window Support

Jangine supports multiple windows, you can switch between freely.
All windows are updated every frame and stored and managed in the
Engine singleton-class.

#### Scene System

Every window can contain multiple scenes,
from which only one can ever be active at the same time.
Only the active scene of every window gets updates, events
and rendered.

#### Event System

Every layer (engine -> window -> scene) has its own event-handler.
Eventy handlers work with Ports, which an object reveives by registering
at the handler. This port can be activated and deactivated,
also it is used to apply callbacks for events and can filter
events by type.
To remove the port and therefore any connection to the handler,
just deregister the port.
This system ensures anonymous and safe handling of callbacks.

Example:

'''
int port;
port = eventHandler.register();

port.registerFunction(_callback, List.of(Event.JangineKeyPressedEvent));
'''

#### Rendering

Jangine has its own mesh, texture, shader-program, camera and batch class.

The mesh contains vertices and indices.
The texture contains a texture and can be bound directly.
The shader-program is a program of vertex- and fragment-shader and can be bound directly.
The camera contains a projection- and a view-matrix.
The Batch takes in a texture and a shader-program and renders all given meshes with the camera as a viewport.

#### Input System

Every window has a key- and a mouse-listener, who push their events to every layer.
There are extensive events for every case:

Key:
- KeyEvent
- KeyContinuedEvent
- KeyPressedEvent
- KeyReleasedEvent

Mouse:
- MouseDragging
- MouseDraggingContinued
- MouseDraggingStarted
- MouseDraggingEnded
- CursorPositionChanged
- MouseButtonContinued
- MouseButtonPressed
- MouseButtonReleased