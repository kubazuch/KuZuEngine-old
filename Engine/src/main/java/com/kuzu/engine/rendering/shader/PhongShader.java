package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.components.light.Attenuation;
import com.kuzu.engine.components.light.BaseLight;
import com.kuzu.engine.components.light.DirectionalLight;
import com.kuzu.engine.components.light.PointLight;
import com.kuzu.engine.rendering.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PhongShader extends Shader {
	private final int MAX_POINT_LIGHTS = resource.getIntegerConsts().get("MAX_POINT_LIGHTS");

	public PhongShader() {
		super("phong", false);
	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		setUniform("projectionMat", projectionMatrix);
	}

	public void updateUniforms(Matrix4f transformMat, Camera camera, Material material) {
		setUniform("transformMat", transformMat);
		setUniform("viewMat", camera.getView());
//		setUniform("normalMat", transformMat.mul(viewMat, new Matrix4f()));
		setUniform("baseColor", new Vector3f(1f, 1f, 1f));

		setUniformf("specularIntensity", material.getFloat("specularIntensity"));
		setUniformf("specularPower", material.getFloat("specularPower"));

		setUniform("eyePos", camera.getPosition());
	}

	public void setAmbientLight(Vector3f ambientLight) {
		setUniform("ambientLight", ambientLight);
	}

	public void setBaseLight(String name, BaseLight baseLight) {
		setUniform(name + ".color", baseLight.getColor());
		setUniformf(name + ".intensity", baseLight.getIntensity());
	}

	public void setDirectionalLight(String name, DirectionalLight light) {
		setBaseLight(name + ".base", light.getBase());
		setUniform(name + ".direction", light.getDirection());
	}

	public void setAttenutation(String name, Attenuation attenuation) {
		setUniformf(name + ".constant", attenuation.getConstant());
		setUniformf(name + ".linear", attenuation.getLinear());
		setUniformf(name + ".exponent", attenuation.getExponent());
	}

	public void setPointLight(String name, PointLight light) {
		setBaseLight(name + ".base", light.getBaseLight());
		setAttenutation(name + ".atten", light.getAtten());
		setUniform(name + ".pos", light.getPosition());
	}

	public void setPointLights(PointLight... pointLights) {
		if (pointLights.length > MAX_POINT_LIGHTS)
			throw new RuntimeException("Error: Passed in too many lights. Max allowed is " + MAX_POINT_LIGHTS + ", passed " + pointLights.length);

		for (int i = 0; i < pointLights.length; i++) {
			setPointLight("pointLights[" + i + "]", pointLights[i]);
		}
	}
}
