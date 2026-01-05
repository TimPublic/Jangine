package internal.rendering.texture.dependencies.implementations;


import internal.rendering.texture.dependencies.I_JangineTextureLoader;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.stb.STBImage.stbi_load;


public class STBI_TextureLoader implements I_JangineTextureLoader {


    private static STBI_TextureLoader _instance;


    public static STBI_TextureLoader get() {
        if (_instance == null) {
            _instance = new STBI_TextureLoader();
        }

        return _instance;
    }


    @Override
    public void _loadTextureAndUploadInternal(String filePath, int textureID) {
        IntBuffer width;
        IntBuffer height;
        IntBuffer channels;

        ByteBuffer image;

        width = BufferUtils.createIntBuffer(1);
        height = BufferUtils.createIntBuffer(1);
        channels = BufferUtils.createIntBuffer(1);

        image = stbi_load(filePath, width, height, channels, 0);

        if (image == null) {
            System.err.println("[TEXTURE-LOADER ERROR] : Error while loading image!");
            System.err.println("|-> File-Path : " + filePath);

            System.exit(1);
        }

        switch (channels.get(0)) {
            case 3: // RGB
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
                break;
            case 4: // RGBA
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
                break;
            default:
                System.err.println("[TEXTURE-LOADER ERROR] : Image has an unsupported amount of channels!");
                System.err.println("|-> File-Path : " + filePath);
                System.err.println("|-> Channels : " + channels.get(0));

                System.exit(1);

                break;
        }
    }


}