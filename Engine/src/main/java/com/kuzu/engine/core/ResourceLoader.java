package com.kuzu.engine.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceLoader {

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
