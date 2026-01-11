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
**Jangine** also provides a **texture-**, a **camera-** as well as a **render-batch class**.

**Texture**
Textures can be created as follows:
~~~
JangineTexture texture;

texture = new JangineTexture("texturepath/texture.png", imageLoaderImpl);
~~~
They can be bound and unbound as follows:
~~~
glActiveTexture(GL_TEXTURE0);
texture.bind();

...

texture.unbind();
~~~

**Shader Program**
Shaders can be created as follows:
~~~
JangineShaderProgram shaderProgram;

// This will automatically compile and link.
shaderProgram = new JangineShaderProgram("shaderpath/shader.glsl"); // Needs to be glsl-file.
~~~
The shader-program can simply be bound with a single method and is then used:
~~~
shaderProgram.use();

shaderProgram.unuse();
~~~
The shader also takes in **uniforms**:
~~~
shaderProgram.upload(int / float / matrix / ...);
~~~

**Camera**
The camera is currently a nearly one-to-one copy from "Games with Gabe" and all credits for this class, belong to him.

To create a camera, do the following:
~~~
JangineCamera2D camera;

// Width and height currently in 32-by-32 pixels.
camera = new JangineCamera2D(width, height);
~~~
If you want to change the projection, do the following:
~~~
camera.adjustProjection(width, height);
~~~
The view matrix gets updated upon calling:
~~~
viewMatrix = camera.getViewMatrix();
~~~

**Render Batch**
Render batches can be created as follows:
~~~
JangineRenderBatch renderBatch;

renderBatch = new JangineRenderBatch(shaderProgram, texture, camera);
~~~
You can then add and remove meshes freely, those need to follow the following premise of a **four-float vertex**: x, y, uvX and uvY:
~~~
renderBatch.addMesh(mesh);

renderBatch.updateMesh(mesh);

renderBatch.rmvMesh(mesh);
~~~
To render a batch, simply call:
~~~
renderBatch.render();
~~~

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
