package internal.rendering.mesh;


public class TexturedMesh extends Mesh {


    /*
    Vertex Layout:
    xCoordinate, yCoordinate, xUV, yUV, textureIndex
     */


    public TexturedMesh(float[] vertices, int[] indices) {
        super(vertices, indices);
    }


    @Override
    protected boolean _areValidVertices(float[] vertices) {
        return (vertices.length % 5 == 0);
    }


}