package entity_component_system.specifics.collision.dependencies.calculator;


import internal.ecs.specific.collision.calculator.I_Calculator;
import internal.ecs.specific.collision.data.data.CollisionData;
import internal.ecs.specific.collision.data.object.CircleObject;
import internal.ecs.specific.collision.data.object.RectangleObject;
import internal.rendering.container.Container;
import org.joml.Vector2d;


public class AABB_Calculator implements I_Calculator {


    // -+- GETTERS -+- //

    @Override
    public CollisionData.COLLISION_AXIS getCollisionAxis(RectangleObject objA, RectangleObject objB) {
        double xOverlap, yOverlap;

        xOverlap = Math.min(objA.position.x + objA.width, objB.position.x + objB.width) - Math.max(objA.position.x, objB.position.x);
        yOverlap = Math.min(objA.position.y + objA.height, objB.position.y + objB.height) - Math.max(objA.position.y, objB.position.y);

        if (xOverlap > yOverlap) {
            return CollisionData.COLLISION_AXIS.X;
        }

        return CollisionData.COLLISION_AXIS.Y;
    }
    @Override
    public CollisionData.COLLISION_AXIS getCollisionAxis(RectangleObject objA, CircleObject objB) {
        double xOverlap, yOverlap;

        xOverlap = Math.min(objA.position.x + objA.width, objB.position.x + objB.radius) - Math.max(objA.position.x, objB.position.x - objB.radius);
        yOverlap = Math.min(objA.position.y + objA.height, objB.position.y + objB.radius) - Math.max(objA.position.y, objB.position.y - objB.radius);

        if (xOverlap > yOverlap) {
            return CollisionData.COLLISION_AXIS.X;
        }

        return CollisionData.COLLISION_AXIS.Y;
    }
    @Override
    public CollisionData.COLLISION_AXIS getCollisionAxis(RectangleObject obj, Container container) {
        if (obj.position.x < container.getPosition().x || obj.position.x + obj.width > container.getPosition().x + container.getWidth()) return CollisionData.COLLISION_AXIS.X;

        return CollisionData.COLLISION_AXIS.Y;
    }
    @Override
    public CollisionData.COLLISION_AXIS getCollisionAxis(CircleObject obj, Container container) {
        if (obj.position.x - obj.radius < container.getPosition().x || obj.position.x + obj.radius > container.getPosition().x + container.getWidth()) return CollisionData.COLLISION_AXIS.X;

        return CollisionData.COLLISION_AXIS.Y;
    }


    // -+- CHECKERS -+- //

    @Override
    public boolean isColliding(RectangleObject objA, RectangleObject objB) {
        boolean xOverlap, yOverlap;

        xOverlap = objA.position.x < objB.position.x + objB.width && objA.position.x + objA.width > objB.position.x;
        yOverlap = objA.position.y < objB.position.y + objB.height && objA.position.y + objA.height > objB.position.y;

        return xOverlap && yOverlap;
    }
    @Override
    public boolean isColliding(RectangleObject objA, CircleObject objB) {
        Vector2d closestPoint;

        closestPoint = new Vector2d(objB.position);

        if (closestPoint.x > objA.position.x + objA.width) closestPoint.x = objA.position.x + objA.width;
        else if (closestPoint.x < objA.position.x) closestPoint.x = objA.position.x;
        if (closestPoint.y > objA.position.y + objA.height) closestPoint.y = objA.position.y + objA.height;
        else if (closestPoint.y < objA.position.y) closestPoint.y = objA.position.y;

        return objB.position.distance(closestPoint) <= objB.radius;
    }
    @Override
    public boolean isColliding(CircleObject objA, CircleObject objB) {
        return objA.position.distance(objB.position) < Math.max(objA.radius, objB.radius);
    }
    @Override
    public boolean isColliding(RectangleObject obj, Container container) {
        boolean xOverlap, yOverlap;

        xOverlap = obj.position.x < container.getPosition().x || obj.position.x + obj.width > container.getPosition().x + container.getWidth();

        if (xOverlap) return true;

        yOverlap = obj.position.y < container.getPosition().y || obj.position.y + obj.height > container.getPosition().y + container.getHeight();

        return yOverlap;
    }
    @Override
    public boolean isColliding(CircleObject obj, Container container) {
        boolean xOverlap, yOverlap;

        xOverlap = obj.position.x - obj.radius < container.getPosition().x || obj.position.x + obj.radius > container.getPosition().x + container.getWidth();

        if (xOverlap) return true;

        yOverlap = obj.position.y - obj.radius < container.getPosition().y || obj.position.y + obj.radius > container.getPosition().y + container.getHeight();

        return yOverlap;
    }


}