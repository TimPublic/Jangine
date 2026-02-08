package pong;


import internal.entity_component_system.System;
import internal.entity_component_system.specifics.collision.CollisionComponent;
import internal.entity_component_system.specifics.collision.data.A_CollisionData;
import internal.entity_component_system.specifics.collision.events.ContainerCollisionEvent;
import internal.entity_component_system.specifics.hitbox.RectangleHitboxComponent;
import internal.entity_component_system.specifics.position.PositionComponent;
import internal.entity_component_system.specifics.render.RenderComponent;
import internal.entity_component_system.specifics.velocity.VelocityComponent;
import internal.events.Event;
import internal.events.EventListeningPort;
import internal.events.input.key.KeyContinuedEvent;
import internal.rendering.mesh.TexturedAMesh;
import org.joml.Vector2d;

import java.util.List;


public class Paddle {


    // -+- CREATION -+- //

    public Paddle(System ecs, EventListeningPort windowPort) {
        _ID = ecs.addEntity();

        float[] vertices;
        int[] indices;
        TexturedAMesh mesh;

        vertices = new float[] {
                0, 0, 0, 0, 0,
                100, 0, 1, 0, 0,
                100, 100, 1, 1, 0,
                0, 100, 0, 1, 0,
        };
        indices = new int[] {
                0, 1, 2,
                2, 3, 0,
        };

        mesh = new TexturedAMesh(vertices, indices, "assets/ui.png");

        _POSITION = new Vector2d(0, 0);
        _VELOCITY = new Vector2d(0, 0);

        ecs.addComponentToEntity(_ID, new PositionComponent(_POSITION), false);
        ecs.addComponentToEntity(_ID, new RenderComponent(true, mesh, "assets/default.glsl"), false);
        ecs.addComponentToEntity(_ID, new RectangleHitboxComponent(100, 100), false);
        ecs.addComponentToEntity(_ID, new CollisionComponent(), false);
        ecs.addComponentToEntity(_ID, new VelocityComponent(_VELOCITY), false);

        windowPort.registerFunction(this::onKeyContinued, List.of(KeyContinuedEvent.class));
        windowPort.registerFunction(this::onCollisionWithWindow, List.of(ContainerCollisionEvent.class));

        _speed = 7;

        _touchesWallLeft = false;
        _touchesWallRight = false;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final int _ID;

    private final Vector2d _POSITION;
    private final Vector2d _VELOCITY;


    // NON-FINALS //

    private int _speed;

    private boolean _touchesWallLeft, _touchesWallRight;


    // -+- UPDATE LOOP -+- //

    public void update() {
        _VELOCITY.set(0, 0);
    }


    // -+- CALLBACKS -+- //

    public void onKeyContinued(Event event) {
        KeyContinuedEvent kce;

        kce = (KeyContinuedEvent) event;

        int keyCode;

        keyCode = kce.getKeyCode();

        switch (keyCode) {
            case 65: // s
                h_tryMoveLeft();
                break;
            case 68: // d
                h_tryMoveRight();
                break;
            default:
                break;
        }
    }
    public void onCollisionWithWindow(Event event) {
        ContainerCollisionEvent cce;

        cce = (ContainerCollisionEvent) event;

        if (cce.object.hitboxComponent.owningEntity != _ID) return;

        if (cce.collisionAxis == A_CollisionData.COLLISION_AXIS.Y) {
            if (cce.object.positionComponent.position.x < cce.container.getPosition().x) {
                _touchesWallLeft = true;
            }
            else _touchesWallRight = true;
        }
    }

    private void h_tryMoveLeft() {
        if (_touchesWallLeft) return;

        _VELOCITY.x = -_speed;
        _touchesWallRight = false;
    }
    private void h_tryMoveRight() {
        if (_touchesWallRight) return;

        _VELOCITY.x = _speed;
        _touchesWallLeft = false;
    }


}