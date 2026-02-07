package internal.rendering.shader;


import internal.util.interfaces.I_Loader;

import java.util.HashMap;


public class ShaderManager implements I_Loader<ShaderProgram> {


    // -+- CREATION -+- //

    public ShaderManager() {
        _SHADERS = new HashMap<>();
    }


    // -+- PARAMETERS -+- //

    // FINAL //

    private final HashMap<String, ShaderProgram> _SHADERS;


    // -+- LOADING -+- //

    public ShaderProgram load(String path) {
        ShaderProgram shader;

        shader = _SHADERS.get(path);
        if (shader == null) {
            shader = new ShaderProgram(path);
            _SHADERS.put(path, shader);
        }

        return shader;
    }


    // -+- ADDITION AND REMOVAL -+- //

    public boolean add(String path) {
        load(path);

        return true;
    }
    public boolean add(ShaderProgram shader) {
        String path;
        ShaderProgram foundShader;

        path = shader.getPath();
        foundShader = _SHADERS.get(path);

        if (foundShader == null) {
            _SHADERS.put(path, shader);

            return true;
        }

        return foundShader == shader;
    }

    public boolean rmv(String path) {
        return _SHADERS.remove(path) != null;
    }
    public boolean rmv(ShaderProgram shader) {
        return _SHADERS.remove(shader.getPath(), shader);
    }


    // -+- DATA MANAGEMENT -+- //

    public void clear() {
        _SHADERS.clear();
    }


}