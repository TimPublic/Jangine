package internal.ecs.specific.rendering.color;


import internal.ecs.ECS_Component;
import internal.rendering.mesh.ColoredAMesh;


public class ColoredMeshComponent extends ECS_Component {


    public ColoredMeshComponent(ColoredAMesh mesh) {
        this.mesh = mesh;
    }


    public ColoredAMesh mesh;


}