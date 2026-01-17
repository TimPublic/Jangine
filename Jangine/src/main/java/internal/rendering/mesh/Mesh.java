package internal.rendering.mesh;


import java.util.Arrays;


public abstract class Mesh {


    public Mesh(float[] vertices, int[] indices) {
        if (!(_areValidVertices(vertices) && _areValidIndices(indices))) {
            System.err.println("[MESH ERROR] : Tried to provide invalid vertices or indices!");
            System.err.println("|-> Vertices : " + Arrays.toString(vertices));
            System.err.println("|-> Indices : " + Arrays.toString(indices));

            System.exit(1);
        }

        this.vertices = vertices;
        this.indices = indices;
    }


    public float[] vertices;
    public int[] indices;


    private boolean _areValidIndices(int[] indices) {
        return (indices.length % 3 == 0);
    }


    protected abstract boolean _areValidVertices(float[] vertices);


}