package com.kuzu.engine.core;

import com.kuzu.engine.rendering.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.GL_NEAREST;

public class ResourceLoader {

	public static Texture loadTexture(String fileName) {
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length - 1];

		try {
			int id = TextureLoader.getTexture(ext, ResourceLoader.class.getResourceAsStream("/res/textures/" + fileName), GL_NEAREST).getTextureID();
			return new Texture(id);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return null;
	}

	public static String loadShader(String fileName) {
		String data = "";
		try {
			InputStream in = ResourceLoader.class.getResourceAsStream("/res/shaders/" + fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			Stream<String> lines = reader.lines();
			data = lines.collect(Collectors.joining("\n"));
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.exit(1);
		}

		return data;
	}
}
