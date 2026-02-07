package internal.batch;


import internal.rendering.mesh.A_Mesh;
import internal.rendering.shader.ShaderManager;
import internal.rendering.shader.ShaderProgram;

import java.util.HashMap;


/**
 * Manages all processors, which are subclasses of the abstract class {@link A_BatchProcessor},
 * which themselves contain batches being any subclass of {@link A_Batch}.
 * You can add meshes which are any subclass of {@link A_Mesh} to be rendered every {@link BatchSystem#update}
 * call.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class BatchSystem {


    // -+- CREATION -+- //

    public BatchSystem() {
        _PROCESSORS = new HashMap<>();
        _SHADER_MANAGER = new ShaderManager();
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    /**
     * Keeps track of the processors, managing certain subclasses of {@link A_Mesh}.
     * These processors are subclasses of {@link A_BatchProcessor}.
     */
    private final HashMap<Class<? extends A_Mesh>, A_BatchProcessor<? extends A_Mesh>> _PROCESSORS;
    private final ShaderManager _SHADER_MANAGER;


    // -+- UPDATE LOOP -+- //

    /**
     * Updates all the processors.
     *
     * @author Tim Kloepper
     */
    public void update() {
        for (A_BatchProcessor<? extends A_Mesh> processor : _PROCESSORS.values()) {
            processor.update();
        }
    }


    // -+- PROCESSOR MANAGEMENT -+- //

    /**
     * Adds a subclass of the {@link A_BatchProcessor} class to the system.
     * If the subclass of the {@link A_Mesh} class that this processor wants to manage,
     * which is defined by the {@link A_BatchProcessor#p_getProcessedMeshSubclass()}
     * method, is already taken, this method will normally fail with a return value of {@code false},
     * except 'overwrite' is set to {@code true}.
     *
     * @param processor The {@link A_BatchProcessor} that should be added.
     *                  The processor can not be null.
     * @param overwrite Determines, whether a possibly already existing processor for the {@link A_Mesh}
     *                  subclass, should be overwritten, or not.
     *
     * @return Whether the addition succeeded, or not.
     *
     * @author Tim Kloepper
     */
    public boolean addProcessor(A_BatchProcessor<? extends A_Mesh> processor, boolean overwrite) {
        if (processor == null) throw new IllegalArgumentException("[BATCH SYSTEM ERROR] : The processor can not be null!");

        Class<? extends A_Mesh> meshClass;

        meshClass = processor.p_getProcessedMeshSubclass();

        if (_PROCESSORS.containsKey(meshClass) && !overwrite) return false;

        _PROCESSORS.put(meshClass, processor);

        return true;
    }
    /**
     * <p>
     * Removes a subclass of the {@link A_BatchProcessor} class from the system.
     * </p>
     * <p>
     * If the removal fails, because the processor is not inside this system,
     * {@code false} is returned.
     * </p>
     *
     * @param processor The {@link A_BatchProcessor} that should be removed.
     *                  The processor can not be null.
     *
     * @return Whether the removal was successful, or not.
     *
     * @author Tim Kloepper
     */
    public boolean rmvProcessor(A_BatchProcessor<? extends A_Mesh> processor) {
        if (processor == null) throw new IllegalArgumentException("[BATCH SYSTEM ERROR] : The processor can not be null!");

        Class<? extends A_Mesh> meshClass;

        if (!_PROCESSORS.containsValue(processor)) return false;
        meshClass = processor.p_getProcessedMeshSubclass();

        _PROCESSORS.remove(meshClass);

        return true;
    }


    // -+- MESH MANAGEMENT -+- //

    /**
     * Adds an {@link A_Mesh} to the correct {@link A_BatchProcessor}.
     * This processor is automatically found,
     * but if no processor is registered for the class of this mesh,
     * the method will return {@code false}.
     *
     * @param mesh The {@link A_Mesh} object, that should be added.
     *             The mesh can not be null.
     * @param shader The {@link ShaderProgram}, that should be used, to render this mesh.
     *               The shader can not be null.
     *
     * @return Whether the addition was successful, or not.
     *
     * @author Tim Kloepper
     */
    public boolean addMesh(A_Mesh mesh, String shaderPath) {
        if (mesh == null) throw new IllegalArgumentException("[BATCH SYSTEM ERROR] : The mesh can not be null!");

        ShaderProgram shader;

        shader = _SHADER_MANAGER.load(shaderPath);

        A_BatchProcessor<? extends A_Mesh> processor;

        processor = _PROCESSORS.get(mesh.getClass());
        if (processor == null) return false;

        return processor.addMesh(mesh, shader);
    }
    /**
     * Removes an {@link A_Mesh} from the holding {@link A_BatchProcessor}.
     * The correct processor is automatically found,
     * but if not processor is registered for the class of this mesh,
     * the method will return {@code false}.
     *
     * @param mesh The {@link A_Mesh} object, that should be removed.
     *             The mesh can not be null.
     *
     * @return Whether the removal was successful, or not.
     *
     * @author Tim Kloepper
     */
    public boolean rmvMesh(A_Mesh mesh) {
        if (mesh == null) throw new IllegalArgumentException("[BATCH SYSTEM ERROR] : The mesh can not be null!");

        A_BatchProcessor<? extends A_Mesh> processor;

        processor = _PROCESSORS.get(mesh.getClass());
        if (processor == null) return false;

        return processor.rmvMesh(mesh);
    }

    public boolean updateMesh(A_Mesh mesh) {
        if (mesh == null) throw new IllegalArgumentException("[BATCH SYSTEM ERROR] : The mesh can not be null!");

        A_BatchProcessor<? extends A_Mesh> processor;

        processor = _PROCESSORS.get(mesh.getClass());
        if (processor == null) return false;

        return processor.updateMesh(mesh);
    }
    public boolean updateMesh(A_Mesh mesh, String shaderPath) {
        if (mesh == null) throw new IllegalArgumentException("[BATCH SYSTEM ERROR] : The mesh can not be null!");

        ShaderProgram shader;

        shader = _SHADER_MANAGER.load(shaderPath);

        A_BatchProcessor<? extends A_Mesh> processor;

        processor = _PROCESSORS.get(mesh.getClass());
        if (processor == null) return false;

        return processor.updateMesh(mesh, shader);
    }


    // -+- GETTERS -+- //

    /**
     * Returns the {@link A_BatchProcessor} processing the class of this {@link A_Mesh}.
     *
     * @param meshClass The subclass of the {@link A_Mesh} class, that you want to find the processor for.
     *                  The class can not be null.
     *
     * @return The {@link A_BatchProcessor} managing the specified subclass of the {@link A_Mesh} class.
     *
     * @author Tim Kloepper
     */
    public A_BatchProcessor<? extends A_Mesh> getProcessorForMeshClass(Class<? extends A_Mesh> meshClass) {
        if (meshClass == null) throw new IllegalArgumentException("[BATCH SYSTEM ERROR] : The mesh class can not be null!");

        return _PROCESSORS.get(meshClass);
    }


    // -+- CHECKERS -+- //

    /**
     * Checks, whether this system contains a {@link A_BatchProcessor} for the specified subclass of the
     * {@link A_Mesh} class.
     *
     * @param meshClass The subclass of the {@link A_Mesh} class.
     *
     * @return Whether this system can handle this subclass of the {@link A_Mesh} class.
     *
     * @author Tim Kloepper
     */
    public boolean containsProcessorForMeshClass(Class<? extends A_Mesh> meshClass) {
        return getProcessorForMeshClass(meshClass) != null;
    }


}