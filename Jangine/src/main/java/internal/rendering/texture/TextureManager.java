package internal.rendering.texture;


import internal.rendering.texture.dependencies.I_TextureLoader;
import internal.util.interfaces.I_Loader;

import java.util.HashMap;


public class TextureManager implements I_Loader<Texture> {


    // -+- CREATION -+- //

    public TextureManager(I_TextureLoader loader) {
        _TEXTURES = new HashMap<>();

        _LOADER = loader;
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final HashMap<String, Texture> _TEXTURES;

    // NON-FINALS //

    private I_TextureLoader _LOADER;


    // -+- LOADING -+- //

    @Override
    public Texture load(String path) {
        Texture texture;

        texture = _TEXTURES.get(path);
        if (texture == null) texture = new Texture(path, _LOADER);

        return texture;
    }


    // -+- ADDITION -+- //

    @Override
    public boolean add(String path) {
        load(path);

        return true;
    }
    @Override
    public boolean add(Texture object) {
        String path;
        Texture foundTexture;

        path = object.getPath();
        foundTexture = _TEXTURES.get(path);

        if (foundTexture == null) {
            _TEXTURES.put(path, object);

            return true;
        }

        return foundTexture == object;
    }


    // -+- REMOVAL -+- //

    @Override
    public boolean rmv(String path) {
        return _TEXTURES.remove(path) != null;
    }
    @Override
    public boolean rmv(Texture object) {
        if (object == null) throw new IllegalArgumentException("[TEXTURE LOADER ERROR] : Texture can not be null!");

        return _TEXTURES.remove(object.getPath(), object);
    }


    // -+- DATA MANAGEMENT -+- //

    public void clear() {
        _TEXTURES.clear();
    }
    public void clear(I_TextureLoader newTextureLoader) {
        _TEXTURES.clear();

        _LOADER = newTextureLoader;
    }


}