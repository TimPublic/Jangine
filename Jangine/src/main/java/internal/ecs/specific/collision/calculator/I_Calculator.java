package internal.ecs.specific.collision.calculator;


import internal.ecs.specific.collision.data.data.CollisionData;
import internal.ecs.specific.collision.data.object.CircleObject;
import internal.ecs.specific.collision.data.object.RectangleObject;
import internal.rendering.container.Container;


public interface I_Calculator {


    boolean isColliding(RectangleObject objA, RectangleObject objB);
    boolean isColliding(RectangleObject objA, CircleObject objB);
    default boolean isColliding(CircleObject objA, RectangleObject objB) {
        return isColliding(objB, objA);
    }
    boolean isColliding(CircleObject objA, CircleObject objB);
    boolean isColliding(RectangleObject obj, Container container);
    boolean isColliding(CircleObject obj, Container container);


    CollisionData.COLLISION_AXIS getCollisionAxis(RectangleObject objA, RectangleObject objB);
    CollisionData.COLLISION_AXIS getCollisionAxis(RectangleObject objA, CircleObject objB);
    CollisionData.COLLISION_AXIS getCollisionAxis(RectangleObject obj, Container container);
    CollisionData.COLLISION_AXIS getCollisionAxis(CircleObject obj, Container container);


}