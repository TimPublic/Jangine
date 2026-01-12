package internal.ecs.rendering;


import internal.ecs.JangineECS_Component;
import internal.rendering.JangineMesh;
import internal.rendering.texture.JangineTexture;


public class JECS_RenderComponent extends JangineECS_Component {


    public JECS_RenderComponent(JangineMesh mesh, JangineTexture texture) {
        active = true;

        this.mesh = mesh;
        this.texture = texture;
    }


    public boolean active;

    public JangineMesh mesh;
    public JangineTexture texture;


}