package internal.ecs.specific.rendering.texture;


import internal.ecs.ECS_Component;
import internal.rendering.mesh.TexturedAMesh;


public class TexturedMeshComponent extends ECS_Component {


    public TexturedMeshComponent(TexturedAMesh mesh) {
        this.mesh = mesh;
    }


    public TexturedAMesh mesh;


}