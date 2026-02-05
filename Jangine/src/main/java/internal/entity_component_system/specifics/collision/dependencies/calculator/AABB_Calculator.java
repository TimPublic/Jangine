package internal.entity_component_system.specifics.collision.dependencies.calculator;


import internal.entity_component_system.specifics.collision.data.A_CollisionData;
import internal.entity_component_system.specifics.collision.data.ObjectData;
import internal.entity_component_system.specifics.hitbox.A_HitboxComponent;
import internal.entity_component_system.specifics.hitbox.CircleHitboxComponent;
import internal.entity_component_system.specifics.hitbox.RectangleHitboxComponent;
import internal.rendering.container.A_Container;
import org.joml.Vector2d;


public class AABB_Calculator implements I_CollisionCalculator {


    // -+- GETTERS -+- //

    @Override
    public A_CollisionData.COLLISION_AXIS getCollisionAxis(ObjectData objA, ObjectData objB) {
        A_HitboxComponent hitboxA, hitboxB;
        Vector2d positionA, positionB;

        hitboxA = objA.hitboxComponent;
        hitboxB = objB.hitboxComponent;
        positionA = objA.positionComponent.position;
        positionB = objB.positionComponent.position;

        if (hitboxA instanceof RectangleHitboxComponent && hitboxB instanceof RectangleHitboxComponent) return h_getCollisionAxis((RectangleHitboxComponent) hitboxA, positionA, (RectangleHitboxComponent) hitboxB, positionB);
        if (hitboxA instanceof RectangleHitboxComponent && hitboxB instanceof CircleHitboxComponent) return h_getCollisionAxis((RectangleHitboxComponent) hitboxA, positionA, (CircleHitboxComponent) hitboxB, positionB);
        if (hitboxA instanceof CircleHitboxComponent && hitboxB instanceof RectangleHitboxComponent) return h_getCollisionAxis((CircleHitboxComponent) hitboxA, positionA, (RectangleHitboxComponent) hitboxB, positionB);
        if (hitboxA instanceof CircleHitboxComponent && hitboxB instanceof CircleHitboxComponent) return h_getCollisionAxis((CircleHitboxComponent) hitboxA, positionA, (CircleHitboxComponent) hitboxB, positionB);

        return null;
    }
    @Override
    public A_CollisionData.COLLISION_AXIS getCollisionAxis(ObjectData objA, A_Container container) {
        A_HitboxComponent hitboxA;

        hitboxA = objA.hitboxComponent;

        if (hitboxA instanceof RectangleHitboxComponent) return h_getCollisionAxis((RectangleHitboxComponent) hitboxA, objA.positionComponent.position, container);
        if (hitboxA instanceof CircleHitboxComponent) return h_getCollisionAxis((CircleHitboxComponent) hitboxA, objA.positionComponent.position, container);

        return null;
    }

    private A_CollisionData.COLLISION_AXIS h_getCollisionAxis(RectangleHitboxComponent objA, Vector2d posA, RectangleHitboxComponent objB, Vector2d posB) {
        double xOverlap, yOverlap;

        xOverlap = Math.min(posA.x + objA.width, posB.x + objB.width) - Math.max(posA.x, posB.x);
        yOverlap = Math.min(posA.y + objA.height, posB.y + objB.height) - Math.max(posA.y, posB.y);

        if (xOverlap > yOverlap) {
            return A_CollisionData.COLLISION_AXIS.X;
        }

        return A_CollisionData.COLLISION_AXIS.Y;
    }
    private A_CollisionData.COLLISION_AXIS h_getCollisionAxis(RectangleHitboxComponent objA, Vector2d posA, CircleHitboxComponent objB, Vector2d posB) {
        double xOverlap, yOverlap;

        xOverlap = Math.min(posA.x + objA.width, posB.x + objB.radius) - Math.max(posA.x, posB.x - objB.radius);
        yOverlap = Math.min(posA.y + objA.height, posB.y + objB.radius) - Math.max(posA.y, posB.y - objB.radius);

        if (xOverlap > yOverlap) {
            return A_CollisionData.COLLISION_AXIS.X;
        }

        return A_CollisionData.COLLISION_AXIS.Y;
    }
    private A_CollisionData.COLLISION_AXIS h_getCollisionAxis(CircleHitboxComponent objA, Vector2d posA, CircleHitboxComponent objB, Vector2d posB) {
        double xOverlap, yOverlap;

        xOverlap = Math.min(posA.x + objA.radius, posB.x + objB.radius) - Math.max(posA.x - objA.radius, posB.x - objB.radius);
        yOverlap = Math.min(posA.y + objA.radius, posB.y + objB.radius) - Math.max(posA.y - objB.radius, posB.y - objB.radius);

        if (xOverlap > yOverlap) {
            return A_CollisionData.COLLISION_AXIS.X;
        }

        return A_CollisionData.COLLISION_AXIS.Y;
    }
    private A_CollisionData.COLLISION_AXIS h_getCollisionAxis(CircleHitboxComponent objA, Vector2d posA, RectangleHitboxComponent objB, Vector2d posB) {
        return h_getCollisionAxis(objB, posB, objA, posA);
    }
    private A_CollisionData.COLLISION_AXIS h_getCollisionAxis(RectangleHitboxComponent obj, Vector2d pos, A_Container container) {
        if (pos.x < container.getPosition().x || pos.x + obj.width > container.getPosition().x + container.getWidth()) return A_CollisionData.COLLISION_AXIS.X;

        return A_CollisionData.COLLISION_AXIS.Y;
    }
    private A_CollisionData.COLLISION_AXIS h_getCollisionAxis(CircleHitboxComponent obj, Vector2d pos, A_Container container) {
        if (pos.x - obj.radius < container.getPosition().x || pos.x + obj.radius > container.getPosition().x + container.getWidth()) return A_CollisionData.COLLISION_AXIS.X;

        return A_CollisionData.COLLISION_AXIS.Y;
    }


