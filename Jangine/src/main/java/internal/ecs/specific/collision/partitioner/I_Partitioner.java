package internal.ecs.specific.collision.partitioner;


import internal.ecs.specific.collision.CollisionComponent;
import internal.ecs.specific.position.PositionComponent;
import internal.ecs.specific.size.SizeComponent;
import internal.ecs.specific.size.SizeComponentSystem;

import java.util.HashSet;


public interface I_Partitioner {


    HashSet<CollisionComponent> getPossibleCollisions(CollisionComponent component);

    void addComponent(CollisionComponent component, PositionComponent position, SizeComponent size);
    void rmvComponent(CollisionComponent component);

    void update();


}