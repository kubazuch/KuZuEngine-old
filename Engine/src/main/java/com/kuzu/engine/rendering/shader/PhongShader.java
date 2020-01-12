package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.core.ResourceLoader;

public class PhongShader extends Shader {
	private static final PhongShader instance = new PhongShader();

	private PhongShader() {
		super();

		addVertexShader(ResourceLoader.loadShader("phong.vs"));
		addGeometryShader(ResourceLoader.loadShader("phong.gs"));
		addFragmentShader(ResourceLoader.loadShader("phong.fs"));
		linkShader();

		bindAttribLocation(0, "pos");
		bindAttribLocation(1, "texCoord");
		bindAttribLocation(2, "normal");

		addUniform("transformMat");
		addUniform("viewMat");
		addUniform("projectionMat");
		addUniform("normalMat");
	}

	public static PhongShader getInstance() {
		return instance;
	}


}
