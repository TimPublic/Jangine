package internal.rendering.mesh;


public class TexturedAMesh extends A_Mesh {


    /*
    Vertex Layout:
    xCoordinate, yCoordinate, xUV, yUV, textureIndex
     */


    public TexturedAMesh(float[] vertices, int[] indices) {
        super(vertices, indices);
    }


    @Override
    protected boolean p_areValidVertices(float[] vertices) {
        return (vertices.length % 5 == 0);
    }


}