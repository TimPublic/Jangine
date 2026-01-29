package internal.rendering.texture;


import internal.rendering.texture.dependencies.I_TextureLoader;

import static org.lwjgl.opengl.GL11.*;


/**
 * Contains an image and can be bound to be used for rendering.
 * The image gets uploaded to the gpu upon creation automatically.
 * <p>
 * The file path of the image needs to be provided in the constructor,
 * as well as the {@link I_TextureLoader} implementation,
 * that is used for uploading the image to the gpu.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class Texture {


    private final int _TEXTURE_ID;
    private final String _PATH;


    public Texture(final String filePath, I_TextureLoader textureLoader) {
        _TEXTURE_ID = glGenTextures();

        bind();

        _setParameters(GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST);

        textureLoader.apply(filePath, _TEXTURE_ID);

        _PATH = filePath;

        unbind();
    }


    // -+- BINDING -+- //

    /**
     * Binds the texture.
     *
     * @author Tim Kloepper
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, _TEXTURE_ID);
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


    // -+- GETTERS -+- //

    public int getId() {
        return _TEXTURE_ID;
    }
    public String getPath() {
        return _PATH;
    }


}