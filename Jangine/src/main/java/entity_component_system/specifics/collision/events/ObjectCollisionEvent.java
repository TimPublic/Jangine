package entity_component_system.specifics.collision.events;


import entity_component_system.specifics.collision.data.A_CollisionData;
import entity_component_system.specifics.collision.data.ObjectData;


public class ObjectCollisionEvent extends A_CollisionEvent {


    // -+- CREATION -+- //

    public ObjectCollisionEvent(A_CollisionData.COLLISION_AXIS collisionAxis, ObjectData object, ObjectData collidingObject) {
        super(collisionAxis, object);

        this.collidingObject = collidingObject;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    public final ObjectData collidingObject;


}