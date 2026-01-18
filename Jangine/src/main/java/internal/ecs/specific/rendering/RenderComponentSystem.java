package internal.ecs.specific.rendering;


import internal.ecs.ECS;
import internal.ecs.ECS_ComponentSystem;
import internal.ecs.specific.rendering.color.ColoredMeshComponent;
import internal.ecs.specific.rendering.color.ColoredMeshComponentSystem;
import internal.ecs.specific.rendering.texture.TexturedMeshComponent;
import internal.ecs.specific.rendering.texture.TexturedMeshComponentSystem;
import internal.ecs.specific.texture.TextureComponent;
import internal.ecs.specific.texture.TextureComponentSystem;
import internal.rendering.batch.ColoredRenderBatch;
import internal.rendering.batch.TexturedRenderBatch;
import internal.rendering.camera.Camera2D;


public class RenderComponentSystem<T extends RenderComponent> extends ECS_ComponentSystem<RenderComponent> {


    private final ColoredRenderBatch _coloredRenderBatch;
    private final TexturedRenderBatch _texturedRenderBatch;


    public RenderComponentSystem() {
        _coloredRenderBatch = new ColoredRenderBatch("assets/default.glsl", new Camera2D(41, 41));
        _texturedRenderBatch = new TexturedRenderBatch("assets/default.glsl", new Camera2D(41, 41));
    }


    @Override
    public void update(ECS system) {
        _updateColored(system);
        _updateTextured(system);

        _coloredRenderBatch.update();
        _texturedRenderBatch.update();
    }


    private void _updateColored(ECS system) {
        ECS_ComponentSystem<?> componentSystem;

        componentSystem = system.getComponentSystem(ColoredMeshComponent.class);
        if (componentSystem == null) {return;}
        if (!(componentSystem instanceof ColoredMeshComponentSystem<?>)) {return;}

        ColoredMeshComponentSystem<?> coloredMeshSystem;

        coloredMeshSystem = (ColoredMeshComponentSystem<?>) componentSystem;

        for (RenderComponent component : _components.values()) {
            if (component.renderType != RenderComponent.RENDER_TYPE.COLOR) {continue;}

            ColoredMeshComponent coloredMeshComponent;

            coloredMeshComponent = coloredMeshSystem.getComponent(component.owningEntity);
            if (coloredMeshComponent == null) {continue;}

            _coloredRenderBatch.addMesh(coloredMeshComponent.mesh);
        }
    }
    private void _updateTextured(ECS system) {
        ECS_ComponentSystem<?> componentSystem;

        componentSystem = system.getComponentSystem(TexturedMeshComponent.class);
        if (componentSystem == null) {return;}
        if (!(componentSystem instanceof TexturedMeshComponentSystem<?>)) {return;}

        TexturedMeshComponentSystem<?> texturedMeshSystem;

        texturedMeshSystem = (TexturedMeshComponentSystem<?>) componentSystem;

        componentSystem = system.getComponentSystem(TextureComponent.class);
        if (componentSystem == null) {return;}
        if (!(componentSystem instanceof TextureComponentSystem<?>)) {return;}

        TextureComponentSystem<?> textureSystem;

        textureSystem = (TextureComponentSystem<?>) componentSystem;

        for (RenderComponent component : _components.values()) {
            if (component.renderType != RenderComponent.RENDER_TYPE.TEXTURE) {continue;}

            TexturedMeshComponent texturedMeshComponent;

            texturedMeshComponent = texturedMeshSystem.getComponent(component.owningEntity);
            if (texturedMeshComponent == null) {continue;}

            TextureComponent textureComponent;
            textureComponent = textureSystem.getComponent(component.owningEntity);
            if (textureComponent == null) {continue;}

            _texturedRenderBatch.addMesh(texturedMeshComponent.mesh, textureComponent.texture);
        }
    }


}