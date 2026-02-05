package internal.rendering.texture;


import internal.rendering.texture.dependencies.I_TextureLoader;
import internal.util.interfaces.I_Loader;

import java.util.HashMap;


public class TextureLoader implements I_Loader<Texture> {


    // -+- CREATION -+- //

    public TextureLoader(I_TextureLoader loader) {
        _LOADER = loader;

        _TEXTURES = new HashMap<>();
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private I_TextureLoader _LOADER;

    private HashMap<String, Texture> _TEXTURES;


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
    public boolean add(String path, boolean overwrite) {
        Texture texture;

        if (_TEXTURES.containsKey(path) && !overwrite) return false;

        texture = new Texture(path, _LOADER);

        _TEXTURES.put(path, texture);

        return true;
    }
    @Override
    public boolean add(Texture object, boolean overwrite) {
        if (object == null) throw new IllegalArgumentException("[TEXTURE LOADER ERROR] : Texture can not be null!");

        String path;
        Texture texture;

        path = object.getPath();
        texture = _TEXTURES.get(path);

        if (texture == null) {
            _TEXTURES.put(path, object);

            return true;
        }
        if (texture == object) return true;

        if (!overwrite) return false;

        _TEXTURES.put(path, object);

        return true;
    }


    // -+- REMOVAL -+- //

    @Override
    public boolean rmv(String path) {
        return _TEXTURES.remove(path) != null;
    }
    @Override
    public boolean rmv(Texture object) {
        if (object == null) throw new IllegalArgumentException("[TEXTURE LOADER ERROR] : Texture can not be null!");

        String path;

        path = object.getPath();

        return _TEXTURES.remove(path) == object;
    }


}