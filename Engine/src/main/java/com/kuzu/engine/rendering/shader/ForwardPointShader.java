package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.components.light.Attenuation;
import com.kuzu.engine.components.light.BaseLight;
import com.kuzu.engine.components.light.PointLight;
import com.kuzu.engine.core.Transform;
import com.kuzu.engine.rendering.Material;
import com.kuzu.engine.rendering.RenderingEngine;

public class ForwardPointShader extends Shader {
	public ForwardPointShader() {
		super("forward-point", false);
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
		setPointLight("pointLight", (PointLight) engine.getActiveLight());
	}

	public void setBaseLight(String name, BaseLight baseLight) {
		setUniform(name + ".color", baseLight.getColor());
		setUniformf(name + ".intensity", baseLight.getIntensity());
	}

	public void setAttenuation(String name, Attenuation attenuation) {
		setUniformf(name + ".constant", attenuation.getConstant());
		setUniformf(name + ".linear", attenuation.getLinear());
		setUniformf(name + ".exponent", attenuation.getExponent());
	}

	public void setPointLight(String name, PointLight light) {
		setBaseLight(name + ".base", light);
		setAttenuation(name + ".atten", light.getAtten());
		setUniform(name + ".pos", light.getTransform().getTransformedPos());
		setUniformf(name + ".range", light.getRange());
	}
}
