package internal.ecs.quick_test.render;


import internal.ecs.quick_test.JangineECS;
import internal.ecs.quick_test.JangineECS_Component;
import internal.ecs.quick_test.JangineECS_ComponentSystem;
import internal.rendering.JangineCamera2D;
import internal.rendering.JangineRenderBatch;
import internal.rendering.JangineShaderProgram;
import internal.rendering.texture.JangineTexture;
import internal.rendering.texture.dependencies.implementations.STBI_TextureLoader;


public class JangineECS_RenderSystem extends JangineECS_ComponentSystem {


    private final JangineRenderBatch _batch;


    public JangineECS_RenderSystem(JangineShaderProgram shaderProgram) {
        _batch = new JangineRenderBatch(shaderProgram, new JangineTexture("assets/test_image.png", new STBI_TextureLoader()), new JangineCamera2D(41, 41));
    }


    @Override
    public void update() {
        _batch.flush();
    }


    @Override
    protected void _onUpdateComponent(JangineECS_Component component) {
        _batch.addMesh(((JangineECS_RenderComponent) component).mesh);
    }
    @Override
    protected void _onAllComponentsUpdated() {
        _batch.render();
    }


}