package internal.rendering.batch;


import internal.rendering.camera.Camera2D;
import internal.rendering.shader.ShaderProgram;
import internal.rendering.mesh.TexturedMesh;
import internal.rendering.texture.JangineTexture;
import internal.rendering.texture.dependencies.implementations.STBI_TextureLoader;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


/**
 * This class is a way to pack together meshes to be rendered with a {@link JangineTexture}, using the same {@link ShaderProgram}.
 * This batch can work with up to eight different textures.
 * You have to provide the texture you want to use when providing the {@link TexturedMesh}, where this batch will then
 * automatically set the correct texture index in the mesh directly, meaning, you should not provide two batches
 * with the same mesh.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class TexturedRenderBatch extends RenderBatch {


    public static final int MAX_TEXTURE_AMOUNT = 8;
    public static final int VERTEX_SIZE = 5;


    public static final String PLACEHOLDER_TEXTURE_PATH = "assets/placeholder_texture.png";


    private final JangineTexture[] _textures;
    private final JangineTexture _placeHolderTexture;

    private final HashMap<TexturedMesh, JangineTexture> _activeMeshesAndTextures;


    // -+- CREATION -+- //

    public TexturedRenderBatch(String shaderPath, Camera2D camera) {
        super(shaderPath, camera);

        _textures = new JangineTexture[MAX_TEXTURE_AMOUNT];
        _placeHolderTexture = new JangineTexture(PLACEHOLDER_TEXTURE_PATH, new STBI_TextureLoader());

        _activeMeshesAndTextures = new HashMap<>();
    }
    public TexturedRenderBatch(ShaderProgram shaderProgram, Camera2D camera) {
        super(shaderProgram, camera);

        _textures = new JangineTexture[MAX_TEXTURE_AMOUNT];
        _placeHolderTexture = new JangineTexture(PLACEHOLDER_TEXTURE_PATH, new STBI_TextureLoader());

        _activeMeshesAndTextures = new HashMap<>();
    }

    /**
     * Generates the Vertex Array Object,
     * with both Vertex Attribute Pointers.
     *
     * @return id of the Vertex Array Object
     *
     * @author Tim Kloepper
     */
    @Override
    protected int _genVertexArrayObject() {
        int id;

        id = glGenVertexArrays();
        glBindVertexArray(id);

        return id;
    }

    @Override
    protected void _genVertexAttribPointers() {
        glBindVertexArray(_vaoID);
        glBindBuffer(GL_ARRAY_BUFFER, _vboID);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 2 * Float.BYTES);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 4 * Float.BYTES);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }
    // -+- TEXTURES -+- //

    /**
     * Adds a new {@link JangineTexture} to the batch to be used by meshes.
     * Returns -1 if no index was free and the function should not overwrite.
     *
     * @param texture texture to be added
     * @param overwrite if the first texture should be overwritten if all indices are taken
     * @return the assigned index
     *
     * @author Tim Kloepper
     */
    public int registerTexture(JangineTexture texture, boolean overwrite) {
        for (int index = 0; index < MAX_TEXTURE_AMOUNT; index++) {
            if (_textures[index] == texture) {return index;}
        }
        for (int index = 0; index < MAX_TEXTURE_AMOUNT; index++) {
            if (_textures[index] != null) {continue;}

            _textures[index] = texture;

            return index;
        }

        if (!overwrite) {return -1;}

        _textures[0] = texture;

        return 0;
    }
    /**
     * Removes the {@link JangineTexture} from the provided index.
     *
     * @param index index of the texture that should be removed
     * @return success
     *
     * @author Tim Kloepper
     */
    public boolean removeTexture(int index) {
        if (_textures[index] == null) {return false;}

        _textures[index] = null;

        return true;
    }
    /**
     * Removes the specified {@link JangineTexture} from the batch.
     *
     * @param texture texture to be removed
     * @return success
     *
     * @author Tim Kloepper
     */
    public boolean removeTexture(JangineTexture texture) {
        for (int index = 0; index < MAX_TEXTURE_AMOUNT; index++) {
            if (_textures[index] != texture) {continue;}

            _textures[index] = null;

            return true;
        }

        return false;
    }


    // -+- MESHES -+- //

    /**
     * Adds a mesh to the batch.
     * You also need to provide a {@link JangineTexture} that this mesh
     * should be rendered with.
     * If the texture is not already in the batch, the batch tries to add it,
     * if this fails, false is returned.
     * If the mesh is already in the batch, the mesh will be updated by
     * calling {@link TexturedRenderBatch#updateMesh(TexturedMesh, JangineTexture)},
     * which will cause a {@link TexturedRenderBatch#_rebuild()} call.
     *
     * @param mesh mesh to be added
     * @param texture texture the mesh is to be rendered with
     * @return success
     *
     * @author Tim Kloepper
     */
    public boolean addMesh(TexturedMesh mesh, JangineTexture texture) {
        FloatBuffer subVertexBuffer;
        IntBuffer subIndexBuffer;

        int textureIndex;

        if (_activeMeshesAndTextures.containsKey(mesh)) {updateMesh(mesh, texture); return true;}

        textureIndex = registerTexture(texture, false);
        if (textureIndex == -1) {return false;}

        for (int index = 4; index < mesh.vertices.length; index += VERTEX_SIZE) {
            mesh.vertices[index] = textureIndex;
        }

        subVertexBuffer = BufferUtils.createFloatBuffer(mesh.vertices.length);
        subIndexBuffer = BufferUtils.createIntBuffer(mesh.indices.length);

        _activeMeshesAndTextures.put(mesh, texture);

        // Add indices, relative to existing meshes.
        for (int index : mesh.indices) {
            subIndexBuffer.put(index + _vertexPointer / VERTEX_SIZE);
        }
        subIndexBuffer.flip();

        // Add vertices.
        subVertexBuffer.put(mesh.vertices);
        subVertexBuffer.flip();

        // Insert indices into the element buffer object.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _eboID);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, (long) _indexPointer * Integer.BYTES, subIndexBuffer);

        // Insert vertices into the vertex buffer object.
        glBindBuffer(GL_ARRAY_BUFFER, _vboID);
        glBufferSubData(GL_ARRAY_BUFFER, (long) _vertexPointer * Float.BYTES, subVertexBuffer);

        _indexPointer += mesh.indices.length;
        _vertexPointer += mesh.vertices.length;

        return true;
    }
    public void updateMesh(TexturedMesh mesh, JangineTexture texture) {
        if (!_activeMeshesAndTextures.containsKey(mesh)) {return;}

        _rebuiltRequired = true;

        _activeMeshesAndTextures.put(mesh, texture);
    }
    /**
     * Removes a mesh from the batch.
     * This action will result in a rebuilt on the next {@link TexturedRenderBatch#update()} call,
     * which can be expensive if many meshes are inside this batch.
     *
     * @param mesh mesh to be removed
     *
     * @author Tim Kloepper
     */
    public void rmvMesh(TexturedMesh mesh) {
        if (!_activeMeshesAndTextures.containsKey(mesh)) {return;}

        _activeMeshesAndTextures.remove(mesh);

        _rebuiltRequired = true;
    }


    // -+- BATCH MANAGEMENT -+- //

    /**
     * Rebuilds the buffers by clearing them and then
     * adding all active meshes back in.
     *
     * @author Tim Kloepper
     */
    @Override
    protected void _rebuild() {
        HashMap<TexturedMesh, JangineTexture> meshes;

        meshes = new HashMap<>(_activeMeshesAndTextures);

        flush();

        for (TexturedMesh mesh : meshes.keySet()) {
            addMesh(mesh, meshes.get(mesh));
        }

        _rebuiltRequired = false;
    }
    @Override
    protected void _clearMeshHolders() {
        _activeMeshesAndTextures.clear();
    }


    // -+- UPDATE LOOP -+- //


    // -+- RENDER -+- //

    /**
     * Renders all meshes in the batch.
     *
     * @author Tim Kloepper
     */
    @Override
    public void render() {
        glBindVertexArray(_vaoID);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _eboID);

        _shaderProgram.use();
        _shaderProgram.upload("uTextures", new int[] {0,1,2,3,4,5,6,7});

        for (int index = 0; index < MAX_TEXTURE_AMOUNT; index++) {
            glActiveTexture(GL_TEXTURE0 + index);

            if (_textures[index] == null) {
                _placeHolderTexture.bind();
            } else {
                _textures[index].bind();
            }
        }

        _shaderProgram.upload("uProjectionMatrix", _camera.getProjectionMatrix());
        _shaderProgram.upload("uViewMatrix", _camera.getViewMatrix());

        glDrawElements(GL_TRIANGLES, _indexPointer, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        _shaderProgram.unuse();
    }


    // -+- GETTERS -+- //

    /**
     * Returns all textures, used by this batch.
     *
     * @return all used textures
     *
     * @author Tim Kloepper
     */
    public JangineTexture[] getTextures() {
        return _textures;
    }

    /**
     * Returns the texture at the provided index.
     * If the index is out of bounds or no texture is assigned
     * to this index, null is returned.
     *
     * @param index index of the searched texture
     *
     * @return texture assigned to the specified index
     *
     * @author Tim Kloepper
     */
    public JangineTexture getTextureAt(int index) {
        if (index >= _textures.length) {return null;}

        return _textures[index];
    }
    /**
     * Returns the index of the specified texture.
     * If the texture is not registered in this batch,
     * -1 is returned.
     *
     * @param texture texture that is searched for
     *
     * @return index of the searched for texture
     *
     * @author Tim Kloepper
     */
    public int getIndexOfTexture(JangineTexture texture) {
        for (int index = 0; index < _textures.length; index++) {
            if (_textures[index] == texture) {return index;}
        }

        return -1;
    }


}