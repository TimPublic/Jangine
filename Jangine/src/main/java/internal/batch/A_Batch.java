package internal.batch;


import internal.rendering.mesh.A_Mesh;
import internal.rendering.mesh.MeshInfo;
import internal.rendering.shader.ShaderProgram;

import javax.naming.SizeLimitExceededException;
import java.util.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


/**
 * <p>
 * This is the abstract base class for any specific batch.
 * It already takes care of all the memory side management,
 * regarding gpu, vertices and indices.
 * </p>
 * <p>
 * A batch is used to package many meshes into big arrays of vertices
 * and indices to render them with one draw call.
 * Every mesh specifies through a generic {@link T} what subclass of {@link A_Mesh}
 * it manages.
 * </p>
 * <p>
 * Every batch has one {@link ShaderProgram},
 * that is used to render all the meshes this batch contains.
 * This shader can be changed any time with {@link A_Batch#setShader(ShaderProgram)}.
 * </p>
 * <p>
 * By keeping track and managing fragments inside the internal arrays and buffers effectively with these classes:
 * <ul>
 *     <li>{@link FragmentNodePool}</li>
 *     <li>{@link FragmentNode}</li>
 *     <li>{@link FragmentBinaryTree}</li>
 * </ul>
 * This class ensures a much faster removal of meshes and update of size changed meshes, than normally expected
 * from batches. <br>
 * The fragments are managed dynamically and are used for new additions and a rebuild only occurs if really necessary. <br>
 * This class can also only rebuild the indices to reduce allocation time even further. <br>
 * This method ensured maximum flexibility and space usage. <br>
 * Addition of meshes and updating size unchanged meshes remains at the normal speed.
 * </p>
 * <p>
 * The batch class gives you full control over the meshes and the rendering process through a clean API:
 * <ul>
 *     <li>[ADDING MESHES] : {@link A_Batch#addMesh(A_Mesh)} OR {@link A_Batch#addOrUpdateMesh(A_Mesh)}</li>
 *     <li>[REMOVING MESHES] : {@link A_Batch#rmvMesh(A_Mesh)}</li>
 *     <li>[UPDATING MESHES] : {@link A_Batch#updateMesh(A_Mesh)} OR {@link A_Batch#addOrUpdateMesh(A_Mesh)}</li>
 *     <li>[CLEARING BATCH] : {@link A_Batch#flush()}</li>
 *     <li>[CHECKING CONTENTS] : {@link A_Batch#contains(A_Mesh)} OR {@link A_Batch#getContainedMeshes()}</li>
 *     <li>[MANAGING SHADER] : {@link A_Batch#setShader(ShaderProgram)} OR {@link A_Batch#getActiveShader()}</li>
 * </ul>
 * </p>
 *
 * @param <T> The subclass of the {@link A_Mesh} class, that the specific batch manages.
 *
 * @version 1.0
 * @author Tim Kloepper
 */
public abstract class A_Batch<T extends A_Mesh> {


    // -+- CREATION -+- //

