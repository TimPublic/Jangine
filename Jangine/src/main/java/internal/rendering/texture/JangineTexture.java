package internal.rendering.texture;


import internal.rendering.texture.dependencies.I_JangineTextureLoader;

import static org.lwjgl.opengl.GL11.*;


/**
 * Contains an image and can be bound to be used for rendering.
 * The image gets uploaded to the gpu upon creation automatically.
 * <p>
 * The file path of the image needs to be provided in the constructor,
 * as well as the {@link I_JangineTextureLoader} implementation,
 * that is used for uploading the image to the gpu.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
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

    /**
     * Binds the texture.
     *
     * @author Tim Kloepper
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, _textureID);
    }
    // Unbinds the texture.
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }


    // -+- GENERATION -+- //

    /**
     * Sets the parameters of this texture.
     *
     * @param wrapS how it should wrap on the x-axis.
     * @param wrapT how it should wrap on the y-axis.
     * @param minFilter how it should act upon downscale.
     * @param magFilter how it should act upon upscale.
     *
     * @author Tim Kloepper
     */
    private void _setParameters(int wrapS, int wrapT, int minFilter, int magFilter) {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
    }


}