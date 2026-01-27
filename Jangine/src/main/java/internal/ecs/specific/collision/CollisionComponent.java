package internal.ecs.specific.collision;


import internal.ecs.ECS_Component;


public class CollisionComponent extends ECS_Component {


    public enum COLLISION_TYPE {
        RECTANGLE,
        CIRCLE,
    }


    COLLISION_TYPE collisionType;


}