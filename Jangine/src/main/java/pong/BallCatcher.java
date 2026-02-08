package pong;


import internal.entity_component_system.specifics.collision.CollisionComponent;
import internal.entity_component_system.specifics.collision.events.ObjectCollisionEvent;
import internal.entity_component_system.specifics.hitbox.RectangleHitboxComponent;
import internal.entity_component_system.specifics.position.PositionComponent;
import internal.entity_component_system.specifics.velocity.VelocityComponent;
import internal.events.Event;
import internal.rendering.container.A_Scene;
import internal.top_classes.A_Entity;
import org.joml.Vector2d;

import java.util.List;


public class BallCatcher extends A_Entity {


    public BallCatcher(A_Scene scene, double width, double height, double x, double y) {
        super(scene);

        _VELOCITY = new Vector2d(0, 0);
        _POSITION = new Vector2d(x, y);

        p_addComponent(new PositionComponent(_POSITION));
        p_addComponent(new VelocityComponent(_VELOCITY, 7));

        p_addComponent(new RectangleHitboxComponent(width, height));
        p_addComponent(new CollisionComponent());

        _WIDTH = width;
        _HEIGHT = height;

        EntityRegistry.get().add(this);

        h_setUpCallbacks();
    }

    private void h_setUpCallbacks() {
        p_PORT.registerFunction(this::onObjectCollision, List.of(ObjectCollisionEvent.class));
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final Vector2d _VELOCITY;
    private final Vector2d _POSITION;

    private final double _WIDTH, _HEIGHT;


    // -+- UPDATE LOOP -+- //

    @Override
    public void update(double deltaTime) {

    }


    // -+- CALLBACKS -+- //

    public void onObjectCollision(Event event) {
        ObjectCollisionEvent oce;

        oce = (ObjectCollisionEvent) event;

        if (oce.object.positionComponent.owningEntity != getId()) return;

        A_Entity entity = EntityRegistry.get().getEntity(oce.collidingObject.positionComponent.owningEntity);
        if (!(entity instanceof Ball)) return;

        Ball ball = (Ball) entity;

        System.out.println("CAUGHT!");
        System.out.println(oce.collidingObject.positionComponent.owningEntity);
        System.out.println(EntityRegistry.get().getEntity(oce.collidingObject.positionComponent.owningEntity));
        System.out.println(oce.object.positionComponent.owningEntity);
        System.out.println(EntityRegistry.get().getEntity(oce.object.positionComponent.owningEntity));

        ball.kill();
    }


}