package internal.ecs.quick_test.movement;

import internal.ecs.quick_test.JangineECS_Component;
import internal.ecs.quick_test.JangineECS;
import internal.ecs.quick_test.JangineECS_ComponentSystem;
import internal.ecs.quick_test.collision.JangineECS_CollisionComponent;
import internal.ecs.quick_test.collision.JangineECS_CollisionSystem;
import internal.ecs.quick_test.collision.JangineEntityCollisionData;
import internal.ecs.quick_test.collision.JangineWindowCollisionData;
import internal.ecs.quick_test.transform.JangineECS_TransformComponent;
import internal.ecs.quick_test.transform.JangineECS_TransformSystem;

import java.util.HashMap;


public class JangineECS_MovementComponentSystem extends JangineECS_ComponentSystem {


    private final HashMap<JangineECS_MovementComponent, JangineECS_TransformComponent> _movementToTransformMap;


    public JangineECS_MovementComponentSystem() {
        _movementToTransformMap = new HashMap<>();
    }


    @Override
    public void init(JangineECS system) {
        JangineECS_ComponentSystem componentSystem;
        JangineECS_CollisionSystem collisionSystem;

        componentSystem = system.findComponentSystem(JangineECS_CollisionComponent.class);
        if (componentSystem instanceof JangineECS_CollisionSystem) {
            collisionSystem = (JangineECS_CollisionSystem) componentSystem;

            collisionSystem.addEntityCollisionCallback(this::onEntityCollision);
            collisionSystem.addWindowCollisionCallback(this::onWindowCollision);
        }

        JangineECS_TransformSystem transformSystem;

        componentSystem = system.findComponentSystem(JangineECS_TransformComponent.class);
        if (componentSystem instanceof JangineECS_TransformSystem) {
            transformSystem = (JangineECS_TransformSystem) componentSystem;

            transformSystem.registerComponentAddedCallback(this::onTransformCollisionComponentAdded);
            transformSystem.registerComponentDeletedCallback(this::onTransformCollisionComponentRemoved);
        }
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

    public void onTransformCollisionComponentAdded(JangineECS_Component component) {
        if (!_components.containsKey(component.entityID)) {return;}

        _movementToTransformMap.put((JangineECS_MovementComponent) component, (JangineECS_TransformComponent) _components.get(component.entityID));
    }
    public void onTransformCollisionComponentRemoved(JangineECS_Component component) {
        _movementToTransformMap.remove(_components.get(component.entityID)); // Worst case: Null, which is no problem for a HashMap removal.
    }


}