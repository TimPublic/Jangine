package internal.ecs.specific.rendering;


import internal.ecs.ECS_Component;


public class RenderComponent extends ECS_Component {


    public RenderComponent(RENDER_TYPE renderType) {
        this.renderType = renderType;
    }


    public enum RENDER_TYPE {
        COLOR,
        TEXTURE,
    }


    public RENDER_TYPE renderType;


}