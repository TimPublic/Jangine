package entity_component_system.specifics.collision.data;


public abstract class A_CollisionData {


    public A_CollisionData(COLLISION_AXIS collisionAxis, ObjectData object) {
        this.collisionAxis = collisionAxis;
        this.object = object;
    }


    public enum COLLISION_AXIS {
        X,
        Y,
    }


    public COLLISION_AXIS collisionAxis;
    public ObjectData object;


}