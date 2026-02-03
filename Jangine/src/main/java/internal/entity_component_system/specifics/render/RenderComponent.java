package internal.entity_component_system.specifics.render;


import internal.entity_component_system.A_Component;
import internal.rendering.mesh.A_Mesh;
import internal.rendering.shader.ShaderProgram;


public class RenderComponent extends A_Component {


    // -+- CREATION -+- //

    public RenderComponent(boolean positionDependent, A_Mesh renderMesh, ShaderProgram shader) {
        super();

        this.positionDependent = positionDependent;

        this.renderMesh = renderMesh;
        this.shader = shader;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    public boolean positionDependent;

    public A_Mesh renderMesh;
    public ShaderProgram shader;


}