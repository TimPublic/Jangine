package internal.rendering;


import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


public class ShaderTest {


    public void run() {
        String vertexShaderSource = "#version 330 core\n" +
                "\n" +
                "\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "layout (location = 1) in vec4 aColor;\n" +
                "\n" +
                "\n" +
                "out vec4 fColor;\n" +
                "\n" +
                "\n" +
                "void main() {\n" +
                "    fColor = aColor;\n" +
                "\n" +
                "    gl_Position = vec4(aPos, 1.0);\n" +
                "}";
        String fragmentShaderSource = "#version 330 core\n" +
                "\n" +
                "\n" +
                "in vec4 fColor;\n" +
                "\n" +
                "\n" +
                "out vec4 color;\n" +
                "\n" +
                "\n" +
                "void main() {\n" +
                "    color = fColor;\n" +
                "}";

        int vertexID, fragmentID, programID;

        // Load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexShaderSource); // -> Pass this source, to this shader!
        glCompileShader(vertexID);

        // Check for errors
        int success;

        success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int stringLenght;

            stringLenght = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);

            System.err.println("[GL_ERROR] : Error while compiling vertex-shader!");
            System.err.println("|-> " + glGetShaderInfoLog(vertexID, stringLenght));

            System.exit(1);
        }

        // Load and compile fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentShaderSource);
        glCompileShader(fragmentID);

        // Check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int stringLenght;

            stringLenght = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);

            System.err.println("[GL_ERROR] : Error while compiling fragment-shader!");
            System.err.println("|-> " + glGetShaderInfoLog(fragmentID, stringLenght));

            System.exit(1);
        }

        // Link shaders and check for errors
        programID = glCreateProgram();
        glAttachShader(programID, vertexID);
        glAttachShader(programID, fragmentID);
        glLinkProgram(programID);

        // Check for errors
        success = glGetProgrami(programID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int stringLenght;

            stringLenght = glGetProgrami(programID, GL_INFO_LOG_LENGTH);

            System.err.println("[GL_ERROR] : Error while linking program!");
            System.err.println("|-> " + glGetProgramInfoLog(programID, stringLenght));

            System.exit(1);
        }

        float[] vertexArray = {
                /* Position */  0.5f, -0.5f, 0.0f, /* Color */ 1.0f, 0.0f, 0.0f, 1.0f, // Bottom-Right
                /* Position */ -0.5f,  0.5f, 0.0f, /* Color */ 0.0f, 1.0f, 0.0f, 1.0f, // Top-Left
                /* Position */  0.5f,  0.5f, 0.0f, /* Color */ 0.0f, 0.0f, 1.0f, 1.0f, // Top-Right
                /* Position */ -0.5f, -0.5f, 0.0f, /* Color */ 1.0f, 1.0f, 0.0f, 1.0f, // Bottom-Left
        };

        // Must be in counter-clockwise order.
        int[] elementArray = {
                0, 2, 1, // Top-Right triangle
                0, 1, 3, // Bottom-Left triangle
        };

        int vertexArrayObjectID, vertexBufferObjectID, elementBufferObjectID;

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

        // Add vertex attribute pointers.
        int positionsSize;
        int colorSize;

        int floatSize;

        int vertexSizeBytes;

        positionsSize = 3;
        colorSize = 4;

        floatSize = 4;

        vertexSizeBytes = (positionsSize + colorSize) * floatSize;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSize);
        glEnableVertexAttribArray(1);

        // Bind shader program.
        glUseProgram(programID);

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

        glUseProgram(0);
    }


}