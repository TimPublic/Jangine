package internal.rendering.mesh;


import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


// A quad-mesh is a mesh, consisting of four vertices,
// making up a square, made out of two triangles, defined
// by the indices.
public class JangineQuadMesh extends JangineMesh {


    public JangineQuadMesh() {
        _vertices = new float[]{
                /* Position       UV-Coordinates */
                   0.0f,  0.0f,   0.0f, 0.0f,    /* <- Bottom-Left  */
                   1.0f,  0.0f,   1.0f, 0.0f,    /* <- Bottom-Right */
                   1.0f,  1.0f,   1.0f, 1.0f,    /* <- Top-Right    */
                   0.0f,  1.0f,   0.0f, 1.0f,    /* <- Top-Left     */
        };
        _indices = new int[]{
                /* Bottom-Right triangle */ 0, 1, 2,
                /* Top-Left triangle     */ 2, 3, 0,
        };

        _vaoID = glGenVertexArrays();
        glBindVertexArray(_vaoID);

        FloatBuffer vertexBuffer;

        vertexBuffer = BufferUtils.createFloatBuffer(_vertices.length);
        vertexBuffer.put(_vertices).flip();

        _vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, _vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer indexBuffer;

        indexBuffer = BufferUtils.createIntBuffer(_indices.length);
        indexBuffer.put(_indices).flip();

        _eboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, _eboID);
        glBufferData(GL_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
    }


}