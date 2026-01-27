package internal.entity_component_system.specifics.collision.events;


import internal.entity_component_system.specifics.collision.data.A_CollisionData;
import internal.entity_component_system.specifics.collision.data.ObjectData;
import internal.events.Event;


public abstract class A_CollisionEvent extends Event {


    // -+- CREATION -+- //

    public A_CollisionEvent(A_CollisionData.COLLISION_AXIS collisionAxis, ObjectData object) {
        this.object = object;
        this.collisionAxis = collisionAxis;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    public final ObjectData object;
    public final A_CollisionData.COLLISION_AXIS collisionAxis;


}