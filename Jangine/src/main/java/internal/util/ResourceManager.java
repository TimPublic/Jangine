package internal.util;


import internal.rendering.shader.ShaderProgram;
import internal.rendering.texture.Texture;
import internal.rendering.texture.dependencies.I_TextureLoader;

import java.util.HashMap;


public class ResourceManager {


    // -+- CREATION -+- //

    public ResourceManager() {
        _TEXTURE_PATH_CACHE = new HashMap<>();
        _SHADER_PATH_CACHE = new HashMap<>();
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    private I_TextureLoader _textureLoader;

    // FINALS //

    private final HashMap<String, Texture> _TEXTURE_PATH_CACHE;
    private final HashMap<String, ShaderProgram> _SHADER_PATH_CACHE;


    // -+- TEXTURE LOADING AND CREATING -+- //

    public Texture loadTexture(String texturePath) {
        Texture texture;

        texture = _TEXTURE_PATH_CACHE.get(texturePath);
        if (texture != null) return texture;

        texture = new Texture(texturePath, _textureLoader);

        _TEXTURE_PATH_CACHE.put(texturePath, texture);

        return texture;
    }
    public void addTexture(Texture texture) {
        if (_TEXTURE_PATH_CACHE.containsValue(texture)) return;

        _TEXTURE_PATH_CACHE.put(texture.getPath(), texture);
    }


    // -+- SHADER LOADING AND CREATING -+- //

    public ShaderProgram loadShader(String shaderPath) {
        ShaderProgram shader;

        shader = _SHADER_PATH_CACHE.get(shaderPath);
        if (shader != null) return shader;

        shader = new ShaderProgram(shaderPath);

        _SHADER_PATH_CACHE.put(shaderPath, shader);

        return shader;
    }
    public void addShader(ShaderProgram shader) {
        if (_SHADER_PATH_CACHE.containsValue(shader)) return;

        _SHADER_PATH_CACHE.put(shader.getPath(), shader);
    }


}