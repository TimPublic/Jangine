# Batch System

The batch system, or the render system, is the most advanced and prominent feature of Jangine. <br>
With the batch efficiently managing fragments, occurring at every deletion, it is perfect for
frequent removals.

## Classes

### 👨‍💼 Batch System

The batch system holds batche processor and manages them based on which mesh they render. <br>

It is the main API for any user or other system. <br>
As long as there is a batch processor for your mesh, you can just add it to the batch system
and it will automatically be distributed and rendered upon the system being updated.

### 🤖 Batch Processor

A batch processor renders one subclass of the mesh class, which it specifies in a dedicated abstract method. <br>
It holds multiple batches and sorts them based on the shader these batches use to render the meshes
they contain.

If the shader the mesh you add is not yet associated with any batch instance,
the batch processor can automatically create them with a dedicated abstract method.

### 📦 Batch

A batch is the main render unit and contains meshes. <br>
All of these meshes get rendered with one specific shader, which the batch got assigned by the batch processor 
managing this batch.

The most prominent and special feature of this batch implementation is its fragment management. <br>
This is explained in depth in Jangine's main README, but here is a quick overview: <br>
Fragments get created upon removing a mesh from the batch. Instead of rebuilding the batch after this event,
the fragment is pushed to a binary tree, holding these fragments and merging them,
if they are directly adjacent. <br>
Upon adding a new mesh, a position is requested by the binary tree, which either returns it,
shrinking the fragment of which the position is form in size, or returns "-1" if no large enough
fragment was found.
Only if the tree returns "-1", a rebuild of the buffers occurr.

This whole system is done automatically by the abstract base class and does not need to be handled,
by extending classes.

### 🗒️ (Mesh)

The mesh is technically not a class specific to the batch system, but is currently only used by it.

A mesh is just a container of vertices and indices and any other data, which subclasses hold.

## Architecture Integration

The batch system is integraded through the render processor of the entity component system.
Although in some cases you may want an indpendent batch system, which is no problem, as you can easily
create a new one, as explained further down.

## Code Examples

### Creating a batch system

Please keep in mind, that you can implement a batch system by adding a render processor
in an entity component system. <br>
The only thing a batch system requires, is a camera.

~~~
BatchSystem system;

system = new BatchSystem(camera);
~~~

### Adding a batch processor

In order to add a processor, you can call one simple method. <br>
Just specify the processor you want to add and you want to overwrite a processor,
which could possibly already manages the mesh, this batch wants to manage.

~~~
// This batch processor gets shipped with Jangine
TextureBatchProcessor processor;

processor = new TextureBatchProcessor();

boolean result;

result = system.addProcessor(processor, false);
if (!result) {
    // This is probably due to a processor inside this batch system already managing the subclass of the mesh class
    // this batch processor wants to manage.
}
~~~

### Removing a batch processor

Removing a processor is very easy. Just specify the processor you want to remove and the method will return
the success of the operation as a boolean.

~~~
boolean result;

result = system.rmvProcessor(processor);
if (!result) {
    // This is probably due to the processor not being in this batch system.
}
~~~

### Adding a mesh

To add a mesh, the only requirement is, that there is a batch processor, managing the mesh,
inside the batch system.

~~~
TextureAMesh mesh;

mesh = new TextureAMesh(vertices, indices, texturePath);

boolean result;

result = system.addMesh(mesh, shaderPath);
if (!result) {
    // There is probably no processor for this meshes class.
}
~~~

### Removing a mesh

Removing a mesh is very easy with just one method call, returning the success of the operation.

~~~
boolean result;

result = system.rmvMesh(mesh);
if (!result) {
    // The mesh was not inside this batch system.
}
~~~

### Updating a mesh

After you changed something inside a mesh, the change is not automatically recognized by the batch system. <br>
This is why you need to explicitly update the mesh. Luckily, this is very easy to do. <br>
But the mesh needs to already be in the batch system and will not be added automaticall. <br>
This is in order to raise more awareness about the state of your data.

You can also specify a new shader you want the mesh to be rendered with, but this is not necessary. <br>
Changing the size of the vertices or indices array is also no problem.

~~~
// Changing the mesh somehow, even in size.
mesh.vertices = new float[] {
      0,   0, 0, 0, 0,
    100,   0, 1, 0, 0,
    100, 100, 1, 1, 0,
      0, 100, 0, 1, 0,
}

boolean result;

result = system.updateMesh(mesh, shaderPath); /* OR */ result = system.updateMesh(mesh);
if (!result) {
    // This is most likely due to the mesh not being inside this batch system.
}
~~~

## Common Mistakes

### Updating before adding

There is an intentional restriction, that does not allow adding a mesh through an update call. <br>
This is meant to raise awareness about your data's state.

If you really want to bypass this limitation, there is a method, called "addOrUpdateMesh()",
in the batch class. <br>
But this method is hard to access, as the batch is hidden beneath two layers, so this will
really only ever be an option for your custom batch processors.
