package internal.rendering.mesh;


public class ColoredMesh extends Mesh {


    /*
    Vertex Layout:
    xCoordinate, yCoordinate, redChannel, greenChannel, blueChannel, alphaChannel
     */


    public ColoredMesh(float[] vertices, int[] indices) {
        super(vertices, indices);
    }


    @Override
    protected boolean _areValidVertices(float[] vertices) {
        return (vertices.length % 6 == 0);
    }


    public void changeRed(double ) {
        for (int index = 2; index < vertices.length; index += 6) {

        }
    }


}