    /**
     * Creates the abstract class for any specific batch to extend from.
     * It initializes all the important arrays and the registry.
     *
     * @param shader The {@link ShaderProgram} that will be used to render the meshes of this batch.
     * @param vertices_amount The amount of vertices that this batch can hold.
     * @param vertex_size The size of one vertex that this batch holds.
     * @param indices_amount The amount of indices this batch holds.
     *
     * @author Tim Kloepper
     */
    public A_Batch(ShaderProgram shader, int vertices_amount, int vertex_size, int indices_amount) {
        _shader = shader;

        _MESH_INFO = new HashMap<>();

        _FRAGMENT_POOL = new FragmentNodePool();
        _VERTICES = new float[vertices_amount * vertex_size];
        _VERTICES_FRAGMENT_TREE = new FragmentBinaryTree(vertices_amount * vertex_size, _FRAGMENT_POOL);
        _INDICES = new int[indices_amount];
        _INDICES_FRAGMENT_TREE = new FragmentBinaryTree(indices_amount, _FRAGMENT_POOL);

        // Needs to be called after initializing the vertices and indices arrays.
        _VAO_ID = h_generateVAO();
        _VBO_ID = h_generateVBO();
        _EBO_ID = h_generateEBO();

        p_genVertexAttribPointers();

        glBindVertexArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Generates the vertex array object and returns it's id,
     * by calling the {@code glGenVertexArrays()} function.
     * Also binds the vertex array object, in preparation for
     * the generation of the vertex buffer object and the element buffer object
     * with the {@link A_Batch#h_generateVBO()} and {@link A_Batch#h_generateEBO()} methods.
     *
     * @return The id of the vertex array object.
     *
     * @author Tim Kloepper
     */
    private int h_generateVAO() {
        int id;

        id = glGenVertexArrays();
        glBindVertexArray(id);

        return id;
    }
    /**
     * Generates the vertex buffer object and returns it's id,
     * by calling the {@code glGenBuffers()} function.
     * Also inserts the internal vertex array with the {@code glBufferData()} function with {@code GL_DYNAMIC_DRAW},
     * in order to set the size and being able to use the {@code glBufferSubData()} function.
     * This method does not unbind the vertex buffer object.
     *
     * @return The id of the vertex buffer object.
     *
     * @author Tim Kloepper
     */
    private int h_generateVBO() {
        int id;

        id = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, id);
        glBufferData(GL_ARRAY_BUFFER, _VERTICES, GL_DYNAMIC_DRAW);

        return id;
    }
    /**
     * Generates the element buffer object and returns it's id,
     * by calling the {@code glGenBuffers()} function.
     * Also inserts the internal index array with the {@code glBufferData()} function with {@code GL_DYNAMIC_DRAW},
     * in order to set the size and being able to use the {@code glBufferSubData()} function.
     * This method does not unbind the element buffer object.
     *
     * @return The id of the element buffer object.
     *
     * @author Tim Kloepper
     */
    private int h_generateEBO() {
        int id;

        id = glGenBuffers();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, _INDICES, GL_DYNAMIC_DRAW);

        return id;
    }

    /**
     * Is used by the specific batches, to generate the vertex attribute pointers.
     * At this point, the vertex array object is already bound and will be unbound
     * after this method call.
     *
     * @author Tim Kloepper
     */
    protected abstract void p_genVertexAttribPointers();


    // -+- PARAMETERS -+- //

    // FINALS //

    /**
     * Holds the {@link MeshInfo} of every mesh and
     * simultaneously functions as a registry for meshes,
     * as every mesh in this batch and only those
     * have mesh info inside this {@link java.util.Hashtable}.
     */
    private final HashMap<T, MeshInfo> _MESH_INFO;

    private final FragmentNodePool _FRAGMENT_POOL;
    /**
     * The internal array, holding the vertices of all meshes.
     * THis makes possibly required quick access to the vertices possible.
     */
    private final float[] _VERTICES;
    private final FragmentBinaryTree _VERTICES_FRAGMENT_TREE;
    /**
     * The internal array, holding the indices of all meshes.
     * This makes possibly required quick access to the indices possible.
     */
    private final int[] _INDICES;
    private final FragmentBinaryTree _INDICES_FRAGMENT_TREE;

    /**
     * The id for the vertex buffer object inside the gpu.
     */
    private final int _VBO_ID;
    /**
     * The id for the element buffer object inside the gpu.
     */
    private final int _EBO_ID;
    /**
     * The id for the vertex array object inside the gpu.
     */
    private final int _VAO_ID;

    // NON-FINALS //

    /**
     * The {@link ShaderProgram} that is used,
     * to render the meshes of this batch.
     */
    private ShaderProgram _shader;


    // -+- MESH MANAGEMENT -+- //

