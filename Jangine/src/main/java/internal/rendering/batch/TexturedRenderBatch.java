package internal.rendering.batch;


import internal.rendering.JangineCamera2D;
import internal.rendering.JangineShaderProgram;
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
 * This class is a way to pack together meshes to be rendered with a {@link JangineTexture}, using the same {@link JangineShaderProgram}.
 * This batch can work with up to eight different textures.
 * You have to provide the texture you want to use when providing the {@link TexturedMesh}, where this batch will then
 * automatically set the correct texture index in the mesh directly, meaning, you should not provide two batches
 * with the same mesh.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class TexturedRenderBatch {


    public static final int MAX_INDEX_AMOUNT = 444;
    public static final int MAX_VERTEX_AMOUNT = 444;
    public static final int MAX_TEXTURE_AMOUNT = 8;
    public static final int VERTEX_SIZE = 5;


    public static final String PLACEHOLDER_TEXTURE_PATH = "assets/placeholder_texture.png";


    private final JangineTexture[] _textures;
    private final JangineTexture _placeHolderTexture;
    private final JangineShaderProgram _shaderProgram;
    private final JangineCamera2D _camera;

    private final HashMap<TexturedMesh, JangineTexture> _activeMeshesAndTextures;
    private int _indexPointer;
    private int _vertexPointer;

    private boolean _rebuiltRequired;

    private int _vaoID, _eboID, _vboID;


    // -+- CREATION -+- //

    public TexturedRenderBatch(String shaderPath) {
        _textures = new JangineTexture[MAX_TEXTURE_AMOUNT];
        _placeHolderTexture = new JangineTexture(PLACEHOLDER_TEXTURE_PATH, new STBI_TextureLoader());
        _shaderProgram = new JangineShaderProgram(shaderPath);
        _camera = new JangineCamera2D(41, 41);

        _activeMeshesAndTextures = new HashMap<>();
        _indexPointer = 0;
        _vertexPointer = 0;

        _rebuiltRequired = false;

        _initObjects();
    }
    public TexturedRenderBatch(JangineShaderProgram shaderProgram) {
        _textures = new JangineTexture[MAX_TEXTURE_AMOUNT];
        _placeHolderTexture = new JangineTexture(PLACEHOLDER_TEXTURE_PATH, new STBI_TextureLoader());
        _shaderProgram = shaderProgram;
        _camera = new JangineCamera2D(41, 41);

        _activeMeshesAndTextures = new HashMap<>();
        _indexPointer = 0;
        _vertexPointer = 0;

        _rebuiltRequired = false;

        _initObjects();
    }

    /**
     * Generates all GPU objects:
     * <ul>
     *     <li>Vertex Array Object</li>
     *     <li>Element Buffer Object</li>
     *     <li>Vertex Buffer Object</li>
     * </ul>
     *
     * The ids generated, will be put in the corresponding variables.
     *
     * @author Tim Kloepper
     */
    private void _initObjects() {
        _vaoID = _genVertexArrayObject();
        _eboID = _genElementBufferObject();
        _vboID = _genVertexBufferObject();

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    /**
     * Generates the Vertex Array Object,
     * with both Vertex Attribute Pointers.
     *
     * @return id of the Vertex Array Object
     *
     * @author Tim Kloepper
     */
    private int _genVertexArrayObject() {
        int id;

        id = glGenVertexArrays();

        glBindVertexArray(id);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 2 * Float.BYTES);

        return id;
    }
    /**
     * Generates the Element Buffer Object and fills
     * it with an empty {@link FloatBuffer}.
     *
     * @return id of the Element Buffer Object
     *
     * @author Tim Kloepper
     */
    private int _genElementBufferObject() {
        int id;

        id = glGenBuffers();

        IntBuffer indexBuffer;

        indexBuffer = BufferUtils.createIntBuffer(MAX_INDEX_AMOUNT);
        indexBuffer.put(new int[MAX_INDEX_AMOUNT]).flip();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_DYNAMIC_DRAW);

        return id;
    }
    /**
     * Generates the Vertex Buffer Object and fills
     * it with an empty {@link IntBuffer}.
     *
     * @return id of the Vertex Buffer Object
     *
     * @author Tim Kloepper
     */
    private int _genVertexBufferObject() {
        int id;

        id = glGenBuffers();

        FloatBuffer vertexBuffer;

        vertexBuffer = BufferUtils.createFloatBuffer(MAX_VERTEX_AMOUNT);
        vertexBuffer.put(new float[MAX_VERTEX_AMOUNT]).flip();

        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);

        return id;
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
     * Clears the batch from all meshes.
     *
     * @author Tim Klopper
     */
    public void flush() {
        _flushBuffers();

        _activeMeshesAndTextures.clear();
    }
    /**
     * Clears the buffers and resets the pointer.
     * Should only be called individually if you know
     * what you are doing.
     *
     * @author Tim Kloepper
     */
    private void _flushBuffers() {
        _vertexPointer = 0;
        _indexPointer = 0;

        glBindBuffer(GL_ARRAY_BUFFER, _vboID);
        glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(MAX_VERTEX_AMOUNT).put(new float[MAX_VERTEX_AMOUNT]).flip(), GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createIntBuffer(MAX_INDEX_AMOUNT).put(new int[MAX_INDEX_AMOUNT]).flip(), GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * Rebuilds the buffers by clearing them and then
     * adding all active meshes back in.
     *
     * @author Tim Kloepper
     */
    private void _rebuild() {
        _flushBuffers();

        // This rebuilds the active meshes, as all meshes get overwritten with the new pointer values.
        for (TexturedMesh mesh : _activeMeshesAndTextures.keySet()) {
            addMesh(mesh, _activeMeshesAndTextures.get(mesh));
        }
    }


    // -+- UPDATE LOOP -+- //

    /**
     * Called every frame,
     * will render and rebuilt if required.
     *
     * @author Tim Kloepper
     */
    public void update() {
        if (_rebuiltRequired) {
            _rebuild();}

        render();
    }


    // -+- RENDER -+- //

    /**
     * Renders all meshes in the batch.
     *
     * @author Tim Kloepper
     */
    public void render() {
        glBindVertexArray(_vaoID);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        _shaderProgram.use();

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
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        _shaderProgram.unuse();
        _placeHolderTexture.unbind();
        for (int index = 0; index < MAX_TEXTURE_AMOUNT; index++) {
            if (_textures[index] == null) {continue;}

            _textures[index].unbind();
        }
    }


    // -+- GETTERS -+- //

    /**
     * Returns the shader program used by this batch.
     *
     * @return the used shader program
     *
     * @author Tim Kloepper
     */
    public JangineShaderProgram getShaderProgram() {
        return _shaderProgram;
    }

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


}