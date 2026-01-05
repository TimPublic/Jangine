package internal.rendering.texture.dependencies;


import static org.lwjgl.opengl.GL11.*;


public interface I_JangineTextureLoader {


    // Retrieves and uploads a texture with the given GLFW texture-id from the given file-path.
    // Binds and unbinds itself.
    // This function is not meant to be overwritten.
    default void apply(String filePath, int textureID) {
        glBindTexture(GL_TEXTURE_2D, textureID);

        _loadTextureAndUploadInternal(filePath, textureID);

        glBindTexture(GL_TEXTURE_2D, 0);
    }
    // Actual load and upload functionality.
    void _loadTextureAndUploadInternal(String filePath, int textureID);


}