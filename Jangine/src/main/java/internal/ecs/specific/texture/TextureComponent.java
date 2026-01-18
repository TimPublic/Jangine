package internal.ecs.specific.texture;


import internal.ecs.ECS_Component;
import internal.rendering.texture.Texture;
import internal.rendering.texture.dependencies.implementations.STBI_TextureLoader;


public class TextureComponent extends ECS_Component {


    public TextureComponent(Texture texture) {
        this.texture = texture;
    }
    public TextureComponent(String texturePath) {
        this.texture = new Texture(texturePath, new STBI_TextureLoader());
    }


    public Texture texture;


}