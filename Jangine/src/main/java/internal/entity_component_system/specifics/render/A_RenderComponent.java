package internal.entity_component_system.specifics.render;


import internal.entity_component_system.A_Component;
import internal.rendering.mesh.A_Mesh;


public class A_RenderComponent extends A_Component {


    // -+- CREATION -+- //

    public A_RenderComponent(boolean positionDependent, A_Mesh mesh) {
        super();

        this.isPositionDependent = positionDependent;
        this.mesh = mesh;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    public boolean isPositionDependent;
    public A_Mesh mesh;


}