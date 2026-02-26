package pong;


import internal.entity_component_system.specifics.collision.CollisionComponent;
import internal.entity_component_system.specifics.collision.events.ContainerCollisionEvent;
import internal.entity_component_system.specifics.hitbox.RectangleHitboxComponent;
import internal.entity_component_system.specifics.position.PositionComponent;
import internal.entity_component_system.specifics.render.RenderComponent;
import internal.entity_component_system.specifics.velocity.VelocityComponent;
import internal.events.I_Event;
import internal.events.implementations.ActiveEventPort;
import internal.events.input.key.KeyContinuedEvent;
import internal.rendering.container.A_Scene;
import internal.rendering.mesh.TexturedAMesh;
import internal.top_classes.A_Entity;
import org.joml.Vector2d;


public class Paddle extends A_Entity {


    public Paddle(A_Scene scene, double width, double height, String imagePath) {
        super(scene);

        _VELOCITY = new Vector2d(0, 0);
        _POSITION = new Vector2d(100, 0);

        p_addComponent(new PositionComponent(_POSITION));
        p_addComponent(new VelocityComponent(_VELOCITY, 7));

        p_addComponent(new RectangleHitboxComponent(width, height));
        p_addComponent(new CollisionComponent());

        _WIDTH = width;
        _HEIGHT = height;

        _MESH = new TexturedAMesh(h_genVertices(), h_genIndices(), imagePath);
        p_addComponent(new RenderComponent(true, _MESH, "assets/default.glsl"));

        EntityRegistry.get().add(this);

        h_setUpCallbacks(scene.SYSTEMS.window_port);
    }

    private float[] h_genVertices() {
        return new float[] {
                0,               0, 0, 0, 0,
                (float) _WIDTH,               0, 1, 0, 0,
                (float) _WIDTH, (float) _HEIGHT, 1, 1, 0,
                0, (float) _HEIGHT, 0, 1, 0,
        };
    }
    private int[] h_genIndices() {
        return new int[] {
                0, 1, 2,
                3, 2, 0,
        };
    }

    private void h_setUpCallbacks(ActiveEventPort windowPort) {
        p_PORT.addCallback(this::onContainerCollision);
        windowPort.addCallback(this::onKeyContinued);
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final Vector2d _VELOCITY;
    private final Vector2d _POSITION;

    private final double _WIDTH, _HEIGHT;

    private final TexturedAMesh _MESH;

    // NON-FINALS //

    private boolean _touchesLeft, _touchesRight;


    // -+- UPDATE LOOP -+- //

    @Override
    public void update(double deltaTime) {
        _VELOCITY.zero();
    }


    // -+- MOVEMENT -+- //

    private void _tryMoveLeft() {
        if (_touchesLeft) return;

        _VELOCITY.x -= 1;

        _touchesRight = false;
    }
    private void _tryMoveRight() {
        if (_touchesRight) return;

        _VELOCITY.x += 1;

        _touchesLeft = false;
    }


    // -+- CALLBACKS -+- //

    public void onContainerCollision(I_Event event) {
        ContainerCollisionEvent cce;

        if (!(event instanceof ContainerCollisionEvent)) return;

        cce = (ContainerCollisionEvent) event;

        if (cce.object.positionComponent.owningEntity != getId()) return;

        if (cce.container.getPosition().x > cce.object.positionComponent.position.x) _touchesLeft = true;
        else _touchesRight = true;
    }
    public void onKeyContinued(I_Event event) {
        KeyContinuedEvent kce;

        if (!(event instanceof KeyContinuedEvent)) return;

        kce = (KeyContinuedEvent) event;

        switch (kce.getKeyCode()) {
            case 68 -> _tryMoveRight();
            case 65 -> _tryMoveLeft();
        }
    }


}