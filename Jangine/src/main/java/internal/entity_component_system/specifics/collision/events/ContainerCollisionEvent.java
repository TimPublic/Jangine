package internal.entity_component_system.specifics.collision.events;


import internal.entity_component_system.specifics.collision.data.A_CollisionData;
import internal.entity_component_system.specifics.collision.data.ObjectData;
import internal.rendering.container.Container;


public class ContainerCollisionEvent extends A_CollisionEvent {


    // -+- CREATION -+- //

    public ContainerCollisionEvent(A_CollisionData.COLLISION_AXIS collisionAxis, ObjectData object, Container container) {
        super(collisionAxis, object);

        this.container = container;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    public final Container container;


}