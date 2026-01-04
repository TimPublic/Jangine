package internal.rendering;


import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.lwjgl.opengl.GL20.*;


// On creation a path to a dlsl-file needs to be specified.
// The shader scans this file for a vertex and a fragment shader,
// marked by '#type vertex' and '#type fragment'.
// If any of those are missing, the engine crashes.
// Then it compiles itself to a program which can then be used and unused
// with the use- and unuse-method calls.
public class JangineShader {


    public final String VERTEX_TYPE_IDENTIFIER = "vertex";
    public final String FRAGMENT_TYPE_IDENTIFIER = "fragment";


    private String _vertexShaderSource;
    private String _fragmentShaderSource;

    private int _vertexShaderID;
    private int _fragmentShaderID;

    private int _programID;


    public JangineShader(String filePath) {
        _retrieveShadersFromPath(filePath);

        _compileShaders();

        _createAndLinkProgram();
    }


    // -+- USE-STATE MANAGEMENT -+- //

    // Sets the used program of GLFW to this shader.
    public void use() {
        glUseProgram(_programID);
    }
    // Sets the used program of GLFW to 0, which means, that no program is used.
    public void unuse() {
        glUseProgram(0);
    }


    // -+- SHADER-RETRIEVAL -+- //

    // Retrieves the shader-string form the file at the specified path.
    // Then filters out the two separate shaders and saves them.
    private void _retrieveShadersFromPath(String path) {
        if (!path.endsWith(".glsl")) {
            System.err.println("[SHADER ERROR] : Given shader file is not of '.glsl!'");

            System.exit(1);
        }

        try {
            String fileContents;

            fileContents = new String(Files.readAllBytes(Paths.get(path)));

            _retrieveShadersFromString(fileContents);
        }
        catch (IOException exception) {
            System.err.println("[SHADER ERROR] : Could not open file!");
            System.err.println("|-> For file-path : " + path);
            System.err.println("|-> Exception : " + exception);
            System.err.println("|-> Stack-Trace : " + Arrays.toString(exception.getStackTrace()));

            System.exit(1);
        }

        if (_vertexShaderSource == null) {
            System.err.println("[SHADER ERROR] : Vertex-JangineShader could not be found!");
            System.err.println("|-> In file : " + path);

            System.exit(1);
        }
        if (_fragmentShaderSource == null) {
            System.err.println("[SHADER ERROR] : Fragment-JangineShader could not be found!");
            System.err.println("|-> In file : " + path);

            System.exit(1);
        }
    }
    // Filters the two separate shaders from the string and saves them.
    private void _retrieveShadersFromString(String from) {
        String[] splitContents = from.split("(#type)( )+");

        for (String shader : splitContents) {
            if (shader.startsWith(VERTEX_TYPE_IDENTIFIER)) {
                _vertexShaderSource = shader.replace(VERTEX_TYPE_IDENTIFIER, "").strip();
                continue;
            }
            if (shader.startsWith(FRAGMENT_TYPE_IDENTIFIER)) {
                _fragmentShaderSource = shader.replace(FRAGMENT_TYPE_IDENTIFIER, "").strip();
            }
        }
    }


    // -+- COMPILING -+- //

