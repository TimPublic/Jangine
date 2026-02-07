package internal.batch;


import internal.rendering.camera.Camera2D;
import internal.rendering.mesh.A_Mesh;
import internal.rendering.shader.ShaderProgram;

import java.util.HashMap;


/**
 * Manages a specific subclass of the {@link A_Mesh} class and
 * orders batches which are subclasses of the {@link A_Batch} class,
 * by shaders of the {@link ShaderProgram} class. <br>
 * Basically holds batches and fills them with meshes,
 * to render them on every {@link A_BatchProcessor#update(Camera2D)} call.
 *
 * @param <T> The subclass of the {@link A_Mesh} the specific batch processor,
 *           will manage.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public abstract class A_BatchProcessor<T extends A_Mesh> {


    // -+- CREATION -+- //

    public A_BatchProcessor() {
        _BATCHES = new HashMap<>();
        _SHADER_PER_MESH = new HashMap<>();
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    private final HashMap<ShaderProgram, A_Batch<T>> _BATCHES;
    private final HashMap<T, ShaderProgram> _SHADER_PER_MESH;


    // -+- UPDATE LOOP -+- //

    /**
     * Renders all the batches.
     *
     * @author Tim Kloepper
     */
    public void update(Camera2D camera) {
        for (A_Batch<T> batch : _BATCHES.values()) {
            batch.render(camera);
        }
    }


    // -+- MESH MANAGEMENT -+- //

    /**
     * Adds an {@link A_Mesh}, which has to be of the class {@link T}, to this processor.
     * The mesh will automatically put into the right {@link A_Batch}, based on the {@link ShaderProgram}.
     * If there is no {@link A_Batch} for the specified shader yet, one will be created.
     * If addition should fail inside {@link A_Batch#addMesh(A_Mesh)} for any reason,
     * {@code false} is returned.
     * This method will only throw an {@link IllegalArgumentException}, if the mesh is null,
     * the shader is null.
     * A {@link ClassCastException} is thrown, if the mesh is not of the {@link T} class.
     *
     * @param mesh The object of the {@link T} class, that should be added.
     *             The mesh can not be null.
     * @param shader The {@link ShaderProgram} that the specified mesh should be rendered with.
     *               The shader can not be null or not be of the {@link T} class.
     *
     * @return Whether the addition was a success, or not.
     *
     * @author Tim Kloepper
     */
    public boolean addMesh(A_Mesh mesh, ShaderProgram shader) {
        if (mesh == null) throw new IllegalArgumentException("[BATCH PROCESSOR ERROR]: Mesh can not be null!");
        if (shader == null) throw new IllegalArgumentException("[BATCH PROCESSOR ERROR] : Shader can not be null!");

        // If no batch for this shader exists, create one.
        if (!_BATCHES.containsKey(shader)) _BATCHES.put(shader, p_createBatch(shader));

        boolean result;

        result = _BATCHES.get(shader).addMesh((T) mesh);
        if (result) _SHADER_PER_MESH.put((T) mesh, shader);

        return result;
    }
    /**
     * Removes an {@link A_Mesh}, which has to be of the class {@link T}, from this processor.
     * If the removal inside {@link A_Batch#rmvMesh(A_Mesh)} fails for any reason, {@code false}
     * is returned. <br>
     * This method will only throw an {@link IllegalArgumentException}, if the mesh is null.
     * A {@link ClassCastException} is thrown, if the mesh is not of the {@link T} class.
     *
     * @param mesh The object of the {@link T} class, that should be added.
     *             The mesh can not be null or not be of the {@link T} class.
     *
     * @return Whether the removal was a success, or not.
     *
     * @author Tim Kloepper
     */
    public boolean rmvMesh(A_Mesh mesh) {
        if (mesh == null) throw new IllegalArgumentException("[BATCH PROCESSOR ERROR]: Mesh can not be null!");

        ShaderProgram shader;

        // Retrieve the shader, this mesh is rendered with.
        shader = _SHADER_PER_MESH.get((T) mesh);
        if (shader == null) throw new IllegalArgumentException("[BATCH PROCESSOR ERROR] : Mesh is not part of any of the batches in this processor!");

        A_Batch<T> batch;

        // Retrieve the batch responsible for this shader.
        batch = _BATCHES.get(shader);
        if (batch == null) throw new IllegalStateException("[BATCH PROCESSOR ERROR] : No batch registered for that shader!");

        boolean result;

        result = batch.rmvMesh((T) mesh);

        // Remove from registry.
        _SHADER_PER_MESH.remove((T) mesh);

        return result;
    }

    public boolean updateMesh(A_Mesh mesh) {
        if (mesh == null) throw new IllegalArgumentException("[BATCH PROCESSOR ERROR]: Mesh can not be null!");

        if (!_SHADER_PER_MESH.containsKey((T) mesh)) return false;

        _BATCHES.get(_SHADER_PER_MESH.get((T) mesh)).updateMesh((T) mesh);

        return true;
    }
    public boolean updateMesh(A_Mesh mesh, ShaderProgram shader) {
        if (mesh == null) throw new IllegalArgumentException("[BATCH PROCESSOR ERROR]: Mesh can not be null!");
        if (shader == null) throw new IllegalArgumentException("[BATCH PROCESSOR ERROR] : Shader can not be null!");

        ShaderProgram currentShader;

        currentShader = _SHADER_PER_MESH.get((T) mesh);

        if (currentShader == null) return false;
        if (currentShader == shader) return false;

        _SHADER_PER_MESH.put((T) mesh, shader);

        _BATCHES.get(currentShader).rmvMesh((T) mesh);

        return _BATCHES.get(shader).addMesh((T) mesh);
    }


    // -+- BATCH MANAGEMENT -+- //

    /**
     * Is meant to be overwritten by the specific batch processors,
     * in order to produce correct batches for every batch processor,
     * without letting every processor rewrite the same mesh adding
     * and removing logic.
     *
     * @return An {@link A_Batch}, fit for the specific batch processor.
     *
     * @author Tim Kloepper
     */
    protected abstract A_Batch<T> p_createBatch(ShaderProgram shader);


    // -+- GETTERS -+- //

    /**
     * Is meant to be overwritten by the specific batch processor,
     * in order to provide the subclass of the {@link A_Mesh} class,
     * all the batches of this processor should manage.
     *
     * @return The subclass of the {@link A_Mesh} this processor will manage.
     *
     * @author Tim Kloepper
     */
    protected abstract Class<? extends A_Mesh> p_getProcessedMeshSubclass();


}