package internal.entity_component_system.specifics.render;


import internal.rendering.mesh.TexturedAMesh;
import internal.rendering.texture.Texture;


public class TexturedRenderComponent extends A_RenderComponent {


    // -+- CREATION -+- //

    public TexturedRenderComponent(boolean isPositionDependent, TexturedAMesh texturedMesh, Texture texture) {
        super(isPositionDependent, texturedMesh);

        this.texture = texture;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    public Texture texture;


}