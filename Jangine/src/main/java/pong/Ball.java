package pong;


import internal.entity_component_system.System;
import internal.entity_component_system.specifics.collision.CollisionComponent;
import internal.entity_component_system.specifics.collision.events.ContainerCollisionEvent;
import internal.entity_component_system.specifics.hitbox.RectangleHitboxComponent;
import internal.entity_component_system.specifics.position.PositionComponent;
import internal.entity_component_system.specifics.render.RenderComponent;
import internal.entity_component_system.specifics.velocity.VelocityComponent;
import internal.events.Event;
import internal.events.EventHandler;
import internal.events.EventListeningPort;
import internal.rendering.mesh.TexturedAMesh;
import org.joml.Vector2d;

import java.util.List;


public class Ball {


    public Ball(System ecs, EventHandler handler) {
        _ID = ecs.addEntity();

        float[] vertices;
        int[] indices;
        TexturedAMesh mesh;

        vertices = new float[] {
                0, 0, 0, 0, 0,
                50, 0, 1, 0, 0,
                50, 50, 1, 1, 0,
                0, 50, 0, 1, 0,
        };
        indices = new int[] {
                0, 1, 2,
                2, 3, 0,
        };

        mesh = new TexturedAMesh(vertices, indices, "assets/ui.png");

        _POSITION = new Vector2d(200, 0);
        _VELOCITY = new Vector2d(2, 0);

        ecs.addComponentToEntity(_ID, new PositionComponent(_POSITION), false);
        ecs.addComponentToEntity(_ID, new RenderComponent(true, mesh, "assets/default.glsl"), false);
        ecs.addComponentToEntity(_ID, new RectangleHitboxComponent(50, 50), false);
        ecs.addComponentToEntity(_ID, new CollisionComponent(), false);
        ecs.addComponentToEntity(_ID, new VelocityComponent(_VELOCITY), false);

        _PORT = handler.register();

        _PORT.registerFunction(this::onContainerCollision, List.of(ContainerCollisionEvent.class));
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final int _ID;

    private final Vector2d _POSITION;
    private final Vector2d _VELOCITY;

    private final EventListeningPort _PORT;


    // -+- CALLBACKS -+- //

    public void onContainerCollision(Event event) {
        ContainerCollisionEvent cce;

        cce = (ContainerCollisionEvent) event;

        if (cce.object.positionComponent.owningEntity != _ID) return;

        switch (cce.collisionAxis) {
            case X -> _VELOCITY.mul(1, -1);
            case Y -> _VELOCITY.mul(-1, 1);
        }
    }


}