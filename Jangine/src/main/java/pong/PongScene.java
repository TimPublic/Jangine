package pong;


import internal.batch.specifics.TextureBatchProcessor;
import internal.entity_component_system.specifics.collision.CollisionComponent;
import internal.entity_component_system.specifics.collision.CollisionProcessor;
import internal.entity_component_system.specifics.collision.dependencies.calculator.AABB_Calculator;
import internal.entity_component_system.specifics.collision.dependencies.spatial_partitioner.QuadTree;
import internal.entity_component_system.specifics.hitbox.HitboxProcessor;
import internal.entity_component_system.specifics.hitbox.RectangleHitboxComponent;
import internal.entity_component_system.specifics.position.PositionComponent;
import internal.entity_component_system.specifics.position.PositionProcessor;
import internal.entity_component_system.specifics.render.RenderComponent;
import internal.entity_component_system.specifics.render.RenderProcessor;
import internal.entity_component_system.specifics.velocity.VelocityProcessor;
import internal.events.Event;
import internal.events.input.key.KeyEvent;
import internal.events.input.key.KeyPressedEvent;
import internal.rendering.container.A_Scene;
import internal.rendering.container.Window;
import internal.rendering.mesh.TexturedAMesh;
import internal.rendering.shader.ShaderProgram;
import internal.rendering.texture.dependencies.I_TextureLoader;
import internal.rendering.texture.dependencies.implementations.STBI_TextureLoader;
import org.joml.Vector2d;

import java.util.List;


public class PongScene extends A_Scene {


    // -+- CREATION -+- //

    public PongScene() {
        super(new Vector2d(0, 0), 1920, 1080);

        boolean result;

        RenderProcessor renderProcessor;

        renderProcessor = new RenderProcessor();

        SYSTEMS.ECS.addProcessor(new PositionProcessor());
        SYSTEMS.ECS.addProcessor(renderProcessor);
        renderProcessor.getBatchSystem().addProcessor(new TextureBatchProcessor(), false);
        SYSTEMS.ECS.addProcessor(new HitboxProcessor());
        SYSTEMS.ECS.addProcessor(new CollisionProcessor(new QuadTree(new Vector2d(0, 0), 1920, 1080), new AABB_Calculator()));
        SYSTEMS.ECS.addProcessor(new VelocityProcessor());
    }

    @Override
    protected I_TextureLoader p_getTextureLoaderImplementation() {
        return new STBI_TextureLoader();
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    Paddle paddle;

    Ball ball;


    // -+- SCENE MANAGEMENT -+- //

    @Override
    protected void p_onActivation() {

    }
    @Override
    protected void p_onDeactivation() {

    }

    @Override
    protected void p_onAdded(Window window) {
        paddle = new Paddle(SYSTEMS.ECS, SYSTEMS.window_port);

        ball =  new Ball(SYSTEMS.ECS, SYSTEMS.EVENT_HANDLER);
    }
    @Override
    protected void p_onRemoved(Window window) {

    }


    // -+- UPDATE LOOP -+- //

    @Override
    public void update(double deltaTime) {
        SYSTEMS.ECS.update();

        paddle.update();
    }


}