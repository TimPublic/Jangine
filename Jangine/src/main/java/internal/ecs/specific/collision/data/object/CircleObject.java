package internal.ecs.specific.collision.data.object;


import org.joml.Vector2d;


public class CircleObject extends CollisionObject {


    public CircleObject(int entityID, Vector2d position, double radius) {
        super(entityID, position);

        this.radius = radius;
    }


    public double radius;


}