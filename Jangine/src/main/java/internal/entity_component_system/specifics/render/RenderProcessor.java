package internal.entity_component_system.specifics.render;


import internal.entity_component_system.A_Component;
import internal.entity_component_system.A_Processor;
import internal.entity_component_system.System;
import internal.entity_component_system.specifics.position.PositionComponent;
import internal.entity_component_system.specifics.position.PositionProcessor;
import internal.rendering.batch.ColoredRenderBatch;
import internal.rendering.batch.TexturedRenderBatch;
import internal.rendering.camera.Camera2D;
import internal.rendering.container.Scene;
import internal.rendering.mesh.A_Mesh;
import internal.rendering.mesh.ColoredAMesh;
import internal.rendering.mesh.TexturedAMesh;
import org.joml.Vector2d;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class RenderProcessor extends A_Processor<A_RenderComponent> {


    // -+- CREATION -+- //

    @Override
    protected void p_init(System system, Scene scene) {
        _texturedRenderBatch = new TexturedRenderBatch("assets/default.glsl", new Camera2D(41, 41));
        _coloredRenderBatch = new ColoredRenderBatch("assets/default.glsl", new Camera2D(41, 41));
    }
    @Override
    protected void p_kill(System system, Scene scene) {

    }


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
            Vector2d move;

            move = new Vector2d(to).sub(origin);

            mesh.vertices[index - 1] += (float) move.x;
            mesh.vertices[index] += (float) move.y;
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

    @Override
    protected void p_onComponentAdded(A_RenderComponent component) {

    }
    @Override
    protected void p_onComponentRemoved(A_RenderComponent component) {
        if (component.mesh instanceof TexturedAMesh) {
            _texturedRenderBatch.rmvMesh((TexturedAMesh) component.mesh);

            return;
        }
        if (component.mesh instanceof ColoredAMesh) {
            _coloredRenderBatch.rmvMesh((ColoredAMesh) component.mesh);

            return;
        }
    }
    @Override
    protected void p_onComponentActivated(A_RenderComponent component) {
        // Is handled by the rendering updates.
    }
    @Override
    protected void p_onComponentDeactivated(A_RenderComponent component) {
        if (component.mesh instanceof TexturedAMesh) _texturedRenderBatch.rmvMesh((TexturedAMesh) component.mesh);
        else if (component.mesh instanceof ColoredAMesh) _coloredRenderBatch.rmvMesh((ColoredAMesh) component.mesh);
    }


    // -+- PROCESSOR MANAGEMENT -+- //

    @Override
    protected void p_receiveRequiredProcessors(HashMap<Class<? extends A_RenderComponent>, A_Processor<?>> requiredProcessors) {
        _positionProcessor = (PositionProcessor) requiredProcessors.get(PositionComponent.class);
    }


    // -+- GETTERS -+- //

    @Override
    protected Collection<Class<? extends A_RenderComponent>> p_getProcessedComponentClasses() {
        return List.of(A_RenderComponent.class, TexturedRenderComponent.class, ColoredRenderComponent.class);
    }
    @Override
    protected Collection<Class<? extends A_Component>> p_getRequiredComponentClasses() {
        return List.of(PositionComponent.class);
    }


}