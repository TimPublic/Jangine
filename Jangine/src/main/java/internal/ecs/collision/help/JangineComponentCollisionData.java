package internal.ecs.collision.help;


public class JangineComponentCollisionData extends JangineCollisionData {


    public JangineComponentCollisionData(COLLISION_AXIS collisionAxis, int firstEntity, int secondEntity) {
        this.collisionAxis = collisionAxis;

        this.firstEntity = firstEntity;
        this.secondEntity = secondEntity;
    }


    public int firstEntity, secondEntity;


}