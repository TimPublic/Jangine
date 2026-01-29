package internal.entity_component_system.specifics.render;


import internal.rendering.mesh.ColoredAMesh;


public class ColoredRenderComponent extends A_RenderComponent {


    // -+- CREATION -+- //

    public ColoredRenderComponent(boolean isPositionDependent, ColoredAMesh coloredMesh) {
        super(isPositionDependent, coloredMesh);
    }


}