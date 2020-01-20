package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.rendering.Material;
import org.joml.Matrix4f;

public class BasicShader extends Shader {
	public BasicShader() {
		super("basic", false);
	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		setUniform("projectionMat", projectionMatrix);
	}

	public void updateUniforms(Matrix4f transformMat, Camera camera, Material material) {
		material.getTexture("diffuse").bind(0);
		setUniform("transformMat", transformMat);
		setUniform("viewMat", camera.getView());
	}
}
