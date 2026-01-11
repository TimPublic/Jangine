# Jangine

**Jangine** is a simple **game-engine** written in Java, using **JWLGL**.

---

## Main Features

#### 📬 Event System

The events get distributed hierachially.
Every window, the engine and every scene has an own **event-handler**.
The engine stands on the top, everyone can connect to it.
The window is in the middle, handling own window-wide events and the
scene is at the bottom, only handling scene wide events.

In case of **input-events**, every window has their own **listeners**, pushing to
the windows' event-handler and the engines'. The scene only receives them,
if they are active.

The event-handlers use a **port-based system** for registration.
If an object wants to receive events, it request a **port**,
where it can register **callbacks** and **event subclasses** on which
this callback should be called.

Those ports can also be **deactivated** or **activated**.

Example:
~~~
// Registration (get port).
port = eventHandler.register();

// Set up callbacks.
port.registerFunction(callback, List.of(Class<JangineKeyEvent>, ...));

// Deactivate.
port.setActive(false);
// Activate.
port.setActive(true);

// Check for status.
if (port.isActive()) {
    ...
}

// Remove port upon not using it anymore.
eventHandler.deregister(port);
port = null;
~~~

#### 💻 Rendering

**Jangine** contains **extensive rendering options**.
You can write your **own shaders** and set them up in a **shader-program class**.
**Jangine** also provides a **texture-** as well as a **render-batch class**.

The shader-program can simply be bound with a single method and is then used:
~~~
shaderProgram.use();

shaderProgram.unuse();
~~~

Same with the texture:
~~~
glActiveTexture(GL_TEXTURE0);
_texture.bind();

texture.unbind();
~~~

Textures take, additionally to the file-path, in a **texture-loader**.
That way, you can define what method is used to load your image.

The shader also takes in **uniforms**:
~~~
shaderProgram.upload(int / float / matrix / ...);
~~~

The render-batch takes in any **mesh** that follows the premise of a **four-float vertex**:
x, y, uvX and uvY.
It also contains a texture and a shader that every mesh given is rendered with.

Meshes can be **updated** too.

You can also **remove meshes**, but currently they only get removed from updates
and are still rendered.

Example:
~~~
// Creating.
batch = new JangineRenderBatch(shaderProgram, texture, camera);

// Adding.
batch.addMesh(mesh);

// Rendering.
batch.render();

// Updating.
batch.updateMesh(mesh);

// Removing.
batch.rmvMesh(mesh);
~~~

As you can see, the batch also takes in a **camera**, containing a **projection-** and a **view matrix**. This camera is code nearly copied one-to-one from **"Games with Gabe"**,
as well as the "default.glsl"-shader.

#### 👽 Entity-Component System

Currently under construction.

#### 🖱️ Input System

Jangine has a ways to identify both **key-** and **mouse events**.
For that, the **JangineKeyListener** and **JangineMouseListener** are being used.

They can push a **variety of events** describing **different inputs**.

They themselves do not take in any **callbacks** or **reactions**,
they take in event-handlers that they push respective events to:

~~~
// Creating.
keyListener = new JangineKeyListener();

// Adding an event-handler.
keyListener.addEventHandler(eventHandler);
// Removing an event-handler.
keyListener.rmvEventHandler(eventHandler);

// Adding engines' event-handler.
keyListener.addEngine();
// Removing engines' event-handler.
keyListener.rmvEngine();

(Same for the JangineMouseListener)
~~~

#### 🪟 Multiple windows

The engine supports **multiple windows**, which get **created** and **managed** by the engine itself.

#### Scene system

Every window can contain **scenes**, from which only one can be **active** in every window at one time.

The scenes that are not active, do not get **updated** and do not **receive events** from the windows' event-handler,
as their own event-handler is connected to the windows'
through a **port** which gets **deactivated** and **activated**.

Scenes can be **switched between windows**, but can only be owned by **one window at a time**, although you **could work around this limitation**.
