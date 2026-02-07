# Entity Component System

The entity component system, or short "ECS", is made to have a simple API while still providing great flexibility.

## Classes

### 👨‍💼 System

The system is the orchestrating class, sitting at the top of the entity component system and being the main
connection point between the entity component system and users or other systems. <br>

It holds and manages processors, based on the component they want to manage. <br>
There can only ever be one processor for a component subclass, but the processors can manage multiple component
subclasses.

The system is verty flexible, you can just add or remove meshes to the system directly and all distribution
will be handled by the system and the processors.

### 🤖 Processor

The processor manages one or more component subclasses, which it specifies in a dedicated method. <br>
It does certain operations inside an update loop and also has the option to know about other processors
and receive notification if something changes in the system.

Besides listing the components a processor wants to manage, it can also specify components, that
the systems already needs to handle, for this processor to work properly. <br>
This should only include components that are absolutely necessary, because if these requirements are not met,
the processor will not be added to the system.

You can write own processor for your custom components, every required method is abstract and therefore you
will get forced to implement it, meaning that you cannot forget it.

### 🧩 Component

The component is a simple data container which should not hold any own logic, although it is totally possible. <br>
Components always belong to only one entity which gets set by the system and can be reused.

To add a component, just called the respective system method and provide the entity you want to add the component
to. With its flexible design, the system automatically finds the correct processor and adds the component. <br>

Every entity can only ever hold at maximum one component at every processor at any time.

You can also write own components to be managed by your own processors.

### 👽 (Entity)

Entities are just integers, helt in the entity component system.

## Architecture Integration

The entity component system is already implemented into Jangine's architecture: <br>
Every scene has an entity component system, publicly available. <br>
At the moment, entities can not be transferred to other systems, although it would be possible
in future updates, if needed.

## Code Examples

### Creating an entity component system

Creating an entity component system is very easy. <br>
But please keep in mind, that every scene already has an entity component system and the case of you needing
another one, is very unlikely.

An entity component system requires a scene, as some shipped processors need to know about it. <br>
This could potentially change in a future update, making the entity component system class more flexible.

~~~
System ecs;

ecs = new System(customScene);
~~~

### Adding a processor

If a processor for the components this processor wants to manage, is already in place,
the addition fails, which the method signalizes with the returned boolean, symbolizing the success of the method.

~~~
// Jangine ships with this and some other processors.
RenderProcessor processor;

processor = new RenderProcessor();

boolean result;

result = ecs.addProcessor(processor);
if (!result) {
    // Another processor seems to already manage one or more of the components subclasses,
    // this processor wants to manage.
    // Another reason can be, that the requirements, set by this processor, are not met by the system.
}
~~~

### Removing a processor

This function also provides a boolean, symbolizing the success of this method.

~~~
boolean result;

result = ecs.rmvProcessor(processor);
if (!result) {
    // This is most likely due to the processor not being part of the system.
}
~~~

### Creating an entity

To create an entity, just being an integer, you just need to call one method. <br>
From there on, you should keep track of this entity, because otherwise, there is no way to remove it
from the system.

~~~
int entityId;

entityId = ecs.addEntity();
~~~

### Removing an entity

Removing an entity is very simple. You should clear your own reference, being the integer, too,
as the id will be reused, if you decide to add a new entity.

This method returns a boolean, symbolizing the success of the method, as well.

~~~
boolean result;

result = ecs.rmvEntity(entityId);
if (!result) {
    // This means, that the entity did not exist in this system.
}
else entityId = -1;
~~~

### Adding a component

To add a component, you need to create it yourself and specify the entity you want to add it to. <br>
There also needs to be a processor, that manages the subclass of the component class this component is of.

You can also specify, that the system should overwrite any component of the same class already processed
for this entity, as otherwise, this component will not be added to this system.

~~~
if (ecs.isProcessing(RenderComponent.class)) {
    boolean result;

    result = ecs.addComponentToEntity(entityId, renderComponent, false);
    if (!result) {
        // There are three possible reasons:
        // - There is no processor for this component in this system.
        // - The entity does not exist in this system
        // - There is already a component of this subclass of the component class for this entity.
        //
        // The processor could have also rejected the component, if any problems occurr and the component
        // not being valid.
        // This is most likely to some requirement not being met, such as the entity not having some other
        // component that is required for this processor to process this component correctly.
    }
}
else {
    // Please first add a processor, which manages the subclass of the component class
    // of your component which you want to add.
}
~~~

### Removing a component

To remove a component, you just need to specify the component class and the entity. <br>
You do not need the specific component, as there can only every be one component of one class per entity.

The method also returns its success being a boolean.

~~~
boolean result;

result = ecs.rmvComponentFromEntity(entityId, RenderComponent.class);
if (!result) {
    // Either the entity is not inside this system,
    // or there was no component of that subclass of the component class registered for the specified entity id.
}
~~~

## Common Mistakes

### Requirements

Please make sure, to meet all requirements of a processor, as these problems are fairly hard to debug.

Furthermore, be aware, that if you remove a required processor, the relying processor can react
to it over the occuring event, but if it does not, you will have unmet requirements.
