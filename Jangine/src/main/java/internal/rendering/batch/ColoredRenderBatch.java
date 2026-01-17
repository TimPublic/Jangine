package internal.rendering.batch;


import internal.rendering.JangineCamera2D;
import internal.rendering.JangineShaderProgram;
import internal.rendering.mesh.ColoredMesh;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class ColoredRenderBatch extends RenderBatch {


    public static final int VERTEX_SIZE = 6;


    private final HashSet<ColoredMesh> _activeMeshes;


    // -+- CREATION -+- //

    public ColoredRenderBatch(String shaderPath, JangineCamera2D camera) {
        super(shaderPath, camera);

        _activeMeshes = new HashSet<>();
    }
    public ColoredRenderBatch(JangineShaderProgram shaderProgram, JangineCamera2D camera) {
        super(shaderProgram, camera);

        _activeMeshes = new HashSet<>();
    }

    @Override
    protected int _genVertexArrayObject() {
        int id;

        id = glGenVertexArrays();
        glBindVertexArray(id);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 2 * Float.BYTES);

        return id;
    }

    @Override
    protected void _genVertexAttribPointers() {
        glBindVertexArray(_vaoID);

        glBindBuffer(GL_ARRAY_BUFFER, _vboID);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 2 * Float.BYTES);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    // -+- MESHES -+- //

    /**
     * Adds a mesh to the batch.
     * If the mesh is already in the batch,
     * {@link ColoredRenderBatch#updateMesh(ColoredMesh)} is called.
     *
     * @param mesh mesh to be added
     * @return success
     *
     * @author Tim Kloepper
     */
    public boolean addMesh(ColoredMesh mesh) {
        FloatBuffer subVertexBuffer;
        IntBuffer subIndexBuffer;

        if (!_activeMeshes.add(mesh)) {updateMesh(mesh); return true;}

        subVertexBuffer = BufferUtils.createFloatBuffer(mesh.vertices.length);
        subIndexBuffer = BufferUtils.createIntBuffer(mesh.indices.length);

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
    public void updateMesh(ColoredMesh mesh) {
        if (!_activeMeshes.contains(mesh)) {return;}

        _rebuiltRequired = true;

        // This is maybe not necessary, as it gets rebuilt and all changes on the mesh
        // are also applied to this reference.
        _activeMeshes.remove(mesh);
        _activeMeshes.add(mesh);
    }
    /**
     * Removes a mesh from the batch.
     * This action will result in a rebuilt on the next {@link ColoredRenderBatch#update()} call,
     * which can be expensive if many meshes are inside this batch.
     *
     * @param mesh mesh to be removed
     *
     * @author Tim Kloepper
     */
    public void rmvMesh(ColoredMesh mesh) {
        if (!_activeMeshes.remove(mesh)) {return;}

        _rebuiltRequired = true;
    }


    // -+- BATCH MANAGEMENT -+- //


    @Override
    protected void _rebuild() {
        HashSet<ColoredMesh> meshes;

        meshes = new HashSet<>(_activeMeshes);

        flush();

        for (ColoredMesh mesh : meshes) {
            addMesh(mesh);
        }

        _rebuiltRequired = false;
    }
    @Override
    protected void _clearMeshHolders() {
        _activeMeshes.clear();
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

        _shaderProgram.upload("uProjectionMatrix", _camera.getProjectionMatrix());
        _shaderProgram.upload("uViewMatrix", _camera.getViewMatrix());

        glDrawElements(GL_TRIANGLES, _indexPointer, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        _shaderProgram.unuse();
    }


}