    // Compiles both shaders.
    private void _compileShaders() {
        // Vertex
        _vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(_vertexShaderID, _vertexShaderSource);
        glCompileShader(_vertexShaderID);

        _checkForShaderCompileErrors(_vertexShaderID);

        // Fragment
        _fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(_fragmentShaderID, _fragmentShaderSource);
        glCompileShader(_fragmentShaderID);

        _checkForShaderCompileErrors(_fragmentShaderID);
    }
    // Checks for shader compile-errors.
    private void _checkForShaderCompileErrors(int shaderID) {
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) != GL_FALSE) {return;}

        int logLenght;

        logLenght = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH);

        System.err.println("[SHADER ERROR] : Error while compiling shader!");
        System.err.println("|-> Info-Log : " + glGetShaderInfoLog(shaderID, logLenght));

        System.exit(1);
    }


    // -+- PROGRAM CREATION AND LINKING -+- //

    // Creates the actual program and links the shaders.
    private void _createAndLinkProgram() {
        _programID = glCreateProgram();

        glAttachShader(_programID, _vertexShaderID);
        glAttachShader(_programID, _fragmentShaderID);

        glLinkProgram(_programID);

        _checkForProgramLinkingErrors(_programID);
    }
    // Checks for linking-errors.
    private void _checkForProgramLinkingErrors(int programID) {
        if (glGetProgrami(programID, GL_LINK_STATUS) != GL_FALSE) {return;}

        int logLenght;

        logLenght = glGetProgrami(programID, GL_INFO_LOG_LENGTH);

        System.err.println("[GL_ERROR] : Error while linking program!");
        System.err.println("|-> " + glGetProgramInfoLog(programID, logLenght));

        System.exit(1);
    }


    // -+- UNIFORMS -+- //

    // Uploads an integer-uniform to the shader.
    // If the name of the uniform is invalid, the
    // engine will crash.
    public void upload(String name, int value) {
        use();

        glUniform1i(_getUniformPosition(name), value);
    }
    // Uploads a float-uniform to the shader.
    // If the name of the uniform is invalid, the
    // engine will crash.
    public void upload(String name, float value) {
        use();

        glUniform1f(_getUniformPosition(name), value);
    }
    // Uploads a matrix2f-uniform to the shader.
    // If the name of the uniform is invalid, the
    // engine will crash.
    public void upload(String name, Matrix2f matrix2f) {
        use();

        FloatBuffer matrixBuffer;

        matrixBuffer = BufferUtils.createFloatBuffer(4);
        matrix2f.get(matrixBuffer);

        glUniformMatrix2fv(_getUniformPosition(name), false, matrixBuffer);
    }
    // Uploads a matrix3f-uniform to the shader.
    // If the name of the uniform is invalid, the
    // engine will crash.
    public void upload(String name, Matrix3f matrix3f) {
        use();

        FloatBuffer matrixBuffer;

        matrixBuffer = BufferUtils.createFloatBuffer(9);
        matrix3f.get(matrixBuffer);

        glUniformMatrix3fv(_getUniformPosition(name), false, matrixBuffer);
    }
    // Uploads a matrix4f-uniform to the shader.
    // If the name of the uniform is invalid, the
    // engine will crash.
    public void upload(String name, Matrix4f matrix4f) {
        use();

        FloatBuffer matrixBuffer; // Flat-out the buffer.

        matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix4f.get(matrixBuffer);

        glUniformMatrix4fv(_getUniformPosition(name), false, matrixBuffer);
    }
    // Uploads a vector2f-uniform to the shader.
    // If the name of the uniform is invalid, the
    // engine will crash.
    public void upload(String name, Vector2f vector2f) {
        use();

        glUniform2f(_getUniformPosition(name), vector2f.x, vector2f.y);
    }
    // Uploads a vector3f-uniform to the shader.
    // If the name of the uniform is invalid, the
    // engine will crash.
    public void upload(String name, Vector3f vector3f) {
        use();

        glUniform3f(_getUniformPosition(name), vector3f.x, vector3f.y, vector3f.z);
    }
    // Uploads a vector4f-uniform to the shader.
    // If the name of the uniform is invalid, the
    // engine will crash.
    public void upload(String name, Vector4f vector4f) {
        use();

        glUniform4f(_getUniformPosition(name), vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }

    // Retrieves the internal shader uniform-position.
    // If the name is invalid, the engine crashes.
    // The name is stripped of all leading and trailing whitespaces.
    private int _getUniformPosition(String name) {
        int position;

        name = name.strip();

        position = glGetUniformLocation(_programID, name);

        if (position != -1) {return position;}

        System.err.println("[SHADER ERROR] : JangineShader-Program does not contain uniform!");
        System.err.println("|-> Uniform-Name : " + name);

        System.exit(1);

        return -1;
    }


}