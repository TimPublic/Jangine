# Batch System

The batch system is a management design, orchestrating individual render batches,
used for quick and efficient rendering of any mesh class, in a three layer structure.
The main feature of this system clearly is the management of the fragmentation caused
by mesh removal and updating meshes to another size. <br><br>

---

## Hierarchy

### Batch System (first layer)
<ul>
    <li>Stores processors based on managed mesh</li>
    <li>Provides simple API</li>
    <li>Mesh adding and removal with automatic distribution</li>
    <li>Processors adding and removal</li>
</ul>

### Processor (second layer)
<ul>
    <li>Stores batches based on the used shader</li>
    <li>Contains batches used for one specific mesh subclass</li>
</ul>

### Batch (third layer)
<ul>
    <li>Stores meshes of a specific subclass</li>
    <li>Uses one shader to render all contained meshes</li>
    <li>Reuses space created by fragmentation</li>
    <li>Manages fragmentation efficiently and reduces amount of rebuilds</li>
</ul>
<br>

---

## Fragmentation Management

### What it is

The fragmentation management is the most prominent and important feature of this whole system.
It is the single reason why batches suffice with a much fewer amount of rebuilds,
than what would normally be expected of a batch.
Normally, batches issue a full rebuild either directly after removing a mesh or updating it to a different size,
or they issue it, after a certain threshold of empty fragments is reached in the buffers. <br>
This batch however, has efficiently reduced the amount of rebuilds required,
by keeping track of and reusing said fragments, created as a result of frequent removal or updating to different sizes.

### How it works

Every time, a mesh gets removed from the batch, a fragment with the size and position of the freed space is created.
This fragment is pushed into a binary tree, which uses the fragments size as the key. <br>
Then, upon adding a new mesh, a fragment of the required size, or higher, is tried to be found,
to provide the required space.
If this space is found, the position of the fragment is returned. This process decreases the fragment in size and
moves it further up, directly behind the allocated space.
If the fragment is used completely, it gets deleted from the tree. <br>
By breaking the updating of a mesh to a different size into removing the mesh and therefore creating a fragment
and adding it back in and therefore finding a new space,
this problem is also solved cleanly with existing functionality.

### Further considerations

In the case of two adjacent fragments being directly connect and which should therefore merge, <br>
a tree map with the position of the key is maintained parallelly. <br>
This enables O(n) iteration over all fragments with a lookup time of O(log n) to find neighbours. <br>

Also, by enabling the index buffer to rebuild upon extreme fragmentation without the vertex buffer needing to rebuild,
we further reduce the time spent on rebuilding the buffers.

### Conclusion

So all in all, this design of handling fragmentation, enables the system to reduce rebuilds drastically
and therefore be more efficient by defeating removal and update to different size problems.

---

## Usage

### Batch System

You typically only create one batch system for each render container, as more is not really needed. <br>
Keep in mind, that the batch system is not aware of the glfwWindow and needs an applied context window
to function properly.

You can update add or remove a mesh with just three methods:
<ul>
    <li>addMesh(mesh, shader)</li>
    <li>rmvMesh(mesh)</li>
    <li>updateMesh(mesh)</li>
    <li>updateMesh(mesh, shader)</li>
</ul>

Adding and removing processors is just as easy:
<ul>
    <li>addProcessor(processor)</li>
    <li>rmvProcessor(processor)</li>
    <li>rmvProcessor(Class<? extends Mesh>)</li>
</ul>

And the update loop is accessible with the following method:
<ul>
    <li>update()</li>
</ul>

### Processor

The processor is an abstract class, handling most of the heavy lifting. <br>
Only very few abstract method are for you to complete such as the initialization
of the correct batch class, or the specification of the to be managed mesh class.

You also do not need to worry, about a batch for the specified shader not being present,
as one will automatically be created for you, with the help of the abstract method
for batch initialization.

The processor also comes with a simple API which should normally not be used directly,
but is public for full flexibility:
<ul>
    <li>addMesh(mesh, shader)</li>
    <li>rmvMesh(mesh)</li>
    <li>updateMesh(mesh)</li>
    <li>updateMesh(mesh, shader)</li>
    <li>update()</li>
</ul>

### Batch

The batch is an abstract class, handling most of the heavy lifting,
such as buffer creation and management, as well as fragmentation management
and mesh addition and removal.
But it provides multiple abstract functions to react to different events, to create attribute pointers
and to prepare rendering by uploading stuff to the shader and enabling attribute pointers.

The batch comes with a fairly simple API as well which is not intended to be used outside of this system,
although it technically could be:
<ul>
    <li>addMesh(mesh, shader)</li>
    <li>rmvMesh(mesh)</li>
    <li>addOrUpdateMesh(mesh)</li>
    <li>updateMesh(mesh)</li>
    <li>render()</li>
</ul>