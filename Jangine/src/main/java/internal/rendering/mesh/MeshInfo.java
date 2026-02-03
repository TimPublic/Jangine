package internal.rendering.mesh;


public class MeshInfo {


    // -+- CREATION -+- //

    public MeshInfo(A_Mesh mesh) {
        update(mesh);
    }
    public MeshInfo(int verticesAmount, int indicesAmount, int vertexSize) {
        this.verticesAmount = verticesAmount;
        this.indicesAmount = indicesAmount;
        this.vertexSize = vertexSize;
    }


    // -+- PARAMETERS -+- //

    // NON-FINALS //

    public int verticesAmount, indicesAmount;
    public int vertexSize;

    public int vertexPointer, indexPointer;


    // -+- DATA MANAGEMENT -+- //

    public void update(A_Mesh mesh) {
        verticesAmount = mesh.vertices.length / mesh.getVertexSize();
        indicesAmount = mesh.indices.length;
        vertexSize = mesh.getVertexSize();
    }

    public void addedToBatch(int vertexPointer, int indexPointer) {
        this.vertexPointer = vertexPointer;
        this.indexPointer = indexPointer;
    }
    public void removedFromBatch() {
        vertexPointer = -1;
        indexPointer = -1;
    }
    public void setBatchPosition(int vertexPointer, int indexPointer) {
        this.vertexPointer = vertexPointer;
        this.indexPointer = indexPointer;
    }


    // -+- CHECKERS -+- //

    public boolean isEqual(A_Mesh mesh) {
        return verticesAmount * vertexSize == mesh.vertices.length && indicesAmount == mesh.indices.length;
    }
    public boolean isEqual(MeshInfo info) {
        return verticesAmount * vertexSize == info.verticesAmount * info.vertexSize && indicesAmount == info.indicesAmount;
    }

    public boolean isInBatch() {
        return vertexPointer != -1;
    }


}