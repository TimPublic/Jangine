package internal.ecs.specific.collision.data.object;


import org.joml.Vector2d;


public class RectangleObject extends CollisionObject {


    public RectangleObject(int entityID, Vector2d position, double width, double height) {
        super(entityID, position);

        this.width = width;
        this.height = height;
    }


    public double width, height;


}