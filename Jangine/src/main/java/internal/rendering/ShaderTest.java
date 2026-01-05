package internal.rendering;


import internal.rendering.texture.JangineTexture;
import internal.rendering.texture.dependencies.implementations.STBI_TextureLoader;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class ShaderTest {


    float[] vertexArray = {
            /* Position */ 100.5f,     0f, 0.0f, /* Color */ 1.0f, 0.0f, 0.0f, 1.0f, /* UV-Coordinates */ 0.5f, 0f, // Bottom-Right
            /* Position */     0f, 100.5f, 0.0f, /* Color */ 0.0f, 1.0f, 0.0f, 1.0f, /* UV-Coordinates */ 0f, 0.5f, // Top-Left
            /* Position */ 100.5f, 100.5f, 0.0f, /* Color */ 0.0f, 0.0f, 1.0f, 1.0f, /* UV-Coordinates */ 0.5f, 0.5f, // Top-Right
            /* Position */     0f,     0f, 0.0f, /* Color */ 1.0f, 1.0f, 0.0f, 1.0f, /* UV-Coordinates */ 0f, 0f, // Bottom-Left
    };

    // Must be in counter-clockwise order.
    int[] elementArray = {
            0, 2, 1, // Top-Right triangle
            0, 1, 3, // Bottom-Left triangle
    };

    int vertexArrayObjectID, vertexBufferObjectID, elementBufferObjectID;


    JangineShaderProgram jangineShaderProgram;
    JangineTexture jangineTexture;
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


        jangineShaderProgram = new JangineShaderProgram("assets/default.glsl");
        jangineTexture = new JangineTexture("assets/test_image.png", STBI_TextureLoader.get());
        camera = new JangineCamera2D(40, 21);

        camera._position.y -= 50;
    }


    public void run() {
        // Add vertex attribute pointers.
        int positionsSize;
        int colorSize;
        int uvCoordinatesSize;

        int vertexSizeBytes;

        positionsSize = 3;
        colorSize = 4;
        uvCoordinatesSize = 2;

        vertexSizeBytes = (positionsSize + colorSize + uvCoordinatesSize) * Float.BYTES;

        // Bind jangineShaderProgram program.
        jangineShaderProgram.use();

        glActiveTexture(GL_TEXTURE0);
        jangineTexture.bind();

        jangineShaderProgram.upload("TEXTURE_SAMPLER", 0);
        jangineShaderProgram.upload("uProjectionMatrix", camera.getProjectionMatrix());
        jangineShaderProgram.upload("uViewMatrix", camera.getViewMatrix());

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, uvCoordinatesSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);

        // Bind vertex array.
        glBindVertexArray(vertexArrayObjectID);

        // Enable the vertex-attribute pointes.
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        // Draw.
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything.
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        jangineShaderProgram.unuse();
    }


}