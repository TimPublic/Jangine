package internal.rendering;


import internal.rendering.texture.JangineTexture;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL43.glInvalidateBufferSubData;


/**
 * This class is the jangine implementation of a render batch.
 * A render batch hold multiple vertices, a texture and a shader,
 * to issue one draw call for multiple meshes.
 * <p>
 * Currently, the mesh-removal does not work correctly,
 * but disables all updates on the mesh.
 * This is because of the invalidation-function that is used
 * here. It just makes the data at the point free again,
 * but does not directly delete it.
 * <p>
 * The vertex layout needs to contain those values and only those in the following order:
 * <ul>
 *     <li>{@link Float} x</li>
 *     -> X-Coordinate of the vertex' position.
 *     <li>{@link Float} y</li>
 *     -> Y-Coordinate of the vertex' position.
 *     <li>{@link Float} xUV</li>
 *     -> X-Coordinate of the uv-position, ranging from 0.0 to 1.0.
 *     <li>{@link Float} yUV</li>
 *     -> Y-Coordinate of the uv-position, ranging from 0.0 to 1.0.
 * </ul>
 *
 * @author Tim Kloepper
 * @version 0.9
 */
public class JangineRenderBatch {


    public final int VERTEX_LENGTH = 444;
    public final int INDEX_LENGTH = 444;


    private JangineShaderProgram _shaderProgram;
    private JangineTexture _texture;
    private JangineCamera2D _camera;

    private int _vertexPointer;
    private int _indexPointer;

    private final HashMap<JangineMesh, Integer> _meshVertexPointers;
    private final HashMap<JangineMesh, Integer> _meshIndexPointers;

    private final ArrayList<JangineMesh> _currentMeshes;

    private int _vaoID, _vboID, _eboID;


    /**
     * @param shaderProgram The shader, used to render all given {@link JangineMesh}.
     * @param texture The texture, applied to all {@link JangineMesh}.
     * @param camera The camera, used as a viewport.
     *
     * @author Tim Kloepper
     */
    public JangineRenderBatch(JangineShaderProgram shaderProgram, JangineTexture texture, JangineCamera2D camera) {
        _shaderProgram = shaderProgram;
        _texture = texture;
        _camera = camera;

        _vertexPointer = 0;
        _indexPointer = 0;

        _meshVertexPointers = new HashMap<>();
        _meshIndexPointers = new HashMap<>();

        _currentMeshes = new ArrayList<>();

        _initObjects();
    }


    /**
     * This method initializes the vertex-array-object, the vertex-buffer-object and the element-buffer-object.
     * Those id's then get assigned to the corresponding member-variables.
     *
     * @author Tim Kloepper
     */
    private void _initObjects() {
        _vaoID = glGenVertexArrays();
        glBindVertexArray(_vaoID);

        _genVertexBufferObject();
        _genElementBufferObject();

        // Set attributes.
        /* Position       : */ glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        /* UV-Coordinates : */ glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);

        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * This method generates the vertex-buffer-objects' id and assigns it to the corresponding member-variable.
     *
     * @author Tim Kloepper
     */
    private void _genVertexBufferObject() {
        _vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, _vboID);

