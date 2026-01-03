package internal.rendering;


import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class ShaderTest {


    float[] vertexArray = {
            /* Position */  100.5f, -100.5f, 0.0f, /* Color */ 1.0f, 0.0f, 0.0f, 1.0f, // Bottom-Right
            /* Position */ -100.5f,  100.5f, 0.0f, /* Color */ 0.0f, 1.0f, 0.0f, 1.0f, // Top-Left
            /* Position */  100.5f,  100.5f, 0.0f, /* Color */ 0.0f, 0.0f, 1.0f, 1.0f, // Top-Right
            /* Position */ -100.5f, -100.5f, 0.0f, /* Color */ 1.0f, 1.0f, 0.0f, 1.0f, // Bottom-Left
    };

    // Must be in counter-clockwise order.
    int[] elementArray = {
            0, 2, 1, // Top-Right triangle
            0, 1, 3, // Bottom-Left triangle
    };

    int vertexArrayObjectID, vertexBufferObjectID, elementBufferObjectID;


    JangineShader jangineShader;
    JangineCamera2D camera;


    public ShaderTest() {
        vertexArrayObjectID = glGenVertexArrays();
        glBindVertexArray(vertexArrayObjectID); // Every action on vertex arrays now apply to this object.

        // Create a float-buffer of vertices.
        FloatBuffer vertexBuffer;
        vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip(); // <- Flip is important!

        // Create vertex-buffer-object.
        vertexBufferObjectID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObjectID); // Every action on buffers now apply to this object.
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW); // Static draw means, that we do not change the buffer.

        // Create indices and upload.
        IntBuffer elementBuffer;
        elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        elementBufferObjectID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferObjectID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);


        jangineShader = new JangineShader("assets/default.glsl");
        camera = new JangineCamera2D(40, 21);
    }


    public void run() {
        // Add vertex attribute pointers.
        int positionsSize;
        int colorSize;

        int floatSize;

        int vertexSizeBytes;

        positionsSize = 3;
        colorSize = 4;

        floatSize = 4;

        vertexSizeBytes = (positionsSize + colorSize) * floatSize;

        camera._position.x -= 50.0f;

        // Bind jangineShader program.
        jangineShader.use();
        jangineShader.upload("uProjectionMatrix", camera.getProjectionMatrix());
        jangineShader.upload("uViewMatrix", camera.getViewMatrix());

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSize);
        glEnableVertexAttribArray(1);

        // Bind vertex array.
        glBindVertexArray(vertexArrayObjectID);

        // Enable the vertex-attribute pointes.
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw.
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything.
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        jangineShader.unuse();
    }


}