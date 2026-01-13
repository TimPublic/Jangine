package internal.ecs.quick_test.movement;


import internal.ecs.quick_test.JangineECS;
import internal.ecs.quick_test.JangineECS_ComponentSystem;
import internal.ecs.quick_test.collision.JangineECS_CollisionComponent;
import internal.ecs.quick_test.collision.JangineECS_CollisionSystem;
import internal.ecs.quick_test.collision.JangineEntityCollisionData;
import internal.ecs.quick_test.collision.JangineWindowCollisionData;


public class JangineECS_MovementComponentSystem extends JangineECS_ComponentSystem {


    @Override
    public void init(JangineECS system) {
        JangineECS_ComponentSystem componentSystem;
        JangineECS_CollisionSystem collisionSystem;

        componentSystem = system.findComponentSystem(JangineECS_CollisionComponent.class);
        if (componentSystem instanceof JangineECS_CollisionSystem) {
            collisionSystem = (JangineECS_CollisionSystem) componentSystem;
        } else {return;}

        collisionSystem.addEntityCollisionCallback(this::onEntityCollision);
        collisionSystem.addWindowCollisionCallback(this::onWindowCollision);
    }
    @Override
    public void kill(JangineECS system) {
        JangineECS_ComponentSystem componentSystem;
        JangineECS_CollisionSystem collisionSystem;

        componentSystem = system.findComponentSystem(JangineECS_CollisionComponent.class);
        if (componentSystem instanceof JangineECS_CollisionSystem) {
            collisionSystem = (JangineECS_CollisionSystem) componentSystem;
        } else {return;}

        collisionSystem.rmvEntityCollisionCallback(this::onEntityCollision);
        collisionSystem.rmvWindowCollisionCallback(this::onWindowCollision);
    }


    public void onEntityCollision(JangineEntityCollisionData data) {
        int entityID;

        entityID = data.entity;

        if (!_components.containsKey(entityID)) {return;}

        switch (data.collideAxis) {
            case X:
                ((JangineECS_MovementComponent) _components.get(entityID)).direction.mul(1, -1);
                break;
            case Y:
                ((JangineECS_MovementComponent) _components.get(entityID)).direction.mul(-1, 1);
                break;
        }
    }
    public void onWindowCollision(JangineWindowCollisionData data) {
        int entityID;

        entityID = data.entity;

        if (!_components.containsKey(entityID)) {return;}

        switch (data.collideAxis) {
            case X:
                ((JangineECS_MovementComponent) _components.get(entityID)).direction.mul(1, -1);
                break;
            case Y:
                ((JangineECS_MovementComponent) _components.get(entityID)).direction.mul(-1, 1);
                break;
        }
    }


}