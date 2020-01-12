package com.kuzu.engine.rendering.shader;

public class PhongShader extends Shader {
	private static final PhongShader instance = new PhongShader();

	private PhongShader() {
		super("phong", true);
	}

	public static PhongShader getInstance() {
		return instance;
	}


}
