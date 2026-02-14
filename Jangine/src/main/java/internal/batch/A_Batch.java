package internal.batch;

import internal.rendering.camera.Camera2D;
import internal.rendering.mesh.A_Mesh;
import internal.rendering.mesh.MeshInfo;
import internal.rendering.shader.ShaderProgram;

import javax.naming.SizeLimitExceededException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public abstract class A_Batch<T extends A_Mesh> {


    // -+- CREATION -+- //

    public A_Batch(ShaderProgram shader, int vertices_amount, int vertex_size, int indices_amount) {
        _shader = shader;

        _MESH_INFO = new HashMap<>();

        _VERTICES = new float[vertices_amount * vertex_size];
        _VERTICES_ALLOCATOR = new FragmentAllocator(vertices_amount * vertex_size);
        _INDICES = new int[indices_amount];
        _INDICES_ALLOCATOR = new FragmentAllocator(indices_amount);

        // Needs to be called after initializing the vertices and indices arrays.
        _VAO_ID = h_generateVAO();
        _VBO_ID = h_generateVBO();
        _EBO_ID = h_generateEBO();

        p_genVertexAttribPointers();

        glBindVertexArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private int h_generateVAO() {
        int id;

        id = glGenVertexArrays();
        glBindVertexArray(id);

        return id;
    }
    private int h_generateVBO() {
        int id;

        id = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, _VERTICES, GL_DYNAMIC_DRAW);

        return id;
    }
    private int h_generateEBO() {
        int id;

        id = glGenBuffers();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, _INDICES, GL_DYNAMIC_DRAW);

        return id;
    }

    protected abstract void p_genVertexAttribPointers();


    // -+- PARAMETERS -+- //

    // FINALS //

    private final HashMap<T, MeshInfo> _MESH_INFO;

    private final float[] _VERTICES;
    private final FragmentAllocator _VERTICES_ALLOCATOR;

    private final int[] _INDICES;
    private final FragmentAllocator _INDICES_ALLOCATOR;

    private final int _VBO_ID;
    private final int _EBO_ID;
    private final int _VAO_ID;

    // NON-FINALS //

    private ShaderProgram _shader;


    // -+- MESH MANAGEMENT -+- //

    public boolean addMesh(T mesh) {
        float[] vertices;
        int[] indices;
        int nextFreeVertex, nextFreeIndex;

        if (mesh == null) throw new IllegalStateException("[BATCH ERROR] : Mesh is null!");
        if (_MESH_INFO.containsKey(mesh)) return false;

        // For quick access.
        vertices = mesh.vertices;
        indices = mesh.indices;

        // Grab the free indices and increase the pointers.
        // Also checks for enough space.
        nextFreeVertex = _VERTICES_ALLOCATOR.allocate(mesh.vertices.length);
        if (nextFreeVertex == -1) {
            _rebuild();

            nextFreeVertex = _VERTICES_ALLOCATOR.allocate(mesh.vertices.length);

            if (nextFreeVertex == -1) throw new RuntimeException(new SizeLimitExceededException("[BATCH ERROR] : Not enough space for the vertices!"));
        }
        nextFreeIndex = _INDICES_ALLOCATOR.allocate(mesh.indices.length);
        if (nextFreeIndex == -1) {
            _rebuildIndices();

            nextFreeIndex = _INDICES_ALLOCATOR.allocate(mesh.indices.length);

            if (nextFreeIndex == -1) throw new RuntimeException(new SizeLimitExceededException("[BATCH ERROR] : Not enough space for the indices!"));
        }

        // Add to internal arrays.
        System.arraycopy(vertices, 0, _VERTICES, nextFreeVertex, vertices.length);
        for (int index = 0; index < indices.length; index++) {
            _INDICES[nextFreeIndex + index] = indices[index] + (nextFreeVertex / mesh.getVertexSize());
        }

        MeshInfo info;

        info = new MeshInfo(mesh);
        info.addedToBatch(nextFreeVertex, nextFreeIndex);

        // Add to buffers.
        glBindBuffer(GL_ARRAY_BUFFER, _VBO_ID);
        glBufferSubData(GL_ARRAY_BUFFER, (long) info.vertexPointer * Float.BYTES, mesh.vertices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _EBO_ID);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, (long) info.indexPointer * Integer.BYTES, Arrays.copyOfRange(_INDICES, info.indexPointer, info.indexPointer + info.indicesAmount));

        // Add to registry for updates.
        _MESH_INFO.put(mesh, info);

        p_onMeshAdded(mesh);

        return true;
    }
    public boolean rmvMesh(T mesh) {
        if (mesh == null) throw new IllegalStateException("[BATCH ERROR] : Mesh is null!");

        MeshInfo info;

        info = _MESH_INFO.get(mesh);

        if (info == null) return false;

        glBindBuffer(GL_ARRAY_BUFFER, _VBO_ID);
        glBufferSubData(GL_ARRAY_BUFFER, (long) info.vertexPointer * Float.BYTES, new float[info.verticesAmount * info.vertexSize]);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _EBO_ID);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, (long) info.indexPointer * Integer.BYTES, new int[info.indicesAmount]);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        _VERTICES_ALLOCATOR.addFragment(new FragmentNode(info.vertexPointer, info.verticesAmount * info.vertexSize));
        _INDICES_ALLOCATOR.addFragment(new FragmentNode(info.indexPointer, info.indicesAmount));

        info.removedFromBatch();
        _MESH_INFO.remove(mesh);

        p_onMeshRemoved(mesh);

        return true;
    }

    public void addOrUpdateMesh(T mesh) {
        if (_MESH_INFO.containsKey(mesh)) {
            updateMesh(mesh);

            return;
        }

        addMesh(mesh);
    }

    public void updateMesh(T mesh) {
        if (mesh == null) throw new IllegalStateException("[BATCH ERROR] : Mesh is null!");

        MeshInfo info;

        info = _MESH_INFO.get(mesh);

        if (info == null) throw new IllegalStateException("[BATCH ERROR] : Mesh is not in this batch and can therefore not be updated!");

        // The size of the mesh has changed, so we need to rebuild.
        if (!info.isEqual(mesh)) {
            rmvMesh(mesh);
            addMesh(mesh);

            return;
        }

        System.arraycopy(mesh.vertices, 0, _VERTICES, info.vertexPointer, mesh.vertices.length);
        for (int index = 0; index < mesh.indices.length; index++) {
            _INDICES[index + info.indexPointer] = mesh.indices[index] + info.vertexPointer;
        }

        glBindBuffer(GL_ARRAY_BUFFER, _VBO_ID);
        glBufferSubData(GL_ARRAY_BUFFER, (long) info.vertexPointer * Float.BYTES, mesh.vertices);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _EBO_ID);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, (long) info.indexPointer * Integer.BYTES, Arrays.copyOfRange(_INDICES, info.indexPointer, info.indexPointer + mesh.indices.length));

        p_onMeshUpdated(mesh);
    }

    protected abstract void p_onMeshAdded(T mesh);
    protected abstract void p_onMeshRemoved(T mesh);
    protected abstract void p_onMeshUpdated(T mesh);


    // -+- BUFFER MANAGEMENT -+- //

    public void flush() {
        Arrays.fill(_VERTICES, 0, _VERTICES.length, 0);
        Arrays.fill(_INDICES, 0, _INDICES.length, 0);

        _VERTICES_ALLOCATOR.clear();
        _INDICES_ALLOCATOR.clear();

        glBindBuffer(GL_ARRAY_BUFFER, _VBO_ID);
        glBufferData(GL_ARRAY_BUFFER, _VERTICES, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _EBO_ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, _INDICES, GL_DYNAMIC_DRAW);

        _MESH_INFO.clear();

        p_onFlush();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    protected abstract void p_onFlush();

    private void _rebuild() {
        HashSet<T> meshes;

        meshes = new HashSet<>(_MESH_INFO.keySet());

        flush();

        meshes.forEach(this::addMesh);
    }
    private void _rebuildIndices() {
        MeshInfo info;

        _INDICES_ALLOCATOR.clear();

        int index;

        Arrays.fill(_INDICES, 0, _INDICES.length, 0);
        index = 0;

        for (T mesh : _MESH_INFO.keySet()) {
            info = _MESH_INFO.get(mesh);

            info.indexPointer = index;

            for (int indexValue : mesh.indices) {
                _INDICES[index] = indexValue + info.vertexPointer;

                index++;
            }
        }

        _INDICES_ALLOCATOR.allocate(index);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _EBO_ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, _INDICES, GL_DYNAMIC_DRAW);
    }


    // -+- RENDERING -+- //

    public void render(Camera2D camera) {
        glBindVertexArray(_VAO_ID);
        _shader.use();

        p_prepareRendering(camera);

        glDrawElements(GL_TRIANGLES, _INDICES.length, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        _shader.unuse();
    }

    protected abstract void p_prepareRendering(Camera2D camera2D);


    // -+- SHADER MANAGEMENT -+- //

    public void setShader(ShaderProgram shader) {
        if (shader == null) throw new IllegalStateException("[BATCH ERROR] : Shader is null!");

        _shader = shader;
    }


    // -+- GETTERS -+- //

    public Set<T> getContainedMeshes() {
        return _MESH_INFO.keySet();
    }

    public ShaderProgram getActiveShader() {
        return _shader;
    }


    // -+- CHECKERS -+- //

    public boolean contains(T mesh) {
        return _MESH_INFO.containsKey(mesh);
    }


}