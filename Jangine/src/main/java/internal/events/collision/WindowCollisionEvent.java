package internal.events.collision;


import internal.ecs.specific.collision.data.data.ContainerCollisionData;


public class WindowCollisionEvent extends CollisionEvent {


    public WindowCollisionEvent(ContainerCollisionData data) {
        this.data = data;
    }


    public ContainerCollisionData data;


}