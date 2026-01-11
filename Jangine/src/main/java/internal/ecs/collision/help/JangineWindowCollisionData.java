package internal.ecs.collision.help;


public class JangineWindowCollisionData extends JangineCollisionData {


    public JangineWindowCollisionData(COLLISION_AXIS collisionAxis, int entity) {
        this.collisionAxis = collisionAxis;

        this.entity = entity;
    }


    public int entity;


}