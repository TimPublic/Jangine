package internal.ecs.specific.collision.data;


import internal.ecs.specific.collision.CollisionComponent;


public abstract class CollisionData {


    public enum COLLISION_AXIS {
        X,
        Y,
    }


    public CollisionData(CollisionComponent component, COLLISION_AXIS collisionAxis) {
        this.component = component;

        this.collisionAxis = collisionAxis;
    }


    public CollisionComponent component;

    public COLLISION_AXIS collisionAxis;


}