    /**
     * <p>
     * Adds an {@link A_Mesh} of the subclass {@link T} to this batch. <br>
     * This will add it into internal arrays for
     * the vertices and indices but also add it
     * into the actual gpu-side buffers. <br>
     * Until the mesh is removed with the {@link A_Batch#rmvMesh(A_Mesh)} method,
     * it is rendered every {@link A_Batch#render()} call.
     * Also creates a {@link MeshInfo} for the mesh, for updates.
     * </p>
     * <p>
     * If the mesh is already inside this buffer,
     * it will return {@code false}. <br>
     * If you are unsure whether the mesh is already inside this buffer or not,
     * you can use the {@link A_Batch#contains(A_Mesh)}
     * or the {@link A_Batch#addOrUpdateMesh(A_Mesh)} method.
     * </p>
     *
     * @param mesh The mesh of the subclass {@link T} that should be added to this batch. <br>
     *             This mesh can not be {@code null}.
     *
     * @return Whether the mesh was successfully added, or not.
     *
     * @author Tim Kloepper
     */
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
        nextFreeVertex = _VERTICES_FRAGMENT_TREE.allocate(mesh.vertices.length);
        if (nextFreeVertex == -1) {
            _rebuild();

            nextFreeVertex = _VERTICES_FRAGMENT_TREE.allocate(mesh.vertices.length);

            if (nextFreeVertex == -1) throw new RuntimeException(new SizeLimitExceededException("[BATCH ERROR] : Not enough space for the vertices!"));
        }
        nextFreeIndex = _INDICES_FRAGMENT_TREE.allocate(mesh.indices.length);
        if (nextFreeIndex == -1) {
            _rebuildIndices();

            nextFreeIndex = _INDICES_FRAGMENT_TREE.allocate(mesh.indices.length);

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
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, (long) info.indexPointer * Integer.BYTES, Arrays.copyOfRange(_INDICES, info.indexPointer, info.indexPointer + mesh.indices.length));

        // Add to registry for updates.
        _MESH_INFO.put(mesh, info);

        p_onMeshAdded(mesh);

