package pong;


import internal.entity_component_system.specifics.collision.CollisionComponent;
import internal.entity_component_system.specifics.collision.events.ContainerCollisionEvent;
import internal.entity_component_system.specifics.collision.events.ObjectCollisionEvent;
import internal.entity_component_system.specifics.hitbox.RectangleHitboxComponent;
import internal.entity_component_system.specifics.position.PositionComponent;
import internal.entity_component_system.specifics.render.RenderComponent;
import internal.entity_component_system.specifics.velocity.VelocityComponent;
import internal.events.Event;
import internal.rendering.container.A_Scene;
import internal.rendering.mesh.TexturedAMesh;
import internal.top_classes.A_Entity;
import org.joml.Vector2d;

import java.util.List;


public class Ball extends A_Entity {


    public Ball(A_Scene scene, double width, double height, double x, double y, String imagePath) {
        super(scene);

        _VELOCITY = new Vector2d(1, 1);
        _POSITION = new Vector2d(x, y);

        p_addComponent(new PositionComponent(_POSITION));
        p_addComponent(new VelocityComponent(_VELOCITY, 7));

        p_addComponent(new RectangleHitboxComponent(width, height));
        p_addComponent(new CollisionComponent());

        _WIDTH = width;
        _HEIGHT = height;

        _MESH = new TexturedAMesh(h_genVertices(), h_genIndices(), imagePath);
        p_addComponent(new RenderComponent(true, _MESH, "assets/default.glsl"));

        h_setUpCallbacks();

        _collisionTimer = 0;
    }

    private float[] h_genVertices() {
        return new float[] {
                              0,               0, 0, 0, 0,
                 (float) _WIDTH,               0, 1, 0, 0,
                (float) _HEIGHT, (float) _HEIGHT, 1, 1, 0,
                              0, (float) _HEIGHT, 0, 1, 0,
        };
    }
    private int[] h_genIndices() {
        return new int[] {
                0, 1, 2,
                3, 2, 0,
        };
    }

    private void h_setUpCallbacks() {
        p_PORT.registerFunction(this::onContainerCollision, List.of(ContainerCollisionEvent.class));
        p_PORT.registerFunction(this::onObjectCollision, List.of(ObjectCollisionEvent.class));
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final Vector2d _VELOCITY;
    private final Vector2d _POSITION;

    private final double _WIDTH, _HEIGHT;

    private final TexturedAMesh _MESH;

    // NON-FINALS //

    private int _collisionTimer;


    // -+- UPDATE LOOP -+- //

    @Override
    public void update(double deltaTime) {
        _collisionTimer--;
    }


    // -+- CALLBACKS -+- //

    public void onContainerCollision(Event event) {
        ContainerCollisionEvent cce;

        cce = (ContainerCollisionEvent) event;

        if (cce.object.positionComponent.owningEntity != getId()) return;

        switch (cce.collisionAxis) {
            case X -> _VELOCITY.mul(1, -1);
            case Y -> _VELOCITY.mul(-1, 1);
        }
    }
    public void onObjectCollision(Event event) {
        ObjectCollisionEvent oce;

        oce = (ObjectCollisionEvent) event;

        if (_collisionTimer > 0) return;

        if (oce.object.positionComponent.owningEntity != getId()) return;

        switch (oce.collisionAxis) {
            case X -> _VELOCITY.mul(1, -1);
            case Y -> _VELOCITY.mul(-1, 1);
        }

        _collisionTimer = 10;
    }


}