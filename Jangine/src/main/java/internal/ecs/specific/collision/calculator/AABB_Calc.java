package internal.ecs.specific.collision.calculator;


import internal.ecs.specific.collision.data.data.CollisionData;
import internal.ecs.specific.collision.data.object.CircleObject;
import internal.ecs.specific.collision.data.object.RectangleObject;
import internal.rendering.container.Container;
import org.joml.Vector2d;


public class AABB_Calc implements I_Calculator {


    @Override
    public boolean isColliding(RectangleObject objA, RectangleObject objB) {
        boolean xOverlap, yOverlap;

        xOverlap = objA.position.x < objB.position.x + objB.width && objA.position.x + objA.width > objB.position.x;

        if (!xOverlap) {return false;}

        yOverlap = objA.position.y < objB.position.y + objB.height && objA.position.y + objA.height > objB.position.y;

        return yOverlap;
    }
    @Override
    public boolean isColliding(RectangleObject objA, CircleObject objB) {
        double rectHighestX, rectHighestY;
        double rectLowestX, rectLowestY;

        rectHighestX = objA.position.x + objA.width;
        rectHighestY = objA.position.y + objA.height;
        rectLowestX = objA.position.x;
        rectLowestY = objA.position.y;

        Vector2d nearestRectPoint;

        nearestRectPoint = new Vector2d(objB.position);

        if (objB.position.x > rectHighestX) {nearestRectPoint.x = rectHighestX;}
        else if (objB.position.x < rectLowestX) {nearestRectPoint.x = rectLowestX;}
        if (objB.position.y > rectHighestY) {nearestRectPoint.y = rectHighestY;}
        else if (objB.position.y < rectLowestY) {nearestRectPoint.y = rectLowestY;}

        double dist;

        dist = nearestRectPoint.distance(objB.position);

        return dist <= objB.radius;
    }
    @Override
    public boolean isColliding(CircleObject objA, CircleObject objB) {
        double dist;

        dist = objA.position.distance(objB.position);

        return dist <= Math.max(objA.radius, objB.radius);
    }
    @Override
    public boolean isColliding(RectangleObject obj, Container container) {
        boolean xOverlap, yOverlap;

        xOverlap = obj.position.x < container.getPosition().x || obj.position.x + obj.width > container.getPosition().x + container.getWidth();

        if (xOverlap) {return true;}

        yOverlap = obj.position.y < container.getPosition().y || obj.position.y + obj.height > container.getPosition().y + container.getHeight();

        return yOverlap;
    }
    @Override
    public boolean isColliding(CircleObject obj, Container container) {
        boolean xOverlap;
        boolean yOverlap;

        xOverlap = obj.radius <= Math.min(Math.abs(obj.position.x - container.getPosition().x), Math.abs(obj.position.x - container.getPosition().x + container.getWidth()));

        if (xOverlap) {return true;}

        yOverlap = obj.radius <= Math.min(Math.abs(obj.position.y - container.getPosition().y), Math.abs(obj.position.y - container.getPosition().y + container.getHeight()));

        return yOverlap;
    }


    @Override
    public CollisionData.COLLISION_AXIS getCollisionAxis(RectangleObject objA, RectangleObject objB) {
        return null;
    }
    @Override
    public CollisionData.COLLISION_AXIS getCollisionAxis(RectangleObject objA, CircleObject objB) {
        return null;
    }
    @Override
    public CollisionData.COLLISION_AXIS getCollisionAxis(RectangleObject obj, Container container) {
        return null;
    }
    @Override
    public CollisionData.COLLISION_AXIS getCollisionAxis(CircleObject obj, Container container) {
        return null;
    }


}