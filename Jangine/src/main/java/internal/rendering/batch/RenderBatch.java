package internal.rendering.batch;


import internal.rendering.camera.Camera2D;
import internal.rendering.shader.ShaderProgram;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public abstract class RenderBatch {


    public static final int MAX_INDEX_AMOUNT = 444;
    public static final int MAX_VERTEX_AMOUNT = 444;


    protected ShaderProgram _shaderProgram;
    protected Camera2D _camera;

    protected int _vaoID, _eboID, _vboID;
    protected int _vertexPointer, _indexPointer;

    protected boolean _rebuiltRequired;


    // -+- CREATION -+- //

    public RenderBatch(ShaderProgram shaderProgram, Camera2D camera) {
        _shaderProgram = shaderProgram;
        _camera = camera;

        _vertexPointer = 0;
        _indexPointer = 0;

        _rebuiltRequired = false;

        _initObjects();
    }
    public RenderBatch(String shaderPath, Camera2D camera) {
        _shaderProgram = new ShaderProgram(shaderPath);
        _camera = camera;

        _vertexPointer = 0;
        _indexPointer = 0;

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
    protected final void _initObjects() {
        _vaoID = _genVertexArrayObject();
        _eboID = _genElementBufferObject();
        _vboID = _genVertexBufferObject();

        _genVertexAttribPointers();

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    protected abstract int _genVertexArrayObject();
    protected abstract void _genVertexAttribPointers();
    /**
     * Generates the Element Buffer Object and fills
     * it with an empty {@link FloatBuffer}.
     *
     * @return id of the Element Buffer Object
     *
     * @author Tim Kloepper
     */
    protected final int _genElementBufferObject() {
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
    protected final int _genVertexBufferObject() {
        int id;

        id = glGenBuffers();

        FloatBuffer vertexBuffer;

        vertexBuffer = BufferUtils.createFloatBuffer(MAX_VERTEX_AMOUNT);
        vertexBuffer.put(new float[MAX_VERTEX_AMOUNT]).flip();

        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);

        return id;
    }


    // -+- BATCH MANAGEMENT -+- //

    /**
     * Clears the batch from all meshes.
     *
     * @author Tim Kloepper
     */
    public final void flush() {
        _flushBuffers();

        _clearMeshHolders();
    }
    /**
     * Clears the buffers and resets the pointer.
     * Should only be called individually if you know
     * what you are doing.
     *
     * @author Tim Kloepper
     */
    protected final void _flushBuffers() {
        _vertexPointer = 0;
        _indexPointer = 0;

        glBindBuffer(GL_ARRAY_BUFFER, _vboID);
        glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(MAX_VERTEX_AMOUNT).put(new float[MAX_VERTEX_AMOUNT]).flip(), GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createIntBuffer(MAX_INDEX_AMOUNT).put(new int[MAX_INDEX_AMOUNT]).flip(), GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    protected abstract void _rebuild();
    protected abstract void _clearMeshHolders();


    // -+- RENDER -+- //

    protected abstract void render();


    /**
     * Called every frame,
     * will render and rebuilt if required.
     *
     * @author Tim Kloepper
     */
    public final void update() {
        if (_rebuiltRequired) {
            _rebuild();
        }

        render();
    }


    // -+- GETTERS -+- //

    /**
     * Returns the shader program used by this batch.
     *
     * @return the used shader program
     *
     * @author Tim Kloepper
     */
    public final ShaderProgram getShaderProgram() {
        return _shaderProgram;
    }


}