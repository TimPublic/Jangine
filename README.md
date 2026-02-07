# Jangine

![Jangine Logo](Jangine.png)

# About

Jangine is a game engine written in **Java**, using **LWJGL**. <br>
While supporting standard features as listed further down, the main feature is the **efficent rendering organized in batches**,
which **manage fragments efficiently, reducing removal costs drastically**. <br>

Jangine is the **perfect engine** for **quick and frequent mesh and batch manipulation**.

This engine is written by one person, which is six-teen years old.

# Features

## 💻 Rendering - Dynamic and efficient

Jangine allows you to easily render any kind of mesh. <br>
It comes with texture and color rendering out of the box,
but also supports custom meshes, shaders and batches. <br>

Batches manage fragments, created by removing meshes, efficiently, reusing them upon adding a new mesh. <br>
This feature has its own section further down this README.

## 📫 Event Handling - Anonymous and port-based

Events are handled through event handlers, home to the engine, all windows and all scenes. <br>
Listening to events is managed anonymously with ports, holding callbacks. <br>

The event distribution follows an hierarchial system: <br>
The engine pushes every event of the engine. <br>
Windows push every event occuring in the window or its scenes. <br>
Scenes push every event occuring in itself. <br>

Despite this system, a scene could still receive an event occuring in a scene of another window,
as it holds one (standard) or multiple (custom) ports from the engines' event handler.

## 👽 Entity Component System - Anonymous and easy to use

The entity component system is a standard implementation of this wide spread design. <br>
But this implementation is all about being dynamic and easy to use: <br>
Upon a processor being added, that can manage a specific component, you can add this type of component for any entity in this entity component system.

It ships with support for rendering, position and collision (+ hitbox), but also supports custom systems: <br>
You can easily create new components and processors for them by extending the respective abstract classes.

## 💥 Collision - Dynamic and easy to use

The collision system is heavily integraded into the entity component system and only exists inside of it. <br>

It uses dependency injection to support different collision calculations.
While shipping with only AABB collision and rectangle and circle hitbox components, it supports custom calculators and hitbox components.

## ⌨️ Input System - Mouse and Keyboard

The input system is window based and has listeners for mouse and keyboard. <br>
These are standard listeners implemented through openGL.

## 🪟 Multiple Windows - Any amount and easy management

Jangine supports any amount of windows, giving you maximum flexibility. <br>
Though the window is a complete and standalone class, you can still extend it and tailor it to your needs. <br>

Due to having to add windows manually to the engine, you can also manage them as you like.

Window creation and adding them to the engine, while the engine is already running, is also supported.

## 🎞️ Scene based - Any amount and easy management

Jangine supports any amount of scenes, which are home to a window. <br>
The scene class is abstract and therefore is intended to be extended and then added to any window you would like.

Scene are basically the render and logic containers in the engine, containing the entity component systems. <br>
Only one scene can be active per window at any given moment, supporting their position as being the container for one complete logic tank.

# Getting Started

To get started, just download the .jar and implement it into your project. <br>
Then, you can write your first small scene, add it to a window, add the window to the engine and then run it! <br>

## Minimal Setup

~~~
// Just an example method, could also be main or anything else, make sure this is called though.
public void start() {
    // What you need.
    Engine engine;
    Window window;
    // SimpleScene is not shipped, it should just represent a scene, extending the base scene class.
    SimpleScene scene;

    // This is necessary to set up GLFW, if you don't do that, the engine won't run.
    engine = Engine.get();

    // You need to create a window, before doing anything else, as this will set up OpenGL.
    window = new Window(1920, 1080);
    engine.addWindow(window);

    scene = new SimpleScene(window.getWidth(), window.getHeight());
    window.addScene(scene);

    // Anything after this line will only run after the engine closed.
    // any other logic needs to be in objects inside the engine.
    engine.run();
}
~~~

## Typical Mistakes

### Initialization Order

You need to get the engine one time, as it is a singleton, you do not need to safe it,
but get it. <br>
This initilializes GLFW which is used to create windows. <br>

Then, before creating shaders or anything else, get the window where you want to use it
into context, so that OpenGL does not work other than you or the engine expected.

### Scene Activation

Only one scene can be active at any time per window. <br>
Adding a scene does not suffice and will not make it active, although you can call "addAndActivateScene()". <br>
Use "activateScene()" to actually make the scene active, because only then its update method will be called.

## Batch - Fragment Management

As this is the most prominent and important feature, which makes Jangine special, this section is dedicated to it.

### The Problem

Usually, when removing a mesh or updating it to another size, the operation creates holes in the batch,
called fragments. <br>
To avoid these unused spaces of data, batches just rebuilds either directly after removing a mesh, or after a specified threshold of fragmentation is reached.

This costs lots of time, as it turns removal into an O(n) operation.

### The Solution

If you would keep track of these fragments, to later reuse them,
you could reduce the amount of rebuilds needed drastically. <br>
By just finding a fitting fragments, created at a removal, upon adding a new mesh and using it to store this new mesh,
you would avoid rebuilds almost completely, as they are only required, if no fitting fragment is available inside the batch. <br>
Doing this for both the vertex and the index buffer, is an important optimization to support frequent removal and
unpredictable mesh manipulations.

### How Jangine implements it

Jangine implements the previously stated solution, by implementing fragments through a fragment node.
This node constists of a position, a size and two other fragment nodes. <br>
These nodes make up a binary tree, with the size as the key. <br>
Upon addition, a fitting node is found with the binary tree, the position is returned and the size of the fragment
node gets reduced by the allocated amount. <br>
Of cource, directly adjacent nodes need to be merged together. In order to provide this functionality without
brute force checks with O(n²) complexity, a tree map is maintained, to get adjacent nodes in O(log n) time,
making the final complexity O(n log n), with regards to the recursive nature, as a new loop needs to be started
if nodes get merged, potentially increasing the time complexity.

## Further Documentation

More detailled explanations of the systems can be found in their corresponding packages.
