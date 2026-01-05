package internal.rendering.mesh;


import internal.rendering.JangineShaderProgram;


// A mesh defines vertices and how to interpret them.
// Those vertices consist of a position and a uv-coordinate.
public abstract class JangineMesh {


    protected float[] _vertices;
    protected int[] _indices;

    protected int _vaoID, _vboID, _eboID;


    public int getVertexArrayObjectID() {
        return _vaoID;
    }

    public int getVerticesSize() {
        return _vertices.length;
    }
    public int getIndicesSize() {
        return _indices.length;
    }


}