package internal.ecs.specific.collision.calculator;


import internal.ecs.specific.collision.CollisionComponent;
import internal.ecs.specific.collision.data.CollisionData;
import internal.ecs.specific.position.PositionComponent;
import internal.ecs.specific.size.SizeComponent;
import org.joml.Vector2d;


public interface I_Calculator {


    boolean collidesWithComponent(CollisionComponent component, PositionComponent componentPosition, SizeComponent componentSize, CollisionComponent collidingComponent, PositionComponent collidingPosition, SizeComponent collidingSize);
    boolean collidesWithContainer(CollisionComponent component, Vector2d containerPosition, double containerWidth, double containerHeight);


    CollisionData.COLLISION_AXIS getCollisionAxisWithComponent(CollisionComponent component, CollisionComponent collidingComponent);
    CollisionData.COLLISION_AXIS getCollisionAxisWithContainer(CollisionComponent component, Vector2d containerPosition, double containerWidth, double containerHeight);


}