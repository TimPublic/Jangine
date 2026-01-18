package internal.ecs.specific.rendering.texture;


import internal.ecs.ECS_Component;
import internal.rendering.mesh.TexturedMesh;


public class TexturedMeshComponent extends ECS_Component {


    public TexturedMeshComponent(TexturedMesh mesh) {
        this.mesh = mesh;
    }


    public TexturedMesh mesh;


}