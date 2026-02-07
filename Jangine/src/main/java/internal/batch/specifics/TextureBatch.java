package internal.batch.specifics;


import internal.batch.A_Batch;
import internal.rendering.camera.Camera2D;
import internal.rendering.mesh.TexturedAMesh;
import internal.rendering.shader.ShaderProgram;
import internal.rendering.texture.Texture;
import internal.rendering.texture.TextureManager;
import internal.rendering.texture.dependencies.implementations.STBI_TextureLoader;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;


public class TextureBatch extends A_Batch<TexturedAMesh> {


    public TextureBatch(ShaderProgram shader, int verticesAmount, int vertexSize, int indicesAmount) {
        super(shader, verticesAmount, vertexSize, indicesAmount);

        _LOADER = new TextureManager(new STBI_TextureLoader());

        _TEXTURES = new Texture[8];
        _PLACEHOLDER = _LOADER.load("assets/placeholder_texture.png");
    }


    private final TextureManager _LOADER;

    private final Texture[] _TEXTURES;
    private final Texture _PLACEHOLDER;


    @Override
    protected void p_genVertexAttribPointers() {
        /* Coordinates */ glVertexAttribPointer(0, 2, GL_FLOAT, false, 5 * Float.BYTES, 0);
        /* UV-Coordinates */ glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 2 * Float.BYTES);
        /* Texture Id */ glVertexAttribPointer(2, 1, GL_FLOAT, false, 5 * Float.BYTES, 4 * Float.BYTES);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }


    @Override
    protected void p_onMeshAdded(TexturedAMesh mesh) {
        int textureIndex;

        textureIndex = h_getTextureIndex(_LOADER.load(mesh.texturePath));
        if (textureIndex == -1) throw new RuntimeException("Too many textures in this batch!");

        for (int index = 4; index < mesh.vertices.length; index += 5) {
            mesh.vertices[index] = textureIndex;
        }
    }
    @Override
    protected void p_onMeshRemoved(TexturedAMesh mesh) {

    }

    @Override
    protected void p_onMeshUpdated(TexturedAMesh mesh) {
        int textureIndex;

        textureIndex = h_getTextureIndex(_LOADER.load(mesh.texturePath));
        if (textureIndex == -1) throw new RuntimeException("Too many textures in this batch!");

        for (int index = 4; index < mesh.vertices.length; index += 5) {
            mesh.vertices[index] = textureIndex;
        }
    }

    @Override
    protected void p_onFlush() {
        Arrays.fill(_TEXTURES, null);
    }

    @Override
    protected void p_prepareRendering() {
        getActiveShader().upload("uTextures", new int[] {0,1,2,3,4,5,6,7});

        for (int index = 0; index < 8; index++) {
            glActiveTexture(GL_TEXTURE0 + index);

            if (_TEXTURES[index] == null) {
                _PLACEHOLDER.bind();
            } else {
                _TEXTURES[index].bind();
            }
        }

        getActiveShader().upload("uTextures", new int[] {0,1,2,3,4,5,6,7});

        Camera2D camera;

        camera = new Camera2D(41, 41);

        getActiveShader().upload("uProjectionMatrix", camera.getProjectionMatrix());
        getActiveShader().upload("uViewMatrix", camera.getViewMatrix());
    }

    private int  h_getTextureIndex(Texture texture) {
        for (int index = 0; index < 8; index++) {
            if (_TEXTURES[index] == texture) return index;
        }
        for (int index = 0; index < 8; index++) {
            if (_TEXTURES[index] == null) {
                _TEXTURES[index] = texture;

                return index;
            }
        }

        return -1;
    }


}