        return true;
    }
    /**
     * <p>
     * Removes an {@link A_Mesh} of the subclass {@link T} from this batch. <br>
     * This will remove the vertices and indices of the mesh from both the internal
     * vertex and index arrays and remove it from the gpu-side buffers. <br>
     * This will create a {@link FragmentNode}, both for the vertex buffer and array, as well
     * as the index buffer and array.
     * Both fragment nodes is inserted into two {@link FragmentBinaryTree}, which
     * enables this batch to later use them for new meshes.
     * With this tactic, the buffer does not need to rebuild itself every time and
     * enables future meshes to fill these fragments.
     * </p>
     * <p>
     * If this batch does not contain the mesh,
     * it will return {@code false}. <br>
     * If you are unsure whether this mesh is inside this batch or not,
     * use the {@link A_Batch#contains(A_Mesh)} method.
     * </p>
     *
     * @param mesh The mesh of the subclass {@link T} that should be removed from this batch. <br>
     *             This mesh can not be {@code null}.
     *
     * @return Whether the mesh was successfully removed, or not.
     *
     * @author Tim Kloepper
     */
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

        _VERTICES_FRAGMENT_TREE.addNode(_FRAGMENT_POOL.request(info.vertexPointer, info.verticesAmount * info.vertexSize));
        _INDICES_FRAGMENT_TREE.addNode(_FRAGMENT_POOL.request(info.indexPointer, info.indicesAmount));

        info.removedFromBatch();
        _MESH_INFO.remove(mesh);

        p_onMeshRemoved(mesh);

        return true;
    }

    /**
     * Checks if the {@link A_Mesh} of the subclass {@link T} is inside this batch
     * to then either update it with the {@link A_Batch#updateMesh(A_Mesh)} method or
     * add it with the {@link A_Batch#addMesh(A_Mesh)} method.
     *
     * @param mesh The mesh of the subclass {@link T} that should be added or updated. <br>
     *             This mesh can not be {@code null}.
     *
     * @author Tim Kloepper
     */
    public void addOrUpdateMesh(T mesh) {
        if (_MESH_INFO.containsKey(mesh)) {
            updateMesh(mesh);

            return;
        }

        addMesh(mesh);
    }

    /**
     * <p>
     * Updates an {@link A_Mesh} of the subclass {@link T}. <br>
     * For that, it checks if either the size of the vertex or the index
     * array has changed. <br>
     * If they have not, the internal arrays as well as the gpu-side buffers
     * can just be changed at the meshes index. <br>
     * But if they have changed, this batch will first remove the mesh with {@link A_Batch#rmvMesh(A_Mesh)},
     * to then add it again with {@link A_Batch#addMesh(A_Mesh)}, this will automatically create a new {@link MeshInfo}
     * for the mesh and reduces rebuilds, as it first tries to find a free fragment and also frees
     * the old space of the mesh for a new one.
     * </p>
     * <p>
     * If the mesh is not inside this batch,
     * an {@link IllegalStateException} is thrown. <br>
     * If you are unsure, if this batch contains the mesh,
     * use the {@link A_Batch#contains(A_Mesh)} or {@link A_Batch#addOrUpdateMesh(A_Mesh)} method. <br>
     * This check is that hard, to enforce awareness about the state of your mesh.
     * </p>
     *
     * @param mesh The mesh of the subclass {@link T} that should be updated. <br>
     *             This mesh can not be {@code null}.
     *
     * @author Tim Kloepper
     */
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

    /**
     * Clears both the internal and gpu-side buffers,
     * removes all meshes and resets the pointers. <br>
     * Both instances of the {@link FragmentBinaryTree} class are cleared
     * as well.
     * Also calls {@link A_Batch#p_onFlush()} for
     * specific batches to clear additionally held data.
     * Unbinds the buffers after everything is flushed.
     *
     * @author Tim Kloepper
     */
    public void flush() {
        Arrays.fill(_VERTICES, 0, _VERTICES.length, 0);
        Arrays.fill(_INDICES, 0, _INDICES.length, 0);

        _VERTICES_FRAGMENT_TREE.clear();
        _INDICES_FRAGMENT_TREE.clear();

        glBindBuffer(GL_ARRAY_BUFFER, _VBO_ID);
        glBufferData(GL_ARRAY_BUFFER, _VERTICES, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _EBO_ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, _INDICES, GL_DYNAMIC_DRAW);

        _MESH_INFO.clear();

        p_onFlush();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Can be used by the specific batches,
     * to delete additionally stored data,
     * upon a flush happening.
     *
     * @author Tim Kloepper
     */
    protected abstract void p_onFlush();

    /**
     * Rebuilds this batch, by resetting the free-index pointers as well
     * as both instances of the {@link FragmentBinaryTree} class.
     * and letting all meshes undergo the {@link A_Batch#addMesh(A_Mesh)}
     * method again.
     * This also generates new {@link MeshInfo} for all meshes.
     *
     * @author Tim Kloepper
     */
    private void _rebuild() {
        HashSet<T> meshes;

        meshes = new HashSet<>(_MESH_INFO.keySet());

        flush();

        meshes.forEach(this::addMesh);
    }
    /**
     * Only rebuilds the index buffer, array and {@link FragmentBinaryTree}.
     * This is possible, as indices are basically references.
     * Upon rebuilding vertices we therefore automatically also need
     * to rebuild the indices, making a single rebuild vertices method unnecessary.
     *
     * @author Tim Kloepper
     */
    private void _rebuildIndices() {
        MeshInfo info;

        _INDICES_FRAGMENT_TREE.clear();

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

        _INDICES_FRAGMENT_TREE.allocate(index);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _EBO_ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, _INDICES, GL_DYNAMIC_DRAW);
    }


    // -+- RENDERING -+- //

    /**
     * Renders all meshes of the batch by binding
     * the vertex array object and using the {@link ShaderProgram},
     * to then prepare batch specific things in the {@link A_Batch#p_prepareRendering()}
     * method.
     * Finally, it calls {@code glDrawElements()} and unbinds the vertex array object,
     * any buffer and the shader.
     *
     * @author Tim Kloepper
     */
    public void render() {
        glBindVertexArray(_VAO_ID);
        _shader.use();

        p_prepareRendering();

        glDrawElements(GL_TRIANGLES, _INDICES.length, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        _shader.unuse();
    }

    /**
     * Is used by the specific batches to prepare
     * thing before {@code glDrawElements()} is called.
     * At the point of calling this function,
     * the vertex array object as well as the {@link ShaderProgram}
     * are already bound.
     *
     * @author Tim Kloepper
     */
    protected abstract void p_prepareRendering();


    // -+- SHADER MANAGEMENT -+- //

    /**
     * Sets the {@link ShaderProgram} of this batch.
     * The shader is used to render all meshes, contained
     * by this batch.
     *
     * @param shader The shader that should be set for this batch. <br>
     *               This shader can not be null.
     *
     * @author Tim Kloepper
     */
    public void setShader(ShaderProgram shader) {
        if (shader == null) throw new IllegalStateException("[BATCH ERROR] : Shader is null!");

        _shader = shader;
    }


    // -+- GETTERS -+- //

    /**
     * Returns all meshes that are contained inside this batch.
     *
     * @return The meshes contained by this batch.
     *
     * @author Tim Kloepper
     */
    public Set<T> getContainedMeshes() {
        return _MESH_INFO.keySet();
    }

    /**
     * Returns the shader that is used for rendering all the meshes inside this batch.
     *
     * @return The shader that is used by this batch.
     *
     * @author Tim Kloepper
     */
    public ShaderProgram getActiveShader() {
        return _shader;
    }


    // -+- CHECKERS -+- //

    /**
     * Checks, if this batch contains the specified {@link A_Mesh} of the subclass {@link T}.
     *
     * @param mesh The mesh of the subclass {@link T} that the checked is performed for.
     *
     * @return Whether the mesh is inside this batch, or not.
     *
     * @author Tim Kloepper
     */
    public boolean contains(T mesh) {
        return _MESH_INFO.containsKey(mesh);
    }


}


