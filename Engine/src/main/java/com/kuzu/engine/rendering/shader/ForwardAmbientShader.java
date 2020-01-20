package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.core.Transform;
import com.kuzu.engine.rendering.Material;
import com.kuzu.engine.rendering.RenderingEngine;

public class ForwardAmbientShader extends Shader {
	public ForwardAmbientShader() {
		super("forward-ambient", false);
	}

	@Override
	public void updateUniforms(Transform transform, Material material, RenderingEngine engine, Camera camera) {
		material.getTexture("diffuse").bind(0);
		setUniform("modelMat", transform.getTransformation());
		setUniform("viewMat", camera.getView());
		setUniform("projectionMat", camera.getProjection());
		setUniform("ambientIntensity", engine.getAmbientLight());
	}
}