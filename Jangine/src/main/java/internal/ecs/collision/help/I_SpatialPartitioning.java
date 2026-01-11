package internal.ecs.collision.help;


import internal.ecs.collision.component.JECS_CollisionComponent;

import java.util.ArrayList;


public interface I_SpatialPartitioning {


    ArrayList<JECS_CollisionComponent> getPossibleCollisions(JECS_CollisionComponent component);


    void addComponent(JECS_CollisionComponent component);
    void rmvComponent(JECS_CollisionComponent component);


}