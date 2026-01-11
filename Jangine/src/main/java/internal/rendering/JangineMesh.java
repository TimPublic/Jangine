package internal.rendering;


/**
 * Contains vertices and indices, describing how to draw them.
 * This mesh can be rendered and is intended to be rendered
 * with a {@link internal.rendering.JangineRenderBatch}.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class JangineMesh {


    private float[] _vertices;
    private int[] _indices;


    public JangineMesh(float[] vertices, int[] indices) {
        _vertices = vertices;
        _indices = indices;
    }


    // -+- GETTERS -+- //

    /**
     * Returns this meshes' vertices.
     *
     * @return vertices
     *
     * @author Tim Kloepper
     */
    public float[] getVertices() {
        return _vertices;
    }

    /**
     * Returns this meshes' indices.
     *
     * @return indices
     *
     * @author Tim Kloepper
     */
    public int[] getIndices() {
        return _indices;
    }


}