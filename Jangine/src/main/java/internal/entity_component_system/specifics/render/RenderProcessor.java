package internal.entity_component_system.specifics.render;


import internal.entity_component_system.A_Processor;
import internal.entity_component_system.System;
import internal.entity_component_system.specifics.position.PositionProcessor;
import internal.rendering.batch.ColoredRenderBatch;
import internal.rendering.batch.TexturedRenderBatch;
import internal.rendering.container.Scene;
import internal.rendering.mesh.A_Mesh;
import internal.rendering.mesh.ColoredAMesh;
import internal.rendering.mesh.TexturedAMesh;
import org.joml.Vector2d;

import java.util.Collection;


public class RenderProcessor extends A_Processor<A_RenderComponent> {


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    private PositionProcessor _positionProcessor;

    // FINALS //

    private TexturedRenderBatch _texturedRenderBatch;
    private ColoredRenderBatch _coloredRenderBatch;


    // -+- UPDATE LOOP -+- //

    @Override
    protected void p_internalUpdate(Collection<A_RenderComponent> validComponents, System system, Scene scene) {
        for (A_RenderComponent component : validComponents) {
            if (component.isPositionDependent) h_updatePosition(component.mesh, _positionProcessor.getComponent(component.owningEntity).position); // Is safe because of the valid component check.

            if (component instanceof TexturedRenderComponent) {
                h_updateTexturedComponent((TexturedRenderComponent) component);
                continue;
            }
            if (component instanceof ColoredRenderComponent) {
                h_updateColoredComponent((ColoredRenderComponent) component);
                continue;
            }
        }

        _texturedRenderBatch.update();
        _coloredRenderBatch.update();
    }

    private void h_updatePosition(A_Mesh mesh, Vector2d to) {
        // THE FIRST MESH COORDINATE NEEDS TO BE THE ORIGIN!

        Vector2d origin;

        origin = new Vector2d(mesh.vertices[0], mesh.vertices[1]);

        if (origin.x == to.x && origin.y == to.y) return;

        for (int index = 1; index < mesh.vertices.length; index += mesh.getVertexSize()) {
            mesh.vertices[index - 1] -= (float) (origin.x + to.x);
            mesh.vertices[index] -= (float) (origin.x + to.x);
        }
    }
    private void h_updateTexturedComponent(TexturedRenderComponent component) {
        _texturedRenderBatch.addMesh((TexturedAMesh) component.mesh, component.texture);
    }
    private void h_updateColoredComponent(ColoredRenderComponent component) {
        _coloredRenderBatch.addMesh((ColoredAMesh) component.mesh);
    }


    // -+- COMPONENT MANAGEMENT -+- //


    @Override
    protected boolean p_isComponentValid(A_RenderComponent component) {
        if (component.mesh == null) return false;

        if (!component.isPositionDependent) return true;

        if (_positionProcessor == null) return false;

        return _positionProcessor.getComponent(component.owningEntity) != null;
    }


}