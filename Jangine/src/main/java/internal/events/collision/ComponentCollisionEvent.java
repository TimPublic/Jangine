package internal.events.collision;


import internal.ecs.specific.collision.data.data.ComponentCollisionData;


public class ComponentCollisionEvent extends CollisionEvent {


    public ComponentCollisionEvent(ComponentCollisionData data) {
        this.data = data;
    }


    public ComponentCollisionData data;


}