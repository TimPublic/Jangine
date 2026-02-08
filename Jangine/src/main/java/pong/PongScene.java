package pong;


import internal.batch.specifics.TextureBatchProcessor;
import internal.entity_component_system.specifics.collision.CollisionProcessor;
import internal.entity_component_system.specifics.collision.dependencies.calculator.AABB_Calculator;
import internal.entity_component_system.specifics.collision.dependencies.spatial_partitioner.QuadTree;
import internal.entity_component_system.specifics.hitbox.HitboxProcessor;
import internal.entity_component_system.specifics.position.PositionProcessor;
import internal.entity_component_system.specifics.render.RenderProcessor;
import internal.entity_component_system.specifics.velocity.VelocityProcessor;
import internal.rendering.container.A_Scene;
import internal.rendering.container.Window;
import internal.rendering.texture.dependencies.I_TextureLoader;
import internal.rendering.texture.dependencies.implementations.STBI_TextureLoader;
import org.joml.Vector2d;


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

    Ball ball, secondBall, thirdBall, fourthBall;


    // -+- SCENE MANAGEMENT -+- //

    @Override
    protected void p_onActivation() {

    }
    @Override
    protected void p_onDeactivation() {

    }

    @Override
    protected void p_onAdded(Window window) {
        paddle = new Paddle(this, 400, 100, "assets/placeholder_texture.png");

        ball =  new Ball(this, 100, 100, 101, 101, "assets/ui.png");
        secondBall =  new Ball(this, 100, 100, 300, 101, "assets/ui.png");
        thirdBall =  new Ball(this, 100, 100, 500, 102, "assets/ui.png");
        fourthBall =  new Ball(this, 100, 100, 700, 204, "assets/ui.png");

        BallCatcher catcher;

        catcher = new BallCatcher(this, 1920, 1, 0, 0);
    }
    @Override
    protected void p_onRemoved(Window window) {

    }


    // -+- UPDATE LOOP -+- //

    @Override
    public void update(double deltaTime) {
        SYSTEMS.ECS.update();

        paddle.update(0.4);

        ball.update(0.4);
        secondBall.update(0.4);
        thirdBall.update(0.4);
        fourthBall.update(0.4);
    }


}