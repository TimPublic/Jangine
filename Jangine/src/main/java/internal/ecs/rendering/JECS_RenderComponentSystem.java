package internal.ecs.rendering;


import internal.ecs.JangineECS_Component;
import internal.ecs.JangineECS_ComponentSystem;
import internal.rendering.JangineCamera2D;
import internal.rendering.JangineMesh;
import internal.rendering.JangineRenderBatch;
import internal.rendering.JangineShaderProgram;
import internal.rendering.texture.JangineTexture;

import java.util.HashMap;


public class JECS_RenderComponentSystem<T extends JangineECS_Component> extends JangineECS_ComponentSystem<T> {


    private final JangineShaderProgram _shaderProgram;

    private final HashMap<JangineTexture, JangineRenderBatch> _batches;


    public JECS_RenderComponentSystem(JangineShaderProgram shaderProgram) {
        _shaderProgram = shaderProgram;

        _batches = new HashMap<>();
    }


    @Override
    public void update(double deltaTime) {
        _buildBatches();
        _renderBatches();
    }


    private void _buildBatches() {
        for (JangineRenderBatch batch : _batches.values()) {
            batch.flush();
        }

        for (T component : _components) {
            JangineTexture componentTexture;
            JangineMesh componentMesh;

            if (!((JECS_RenderComponent) component).active) {
                continue;
            }

            componentTexture = ((JECS_RenderComponent) component).texture;
            componentMesh = ((JECS_RenderComponent) component).mesh;

            if (!_batches.containsKey(componentTexture)) {
                _batches.put(componentTexture, new JangineRenderBatch(_shaderProgram, componentTexture, new JangineCamera2D(41, 41)));
            }

            _batches.get(componentTexture).addMesh(componentMesh);
        }
    }
    private void _renderBatches() {
        for (JangineRenderBatch batch : _batches.values()) {
            batch.render();
        }
    }


}