/**
 * Pools fragment nodes, to reuse them and avoid unnecessary instantiation
 * of {@link FragmentNode} objects.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
class FragmentNodePool {


    // -+- CREATION -+- //

    public FragmentNodePool() {
        _POOL = new HashSet<>();
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final HashSet<FragmentNode> _POOL;


    // -+- FRAGMENT MANAGEMENT -+- //

    public FragmentNode request(int position, int size) {
        FragmentNode fragment;

        if (_POOL.isEmpty()) fragment = new FragmentNode();
        else {
            fragment = _POOL.iterator().next();
            _POOL.remove(fragment);
        }

        fragment.init(position, size);

        return fragment;
    }
    public void giveBack(FragmentNode fragment) {
        _POOL.add(fragment);
    }


}
/**
 * A node that describes a fragment, a free internal space between allocated space,
 * by defining a size and a position. <br>
 * Is meant to be used with a {@link FragmentNodePool}, which is why it is a node and also
 * contains both a 'higher' and a 'lower' parameter to hold other fragment nodes.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
class FragmentNode {


    // -+- CREATION -+- //

    /**
     * Sets the position and the size of the fragment.
     * Also removes the children.
     *
     * @param position The index this fragment lives on. <br>
     *                 Can not be lower than {@code 0}.
     * @param size The size of the fragment. <br>
     *             Can not be lower than {@code 1}.
     *
     * @author Tim Kloepper
     */
    public void init(int position, int size) {
        if (position < 0) throw new IllegalArgumentException("[FRAGMENT ERROR] : Position can not be lower than zero, as it is the index this fragment lives on!");
        this.position = position;
        this.size = size;

        lower = null;
        higher = null;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    /**
     * The index this fragment lives on.
     */
    public int position;
    /**
     * The size of the fragment.
     */
    public int size;

    public FragmentNode lower;
    public FragmentNode higher;


    // -+- FRAGMENT MANAGEMENT -+- //

    public void clearChildren(FragmentNodePool pool) {
        if (lower != null) {
            lower.clearChildren(pool);

            pool.giveBack(lower);
        }
        if (higher != null) {
            higher.clearChildren(pool);

            pool.giveBack(higher);
        }
    }

    /**
     * <p>
     * Merges this fragment with another one,
     * by adapting the lower position and adding up the sizes of both.
     * This fragment will be used for this conversion,
     * the other one specified, will not be manipulated.
     * </p>
     * <p>
     * If you want the specified fragment to be taken care of,
     * you can use the {@link FragmentNode#merge(FragmentNode, FragmentNodePool)} method,
     * which will add the fragment to the specified pool with the {@link FragmentNodePool#giveBack(FragmentNode)} method.
     * </p>
     * <p>
     * This method checks, if the two fragments are connected with {@link FragmentNode#isConnectedWith(FragmentNode)} and will
     * throw an {@link IllegalArgumentException} if they are not connected with each other.
     * Connected means, them being direct neighbours. <br>
     * You can check this condition with the {@link FragmentNode#isConnectedWith(FragmentNode)} method.
     * </p>
     *
     * @param fragment The {@link FragmentNode} this fragment will be merged with.
     *
     * @author Tim KLoepper
     */
    public void merge(FragmentNode fragment) {
        if (!isConnectedWith(fragment)) throw new IllegalArgumentException("[FRAGMENT ERROR] : Cannot merge those fragments, as they are not connected!");

        position = Math.min(position, fragment.position);
        size += fragment.size;
    }
    /**
     * <p>
     * Merges this fragment with another one,
     * by adapting the lower position and adding up the sizes of both.
     * This fragment will be used for this conversion,
     * the other one will be added to the specified {@link FragmentNodePool} with the
     * {@link FragmentNodePool#giveBack(FragmentNode)} method.
     * </p>
     * <p>
     * If you want the specified fragment to not be manipulated,
     * please use the {@link FragmentNode#merge(FragmentNode)} method.
     * </p>
     * <p>
     * This method just wraps the {@link FragmentNode#merge(FragmentNode)} method and
     * then adds the specified fragment to the specified pool,
     * so any fail cases, existing in the other merge method, also exist here.
     * </p>
     *
     * @param fragment The {@link FragmentNode} this fragment will be merged with.
     * @param pool The {@link FragmentNodePool} the specified fragment will be added to.
     *
     * @author Tim Kloepper
     */
    public void merge(FragmentNode fragment, FragmentNodePool pool) {
        merge(fragment);

        pool.giveBack(fragment);
    }

    /**
     * <p>
     * Tries to give you a pointer to a position with a free size of the specified one,
     * for you to use freely inside the associated buffer or array. <br>
     * </p>
     * <p>
     * If the requested size is available, this method will return the current position
     * of this fragment and increase this position by the allocation size afterward. <br>
     * But if the requested size is not available, this method will return {@code -1}. <br>
     * You can check, if enough space is available with the {@link FragmentNode#canAllocate(int)} method.
     * </p>
     * <p>
     * Keep in mind, that this method can invalidate the fragment or rather reduce its size
     * to zero. <br>
     * Also, the allocation size needs to be at least {@code 1}, otherwise, this method
     * will throw an {@link IllegalArgumentException}.
     * </p>
     *
     * @param allocationSize The size you want to allocate and therefore need from this fragment.
     *                       Can not be lower than {@code 1}.
     *
     * @return The position of the allocatable space or {@code -1} if a space of that size is not available
     * inside this fragment.
     *
     * @author Tim Kloepper
     */
    public int allocate(int allocationSize) {
        if (allocationSize < 1) throw new IllegalArgumentException("[FRAGMENT ERROR] : Can not allocate an amount of zero or lower!");
        if (!canAllocate(allocationSize)) return -1;

        int allocationPosition;

        allocationPosition = position;

        position += allocationSize;
        size -= allocationSize;

        return allocationPosition;
    }

    /**
     * Adds another node as a child of this node. <br>
     * This is done by first checking the nodes' size and
     * then either add it as the lower or higher node. <br>
     * If this node is already set, then the {@link FragmentNode#addNode(FragmentNode)}
     * method of that node is called. <br>
     * This effectively creates a recursive addition structure.
     *
     * @param node The {@link FragmentNode} that should be added as a child of this node.
     *
     * @author Tim Kloepper
     */
    public void addNode(FragmentNode node) {
        if (node == this) return;

        if (node.size >= size) {
            if (higher != null) higher.addNode(node);
            else higher = node;

            return;
        }

        if (lower != null) lower.addNode(node);
        else lower = node;
    }
    /**
     * <p>
     * Removes another node from this node,
     * by checking, if either of both of its children
     * are the specified node. <br>
     * If not, this method will be called upon the children of this node
     * and if any of those return {@code true},
     * {@code true} and otherwise {@code false} is returned. <br>
     * This effectively creates a recursive removal structure.
     * </p>
     * <p>
     * This method also adds the specified node, upon finding it, to the specified {@link FragmentNodePool}
     * with the {@link FragmentNodePool#giveBack(FragmentNode)} method.
     * </p>
     *
     * @param node The {@link FragmentNode} that should be removed.
     * @param pool The {@link FragmentNodePool} that the specified node will be added to.
     *
     * @return Whether the specified node was found and removed, or not.
     *
     * @author Tim Kloepper
     */
    public boolean rmvNode(FragmentNode node, FragmentNodePool pool) {
        if (lower == node) {
            pool.giveBack(lower);
            lower = null;

            node.getChildrenAndClear().forEach(this::addNode);

            return true;
        }
        if (higher == node) {
            pool.giveBack(higher);
            higher = null;

            node.getChildrenAndClear().forEach(this::addNode);

            return true;
        }

        if (lower != null) if (lower.rmvNode(node, pool)) return true;
        if (higher != null) return higher.rmvNode(node, pool);

        return false;
    }

    /**
     * Gives back all the children nodes by recursively traversing them.
     *
     * @return All children, being of the {@link FragmentNode} class, of this node.
     *
     * @author Tim Kloepper
     */
    public HashSet<FragmentNode> getChildren() {
        HashSet<FragmentNode> result;

        result = new HashSet<>();

        if (lower != null) {
            result.add(lower);
            result.addAll(lower.getChildren());
        }
        if (higher != null) {
            result.add(higher);
            result.addAll(higher.getChildren());
        }

        return result;
    }
    /**
     * Retrieves all the children nodes by recursively traversing them and
     * then deletes them.
     *
     * @return All children, being of the {@link FragmentNode} class, of this node.
     *
     * @author Tim Kloepper
     */
    public HashSet<FragmentNode> getChildrenAndClear() {
        HashSet<FragmentNode> children;

        children = new HashSet<>();

        if (lower != null) {
            children.add(lower);
            children.addAll(lower.getChildrenAndClear());

            lower = null;
        }
        if (higher != null) {
            children.add(higher);
            children.addAll(higher.getChildrenAndClear());

            higher = null;
        }

        return children;
    }


    // -+- CHECKERS -+- //

    /**
     * Checks, if this node and the specified one are direct neighbours. <br>
     * Direct neighbour meaning no distance being between them.
     *
     * @param fragment The {@link FragmentNode} that should be checked for being the direct neighbour of this node.
     *
     * @return Whether this node and the specified one are connected.
     */
    public boolean isConnectedWith(FragmentNode fragment) {
        if (fragment == this) throw new IllegalArgumentException("[FRAGMENT ERROR] : This fragment can not be connected to itself!");

        return position + size == fragment.position || fragment.position + fragment.size == position;
    }

    /**
     * Checks, if this node can provide the specified amount of space.
     *
     * @param allocationSize The size that is requested of this node.
     *
     * @return Whether this node could provide this amount of space, or not.
     */
    public boolean canAllocate(int allocationSize) {
        return allocationSize <= size;
    }


}

