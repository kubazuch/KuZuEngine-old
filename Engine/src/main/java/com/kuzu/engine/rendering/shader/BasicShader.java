package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.core.ResourceLoader;

public class BasicShader extends Shader {
	private static final BasicShader instance = new BasicShader();

	private BasicShader() {
		super();

		addVertexShader(ResourceLoader.loadShader("basic.vs"));
		addFragmentShader(ResourceLoader.loadShader("basic.fs"));
		linkShader();

		bindAttribLocation(0, "pos");
		bindAttribLocation(1, "texCoord");

		addUniform("transformMat");
		addUniform("viewMat");
		addUniform("projectionMat");
	}

	public static BasicShader getInstance() {
		return instance;
	}
}
