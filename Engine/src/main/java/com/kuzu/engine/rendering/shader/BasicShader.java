package com.kuzu.engine.rendering.shader;

import org.joml.Matrix4f;

public class BasicShader extends Shader {
	public BasicShader() {
		super("basic", false);
	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		setUniform("projectionMat", projectionMatrix);
	}

	public void updateUniforms(Matrix4f transformMat, Matrix4f viewMat) {
		setUniform("transformMat", transformMat);
		setUniform("viewMat", viewMat);
	}
}
