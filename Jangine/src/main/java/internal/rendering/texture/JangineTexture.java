package internal.rendering.texture;


import internal.rendering.texture.dependencies.I_JangineTextureLoader;

import static org.lwjgl.opengl.GL11.*;


// Contains an image and can be bound to be used by the gpu.
// Automatically uploads this image on creation.
// Uses the texture-loader interface, which needs to be
// specified in the constructor to load the image.
// The image is loaded through a file-path, which also needs to be
// specified in the constructor.
public class JangineTexture {


    private final int _textureID;


    public JangineTexture(final String filePath, I_JangineTextureLoader textureLoader) {
        _textureID = glGenTextures();

        bind();

        _setParameters(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST);

        textureLoader.apply(filePath, _textureID);

        unbind();
    }


    // -+- BINDING -+- //

    // Binds the texture.
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, _textureID);
    }
    // Unbinds the texture.
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }


    // -+- GENERATION -+- //

    // Sets the parameters for the texture.
    private void _setParameters(int wrapS, int wrapT, int minFilter, int magFilter) {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
    }


}