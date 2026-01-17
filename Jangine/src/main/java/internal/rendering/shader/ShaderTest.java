package internal.rendering.shader;


import internal.rendering.camera.Camera2D;
import internal.rendering.batch.TexturedRenderBatch;
import internal.rendering.mesh.TexturedMesh;
import internal.rendering.texture.JangineTexture;
import internal.rendering.texture.dependencies.implementations.STBI_TextureLoader;


public class ShaderTest {


    ShaderProgram shaderProgram;
    Camera2D camera;

    TexturedRenderBatch batch;

    TexturedMesh firstMesh;
    TexturedMesh secondMesh;

    JangineTexture firstTexture;
    JangineTexture secondTexture;


    public ShaderTest() {
        shaderProgram = new ShaderProgram("assets/default.glsl");
        camera = new Camera2D(40, 21);

        batch = new TexturedRenderBatch(shaderProgram, camera);

        camera._position.y -= 50;

        firstTexture = new JangineTexture("assets/test_image.png", new STBI_TextureLoader());
        secondTexture = new JangineTexture("assets/ui.png", new STBI_TextureLoader());

        float[] vertices = new float[]{
                /* Position */    /* uvCoordinates */  /* textureIndex */
                  0.0f,   0.0f,   0.0f, 0.0f,          0.0f,
                100.0f,   0.0f,   1.0f, 0.0f,          1.0f,
                100.0f, 100.0f,   1.0f, 1.0f,          1.0f,
                  0.0f, 100.0f,   0.0f, 1.0f,          0.0f,
        };
        int[] indices = new int[]{
                0, 1, 2,
                2, 3, 0,
        };

        float[] secondVertices = new float[]{
                /* Position */    /* uvCoordinates */  /* textureIndex */
                100.0f,   100.0f,   0.0f, 0.0f,          0.0f,
                200.0f,   100.0f,   1.0f, 0.0f,          1.0f,
                200.0f, 200.0f,   1.0f, 1.0f,          1.0f,
                100.0f, 200.0f,   0.0f, 1.0f,          0.0f,
        };

        firstMesh = new TexturedMesh(vertices, indices);
        secondMesh = new TexturedMesh(secondVertices, indices);

        batch.addMesh(firstMesh, firstTexture);
        batch.addMesh(secondMesh, secondTexture);
    }


    public void run() {
        batch.update();

        firstMesh.vertices[0] += 1.0f;
        secondMesh.vertices[0] -= 1.0f;

        batch.updateMesh(firstMesh, firstTexture);
        batch.updateMesh(secondMesh, secondTexture);
    }


}