package internal.rendering;


import internal.rendering.texture.JangineTexture;
import internal.rendering.texture.dependencies.implementations.STBI_TextureLoader;


public class ShaderTest {


    JangineShaderProgram jangineShaderProgram;
    JangineTexture jangineTexture;
    JangineCamera2D camera;

    JangineRenderBatch batch;

    JangineMesh firstMesh;


    public ShaderTest() {
        jangineShaderProgram = new JangineShaderProgram("assets/default.glsl");
        jangineTexture = new JangineTexture("assets/test_image.png", STBI_TextureLoader.get());
        camera = new JangineCamera2D(40, 21);

        batch = new JangineRenderBatch(jangineShaderProgram, jangineTexture, camera);

        camera._position.y -= 50;

        float[] vertices = new float[]{
                /* Position */    /* UV-Coordinates */
                  0.0f,   0.0f,       0.0f, 0.0f,
                100.0f,   0.0f,       1.0f, 0.0f,
                100.0f, 100.0f,       1.0f, 1.0f,
                  0.0f, 100.0f,       0.0f, 1.0f
        };
        int[] indices = new int[]{
                0, 1, 2,
                2, 3, 0,
        };

        float[] secondVertices = new float[]{
                /* Position */    /* UV-Coordinates */
                100.0f, 100.0f,       0.0f, 0.0f,
                200.0f, 100.0f,       1.0f, 0.0f,
                200.0f, 200.0f,       1.0f, 1.0f,
                100.0f, 200.0f,       0.0f, 1.0f
        };
        int[] secondIndices = new int[]{
                0, 1, 2,
                2, 3, 0,
        };

        firstMesh = new JangineMesh(vertices, indices);

        batch.addMesh(firstMesh);
        batch.addMesh(new JangineMesh(secondVertices, secondIndices));
    }


    public void run() {
        if (firstMesh.getVertices()[1] > 200) {
            batch.rmvMesh(firstMesh);
        }

        firstMesh.getVertices()[1] += 1.0f;
        firstMesh.getVertices()[5] += 1.0f;
        firstMesh.getVertices()[9] += 1.0f;
        firstMesh.getVertices()[13] += 1.0f;

        batch.updateMesh(firstMesh);

        batch.render();
    }


}