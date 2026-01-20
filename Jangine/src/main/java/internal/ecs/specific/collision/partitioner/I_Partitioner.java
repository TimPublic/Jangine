package internal.ecs.specific.collision.partitioner;


import internal.ecs.specific.collision.CollisionComponent;
import internal.ecs.specific.position.PositionComponent;
import internal.ecs.specific.size.SizeComponent;
import org.joml.Vector2d;

import java.util.Collection;


public interface I_Partitioner {


    Collection<CollisionComponent> getPossibleCollisions(CollisionComponent component);

    void addComponent(CollisionComponent component, PositionComponent position, SizeComponent size);
    void rmvComponent(CollisionComponent component);

    void init(Vector2d position, double width, double height);

    void update();


}