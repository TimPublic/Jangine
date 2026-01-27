package internal.rendering.mesh;


import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;


public abstract class A_Mesh {


    public A_Mesh(float[] vertices, int[] indices) {
        if (!(p_areValidVertices(vertices) && _areValidIndices(indices))) {
            try {
                throw new InvalidPropertiesFormatException(
                        "[MESH ERROR] : Tried to provide invalid vertices or indices!\n" +
                        "|-> Vertices : " + Arrays.toString(vertices) + "\n" +
                        "|-> Vertices Size : " + vertices.length + "\n" +
                        "|-> Expected Multiple Of : Please look into the class\n" +
                        "|-> Indices : " + Arrays.toString(indices) + "\n" +
                        "|-> Indices Size : " + indices.length + "\n" +
                        "|-> Expected Multiple Of : " + 3 + "\n"
                );
            } catch (InvalidPropertiesFormatException e) {
                throw new RuntimeException(e);
            }
        }

        this.vertices = vertices;
        this.indices = indices;
    }


    public float[] vertices;
    public int[] indices;


    private boolean _areValidIndices(int[] indices) {
        return (indices.length % 3 == 0);
    }


    protected abstract boolean p_areValidVertices(float[] vertices);


}