class FragmentBinaryTree {


    // -+- CREATION -+- //

    public FragmentBinaryTree(int initialSize, FragmentNodePool pool) {
        _POOL = pool;
        _root = _POOL.request(0, initialSize);

        this.initialSize = initialSize;

        _NODES_BY_POSITION = new TreeMap<>();

        _NODES_BY_POSITION.put(_root.position, _root);
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final FragmentNodePool _POOL;

    private final TreeMap<Integer, FragmentNode> _NODES_BY_POSITION;

    // NON-FINALS //

    private FragmentNode _root;

    public int initialSize;


    public void addNode(FragmentNode node) {
        if (node.size == 0) return;

        node.clearChildren(_POOL);

        _NODES_BY_POSITION.put(node.position, node);

        if (_root == null) {
            _root = node;

            return;
        }

        _root.addNode(node);

        h_checkForAndConnectNeighbouredNodes();
    }
    public void rmvNode(FragmentNode node) {
        if (_root == null) return;

        _NODES_BY_POSITION.remove(node.position);

        if (node == _root) {
            HashSet<FragmentNode> children;

            children = _root.getChildren();

            _root = null;

            for (FragmentNode child : children) {
                addNode(_POOL.request(child.position, child.size));
            }

            return;
        }

        _root.rmvNode(node, _POOL);
    }


    public int allocate(int amount) {
        FragmentNode currentNode;
        int result;

        currentNode = _root;
        result = -1;

        while (currentNode != null) {
            if (!currentNode.canAllocate(amount)) {
                currentNode = currentNode.higher;
            }

            if (currentNode.lower != null) if (currentNode.lower.canAllocate(amount)) currentNode = currentNode.lower;

            rmvNode(currentNode);

            result = currentNode.allocate(amount);

            addNode(_POOL.request(currentNode.position, currentNode.size));

            currentNode = null;
        }

        return result;
    }


    public void clear() {
        _root.clearChildren(_POOL);
        _POOL.giveBack(_root);

        _NODES_BY_POSITION.clear();
    }


    private void h_checkForAndConnectNeighbouredNodes() {
        for (FragmentNode node : _NODES_BY_POSITION.values()) {
            FragmentNode up;
            FragmentNode down;

            up = null;
            down = null;

            Map.Entry<Integer, FragmentNode> entry;

            entry = _NODES_BY_POSITION.higherEntry(node.position);
            if (entry != null) up = entry.getValue();
            entry = _NODES_BY_POSITION.lowerEntry(node.position);
            if (entry != null) down = entry.getValue();

            if (up != null) {
                if (node.isConnectedWith(up))  {
                    rmvNode(node);
                    rmvNode(up);

                    node.merge(up);

                    addNode(_POOL.request(node.position, node.size));

                    return;
                }
            }
            if (down != null) {
                if (node.isConnectedWith(down))  {
                    rmvNode(node);
                    rmvNode(down);

                    node.merge(down);

                    addNode(_POOL.request(node.position, node.size));

                    return;
                }
            }
        }
    }


}