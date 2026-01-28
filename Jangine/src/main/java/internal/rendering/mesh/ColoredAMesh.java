package internal.rendering.mesh;


public class ColoredAMesh extends A_Mesh {


    /*
    Vertex Layout:
    xCoordinate, yCoordinate, redChannel, greenChannel, blueChannel, alphaChannel
     */


    public ColoredAMesh(float[] vertices, int[] indices) {
        super(vertices, indices);
    }


    @Override
    protected boolean p_areValidVertices(float[] vertices) {
        return (vertices.length % 6 == 0);
    }


    @Override
    public int getVertexSize() {
        return 6;
    }


}