package com.kuzu.engine.rendering;

import com.kuzu.engine.core.Util;
import com.kuzu.engine.rendering.resources.TextureResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Texture {

	private static HashMap<String, TextureResource> loadedTextures = new HashMap<>();
	private TextureResource resource;
	private String fileName;
	private int textureTarget;

	public Texture(String fileName) {
		this(fileName, GL_TEXTURE_2D, GL_NEAREST, GL_RGBA8, GL_RGBA, false);
	}

	public Texture(String fileName, int textureTarget, int filter, int internalFormat, int format, boolean clamp) {
		this.fileName = fileName;
		this.textureTarget = textureTarget;

		TextureResource oldResource = loadedTextures.get(fileName);
		if (oldResource != null) {
			resource = oldResource;
			resource.addReference();
		} else {
			resource = loadTexture(fileName, textureTarget, filter, internalFormat, format, clamp);
			loadedTextures.put(fileName, resource);
		}
	}

	private static TextureResource loadTexture(String fileName, int textureTarget, int filter, int internalFormat, int format, boolean clamp) {
		try {
			BufferedImage image = ImageIO.read(Texture.class.getResourceAsStream("/res/textures/" + fileName));
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

			ByteBuffer buffer = Util.createByteBuffer(image.getHeight() * image.getWidth() * 4);
			boolean hasAlpha = image.getColorModel().hasAlpha();

			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int pixel = pixels[y * image.getWidth() + x];

					buffer.put((byte) ((pixel >> 16) & 0xFF));
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					buffer.put((byte) ((pixel) & 0xFF));

					if (hasAlpha)
						buffer.put((byte) ((pixel >> 24) & 0xFF));
					else
						buffer.put((byte) (0xFF));
				}
			}

			buffer.flip();

			TextureResource resource = new TextureResource();
			glBindTexture(textureTarget, resource.getId());

			glTexParameteri(textureTarget, GL_TEXTURE_MIN_FILTER, filter);
			glTexParameteri(textureTarget, GL_TEXTURE_MAG_FILTER, filter);
			if (clamp) {
				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			} else {
				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_S, GL_REPEAT);
				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_T, GL_REPEAT);
			}

			glTexImage2D(textureTarget,
					0,
					internalFormat,
					image.getWidth(),
					image.getHeight(),
					0,
					format,
					GL_UNSIGNED_BYTE,
					buffer
			);

			if (filter == GL_NEAREST_MIPMAP_NEAREST ||
					filter == GL_NEAREST_MIPMAP_LINEAR ||
					filter == GL_LINEAR_MIPMAP_NEAREST ||
					filter == GL_LINEAR_MIPMAP_LINEAR) {
				//TODO: MIPMAP
			} else {
//				glTexParameteri(textureTarget, GL_TEXTURE_BASE_LEVEL, 0);
//				glTexParameteri(textureTarget, GL_TEXTURE_MAX_LEVEL, 0);
			}

			return resource;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void dispose() {
		if (resource.removeReference()) {
			resource.dispose();
			if (!fileName.isEmpty())
				loadedTextures.remove(fileName);
		}
	}

	public void bind() {
		bind(0);
	}

	public void bind(int samplerSlot) {
		assert (samplerSlot >= 0 && samplerSlot <= 31);
		glActiveTexture(GL_TEXTURE0 + samplerSlot);
		glBindTexture(textureTarget, resource.getId());
	}

	public int getID() {
		return resource.getId();
	}
}
