package internal.ecs.specific.collision.data.object;


import org.joml.Vector2d;


public class CollisionObject {


    public CollisionObject(int entityID, Vector2d position) {
        id = entityID;

        this.position = position;
    }


    public int id;

    public Vector2d position;


}