        FloatBuffer vertexBuffer;
        vertexBuffer = BufferUtils.createFloatBuffer(VERTEX_LENGTH);
        vertexBuffer.put(new float[VERTEX_LENGTH]).flip();

        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);
    }

    /**
     * This method generates the element-buffer-objects' id and assigns it to the corresponding member-variable.
     *
     * @author Tim Kloepper
     */
    private void _genElementBufferObject() {
        _eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _eboID);

        IntBuffer indexBuffer;
        indexBuffer = BufferUtils.createIntBuffer(INDEX_LENGTH);
        indexBuffer.put(new int[INDEX_LENGTH]).flip();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_DYNAMIC_DRAW);
    }


    /**
     * This method lets you add a {@link JangineMesh} to the Batch.
     * Therefore, this mesh is rendered until removed with rmvMesh.
     *
     * @param mesh A {@link JangineMesh} to be added and therefore rendered by the Batch.
     *
     * @author Tim Kloepper
     */
    public void addMesh(JangineMesh mesh) {
        if (_currentMeshes.contains(mesh)) {return;}

        _currentMeshes.add(mesh);

        _meshVertexPointers.put(mesh, _vertexPointer);
        _meshIndexPointers.put(mesh, _indexPointer);

        IntBuffer indexSubBuffer;
        indexSubBuffer = BufferUtils.createIntBuffer(mesh.getIndices().length);

        for (int index = 0; index < mesh.getIndices().length; index++) {
            // We have to set the index in relation with the other vertices.
            // The vertex-pointer basically holds the current length of the array,
            // so just adding this to the actual index, will account for all other vertices
            // in the buffer.
            // It is also very important to not increase the vertex-pointer before this
            // insertion, as the original index is based on the original vertex amount.
            // We just set the other vertices as a base, not a direct relation!
            indexSubBuffer.put(mesh.getIndices()[index] + (_vertexPointer / 4));
        }
        indexSubBuffer.flip();

        FloatBuffer vertexSubBuffer;
        vertexSubBuffer = BufferUtils.createFloatBuffer(mesh.getVertices().length);

        vertexSubBuffer.put(mesh.getVertices()).flip();

        // Insert to vertex-buffer.
        glBindBuffer(GL_ARRAY_BUFFER, _vboID);
        glBufferSubData(GL_ARRAY_BUFFER, _vertexPointer * Float.BYTES,  vertexSubBuffer);

        // Insert to element-buffer.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _eboID);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, _indexPointer * Integer.BYTES, indexSubBuffer);

        _indexPointer += mesh.getIndices().length; // Is the next free index, because of the leading zero in indexing.
        _vertexPointer += mesh.getVertices().length; // Same.

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Removes a mesh from the Batch. If this Mesh is not part
     * of the Batch, nothing happens.
     * This removal currently does not mean,
     * that the mesh is not rendered anymore, it just can not be
     * changed from this point forward.
     *
     * @param mesh A {@link JangineMesh} to be removed from the Batch.
     *
     * @author Tim Kloepper
     */
    public void rmvMesh(JangineMesh mesh) {
        if (!_currentMeshes.contains(mesh)) {return;}

        glBindVertexArray(_vaoID);

        _currentMeshes.remove(mesh);

        // Probably no need to bind, but safety first for now!
        // TODO: Probably needs a bit of time to bind, so check if it is needed.
        glBindBuffer(GL_ARRAY_BUFFER, _vboID);
        glInvalidateBufferSubData(_vboID, _meshVertexPointers.get(mesh) * Float.BYTES, mesh.getVertices().length * Float.BYTES);

        // If rmvMesh is called, is it possible, the mesh was deleted, so deleting every reference of it, is the correct step.
        // This also means, a reactivation on a future addMesh, is not possible and the invalid data will only vanish on flush.
        _meshVertexPointers.remove(mesh);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _eboID);
        glInvalidateBufferSubData(_eboID, _meshIndexPointers.get(mesh) * Integer.BYTES, mesh.getIndices().length * Integer.BYTES);

        _meshIndexPointers.remove(mesh);

        glBindVertexArray(0);

        // Unbind buffer.
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    /**
     * With this method, you can reupload a mesh, to transfer changes into the Batch.
     * If this mesh is not part of the Batch, nothing happens.
     *
     * @param mesh A {@link JangineMesh} to be updated in the Batch.
     *
     * @author Tim Kloepper
     */
    public void updateMesh(JangineMesh mesh) {
        if (!_currentMeshes.contains(mesh)) {return;}

        FloatBuffer updatedFloatBuffer;
        updatedFloatBuffer = BufferUtils.createFloatBuffer(mesh.getVertices().length); // Can safely expect this float[] to have not grown, because of the fixed size in the get-method.
        updatedFloatBuffer.put(mesh.getVertices()).flip();

        glBindBuffer(GL_ARRAY_BUFFER, _vboID);
        glBufferSubData(GL_ARRAY_BUFFER, _meshVertexPointers.get(mesh) * Float.BYTES, updatedFloatBuffer);

        IntBuffer updatedIndexBuffer;
        updatedIndexBuffer = BufferUtils.createIntBuffer(mesh.getIndices().length);
        updatedIndexBuffer.put(mesh.getIndices()).flip();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _eboID);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, _meshIndexPointers.get(mesh) * Integer.BYTES, updatedIndexBuffer);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }


    /**
     * This method clears the whole Batch, which means that all data gets lost
     * and needs to be reuploaded.
     * For this, the method currently just creates new buffers,
     * but does not delete the old ones.
     *
     * @author Tim Kloepper
     */
    public void flush() {
        _meshVertexPointers.clear();
        _meshIndexPointers.clear();

        _vertexPointer = 0;
        _indexPointer = 0;

        _currentMeshes.clear();

        _initObjects(); // TODO: Delete old objects!
    }


    /**
     * This method renders the Batch to the screen, meaning all meshes
     * that are currently in the Batch.
     *
     * @author Tim Kloepper
     */
    public void render() {
        glBindVertexArray(_vaoID);

        // Enable attributes.
        /* Position       : */ glEnableVertexAttribArray(0);
        /* UV-Coordinates : */ glEnableVertexAttribArray(1);

        // Activate shader.
        _shaderProgram.use();

        // Upload texture.
        glActiveTexture(GL_TEXTURE0);
        _texture.bind();
        _shaderProgram.upload("uTextureSampler", 0);

        // Upload camera data.
        _shaderProgram.upload("uProjectionMatrix", _camera.getProjectionMatrix());
        _shaderProgram.upload("uViewMatrix", _camera.getViewMatrix());

        // Draw.
        glDrawElements(GL_TRIANGLES, _indexPointer, GL_UNSIGNED_INT, 0);

        // Unbind everything.
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        _shaderProgram.unuse();
        _texture.unbind();
    }


}