package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.components.light.BaseLight;
import com.kuzu.engine.components.light.DirectionalLight;
import com.kuzu.engine.core.Transform;
import com.kuzu.engine.rendering.Material;
import com.kuzu.engine.rendering.RenderingEngine;

public class ForwardDirectionalShader extends Shader {
	public ForwardDirectionalShader() {
		super("forward-directional", false);
	}

	@Override
	public void updateUniforms(Transform transform, Material material, RenderingEngine engine, Camera camera) {
		material.getTexture("diffuse").bind(0);
		setUniform("modelMat", transform.getTransformation());
		setUniform("viewMat", camera.getView());
		setUniform("projectionMat", camera.getProjection());

		setUniformf("specularIntensity", material.getFloat("specularIntensity"));
		setUniformf("specularPower", material.getFloat("specularPower"));

		setUniform("eyePos", engine.getMainCamera().getPosition());
		setDirectionalLight("directionalLight", (DirectionalLight) engine.getActiveLight());
	}

	public void setBaseLight(String name, BaseLight baseLight) {
		setUniform(name + ".color", baseLight.getColor());
		setUniformf(name + ".intensity", baseLight.getIntensity());
	}

	public void setDirectionalLight(String name, DirectionalLight light) {
		setBaseLight(name + ".base", light);
		setUniform(name + ".direction", light.getDirection());
	}
}
