package internal.rendering.mock;


import java.util.ArrayList;


public class JangineMesh {


    private float[] _vertices;
    private int[] _indices;


    public JangineMesh(float[] vertices, int[] indices) {
        _vertices = vertices;
        _indices = indices;
    }


    public float[] getVertices() {
        return _vertices;
    }
    public int[] getIndices() {
        return _indices;
    }


}