package internal.ecs.specific.collision.data.data;


import internal.ecs.specific.collision.CollisionComponent;


public class ComponentCollisionData extends CollisionData {


    public ComponentCollisionData(CollisionComponent component, CollisionData.COLLISION_AXIS collisionAxis, CollisionComponent collidingComponent) {
        super(component, collisionAxis);

        this.collidingComponent = collidingComponent;
    }


    public CollisionComponent collidingComponent;


}