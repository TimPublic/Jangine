package internal.ecs.specific.rendering;


import internal.ecs.ECS;
import internal.ecs.ECS_Component;
import internal.ecs.A_ComponentSystem;
import internal.ecs.specific.rendering.color.ColoredMeshComponent;
import internal.ecs.specific.rendering.color.ColoredMeshComponentSystem;
import internal.ecs.specific.rendering.texture.TexturedMeshComponent;
import internal.ecs.specific.rendering.texture.TexturedMeshComponentSystem;
import internal.ecs.specific.texture.TextureComponent;
import internal.ecs.specific.texture.TextureComponentSystem;
import internal.rendering.batch.ColoredRenderBatch;
import internal.rendering.batch.TexturedRenderBatch;
import internal.rendering.camera.Camera2D;

import java.util.Collection;
import java.util.List;


public class RenderComponentSystem<T extends RenderComponent> extends A_ComponentSystem<RenderComponent> {


    private final ColoredRenderBatch _coloredRenderBatch;
    private final TexturedRenderBatch _texturedRenderBatch;

    private TexturedMeshComponentSystem<?> _texturedMeshSystem;
    private ColoredMeshComponentSystem<?> _coloredSystem;
    private TextureComponentSystem<?> _textureSystem;


    public RenderComponentSystem() {
        _coloredRenderBatch = new ColoredRenderBatch("assets/default.glsl", new Camera2D(41, 41));
        _texturedRenderBatch = new TexturedRenderBatch("assets/default.glsl", new Camera2D(41, 41));
    }


    @Override
    protected void p_internalUpdate(ECS system) {
        for (RenderComponent component : p_components.values()) {
            switch (component.renderType) {
                case TEXTURE:
                    if (_texturedMeshSystem == null) {continue;}

                    TexturedMeshComponent texturedMeshComponent;
                    texturedMeshComponent = _texturedMeshSystem.getComponent(component.owningEntity);
                    if (texturedMeshComponent == null) {continue;}

                    TextureComponent textureComponent;
                    textureComponent = _textureSystem.getComponent(component.owningEntity);
                    if (texturedMeshComponent == null) {continue;}

                    _texturedRenderBatch.addMesh(texturedMeshComponent.mesh, textureComponent.texture);

                    break;
                case COLOR:
                    if (_coloredSystem == null) {continue;}

                    ColoredMeshComponent coloredMeshComponent;
                    coloredMeshComponent = _coloredSystem.getComponent(component.owningEntity);
                    if (coloredMeshComponent == null) {continue;}

                    _coloredRenderBatch.addMesh(coloredMeshComponent.mesh);

                    break;
            }
        }

        _coloredRenderBatch.update();
        _texturedRenderBatch.update();

    }


    @Override
    protected boolean p_isComponentValid(RenderComponent component) {
        return true;
    }


    // -+- CALLBACKS -+- //

    @Override
    public void onComponentSystemAdded(A_ComponentSystem componentSystem) {
        if (componentSystem == null) {return;}

        if (componentSystem instanceof TexturedMeshComponentSystem<?>) {
            _texturedMeshSystem = (TexturedMeshComponentSystem<?>) componentSystem;

            return;
        }
        if (componentSystem instanceof ColoredMeshComponentSystem<?>) {
            _coloredSystem = (ColoredMeshComponentSystem<?>) componentSystem;

            return;
        }
        if (componentSystem instanceof TextureComponentSystem<?>) {
            _textureSystem = (TextureComponentSystem<?>) componentSystem;

            return;
        }
    }
    @Override
    public void onComponentSystemRemoved(A_ComponentSystem componentSystem) {
        if (componentSystem == null) {return;}

        if (componentSystem instanceof TexturedMeshComponentSystem<?>) {
            _texturedMeshSystem = null;

            return;
        }
        if (componentSystem instanceof ColoredMeshComponentSystem<?>) {
            _coloredSystem = null;

            return;
        }
        if (componentSystem instanceof TextureComponentSystem<?>) {
            _textureSystem = null;

            return;
        }
    }


    // -+- GETTERS -+- //

    @Override
    public Collection<Class<? extends ECS_Component>> getRequirements() {
        return List.of(ColoredMeshComponent.class, TexturedMeshComponent.class); // NOT REALLY.
    }


}