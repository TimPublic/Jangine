package internal.ecs.specific.rendering.color;


import internal.ecs.ECS_Component;
import internal.rendering.mesh.ColoredMesh;


public class ColoredMeshComponent extends ECS_Component {


    public ColoredMeshComponent(ColoredMesh mesh) {
        this.mesh = mesh;
    }


    public ColoredMesh mesh;


}