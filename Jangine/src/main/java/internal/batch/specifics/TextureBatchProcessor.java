package internal.batch.specifics;


import internal.batch.A_Batch;
import internal.batch.A_BatchProcessor;
import internal.rendering.mesh.A_Mesh;
import internal.rendering.mesh.TexturedAMesh;
import internal.rendering.shader.ShaderProgram;


public class TextureBatchProcessor extends A_BatchProcessor<TexturedAMesh> {


    @Override
    protected Class<? extends A_Mesh> p_getProcessedMeshSubclass() {
        return TexturedAMesh.class;
    }


    @Override
    protected A_Batch<TexturedAMesh> p_createBatch(ShaderProgram shader) {
        return new TextureBatch(shader, 1111, 5, 5555);
    }


}