    // -+- CHECKERS -+- //

    @Override
    public boolean isCollidingWith(ObjectData objA, ObjectData objB) {
        A_HitboxComponent hitboxA, hitboxB;
        Vector2d positionA, positionB;

        hitboxA = objA.hitboxComponent;
        hitboxB = objB.hitboxComponent;
        positionA = objA.positionComponent.position;
        positionB = objB.positionComponent.position;

        if (hitboxA instanceof RectangleHitboxComponent && hitboxB instanceof RectangleHitboxComponent) return h_isColliding((RectangleHitboxComponent) hitboxA, positionA, (RectangleHitboxComponent) hitboxB, positionB);
        if (hitboxA instanceof RectangleHitboxComponent && hitboxB instanceof CircleHitboxComponent) return h_isColliding((RectangleHitboxComponent) hitboxA, positionA, (CircleHitboxComponent) hitboxB, positionB);
        if (hitboxA instanceof CircleHitboxComponent && hitboxB instanceof RectangleHitboxComponent) return h_isColliding((CircleHitboxComponent) hitboxA, positionA, (RectangleHitboxComponent) hitboxB, positionB);
        if (hitboxA instanceof CircleHitboxComponent && hitboxB instanceof CircleHitboxComponent) return h_isColliding((CircleHitboxComponent) hitboxA, positionA, (CircleHitboxComponent) hitboxB, positionB);

        return false;
    }
    @Override
    public boolean isCollidingWith(ObjectData objA, A_Container container) {
        A_HitboxComponent hitboxA;

        hitboxA = objA.hitboxComponent;

        if (hitboxA instanceof RectangleHitboxComponent) return h_isColliding((RectangleHitboxComponent) hitboxA, objA.positionComponent.position, container);
        if (hitboxA instanceof CircleHitboxComponent) return h_isColliding((CircleHitboxComponent) hitboxA, objA.positionComponent.position, container);

        return false;
    }

    public boolean h_isColliding(RectangleHitboxComponent objA, Vector2d posA, RectangleHitboxComponent objB, Vector2d posB) {
        boolean xOverlap, yOverlap;

        xOverlap = posA.x < posB.x + objB.width && posA.x + objA.width > posB.x;
        yOverlap = posA.y < posB.y + objB.height && posA.y + objA.height > posB.y;

        return xOverlap && yOverlap;
    }
    public boolean h_isColliding(RectangleHitboxComponent objA, Vector2d posA, CircleHitboxComponent objB, Vector2d posB) {
        Vector2d closestPoint;

        closestPoint = new Vector2d(posB);

        if (closestPoint.x > posA.x + objA.width) closestPoint.x = posA.x + objA.width;
        else if (closestPoint.x < posA.x) closestPoint.x = posA.x;
        if (closestPoint.y > posA.y + objA.height) closestPoint.y = posA.y + objA.height;
        else if (closestPoint.y < posA.y) closestPoint.y = posA.y;

        return posB.distance(closestPoint) <= objB.radius;
    }
    public boolean h_isColliding(CircleHitboxComponent objA, Vector2d posA, RectangleHitboxComponent objB, Vector2d posB) {
        return h_isColliding(objB, posB, objA, posA);
    }
    public boolean h_isColliding(CircleHitboxComponent objA, Vector2d posA, CircleHitboxComponent objB, Vector2d posB) {
        return posA.distance(posB) < Math.max(objA.radius, objB.radius);
    }
    public boolean h_isColliding(RectangleHitboxComponent obj, Vector2d pos, A_Container container) {
        boolean xOverlap, yOverlap;

        xOverlap = pos.x < container.getPosition().x || pos.x + obj.width > container.getPosition().x + container.getWidth();

        if (xOverlap) return true;

        yOverlap = pos.y < container.getPosition().y || pos.y + obj.height > container.getPosition().y + container.getHeight();

        return yOverlap;
    }
    public boolean h_isColliding(CircleHitboxComponent obj, Vector2d pos, A_Container container) {
        boolean xOverlap, yOverlap;

        xOverlap = pos.x - obj.radius < container.getPosition().x || pos.x + obj.radius > container.getPosition().x + container.getWidth();

        if (xOverlap) return true;

        yOverlap = pos.y - obj.radius < container.getPosition().y || pos.y + obj.radius > container.getPosition().y + container.getHeight();

        return yOverlap;
    }


}