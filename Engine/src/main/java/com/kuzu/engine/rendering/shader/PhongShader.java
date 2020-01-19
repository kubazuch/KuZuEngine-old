package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.components.light.*;
import com.kuzu.engine.rendering.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PhongShader extends Shader {
	private final int MAX_POINT_LIGHTS = resource.getIntegerConsts().get("MAX_POINT_LIGHTS");
	private final int MAX_SPOT_LIGHTS = resource.getIntegerConsts().get("MAX_SPOT_LIGHTS");

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

	public void setAttenuation(String name, Attenuation attenuation) {
		setUniformf(name + ".constant", attenuation.getConstant());
		setUniformf(name + ".linear", attenuation.getLinear());
		setUniformf(name + ".exponent", attenuation.getExponent());
	}

	public void setPointLight(String name, PointLight light) {
		setBaseLight(name + ".base", light.getBaseLight());
		setAttenuation(name + ".atten", light.getAtten());
		setUniform(name + ".pos", light.getPosition());
		setUniformf(name + ".range", light.getRange());
	}

	public void setSpotLight(String name, SpotLight light) {
		setPointLight(name + ".pointLight", light.getPointLight());
		setUniform(name + ".direction", light.getDirection());
		setUniformf(name + ".cutoff", light.getCutoff());
	}

	public void setPointLights(PointLight... pointLights) {
		if (pointLights.length > MAX_POINT_LIGHTS)
			throw new RuntimeException("Error: Passed in too many point lights. Max allowed is " + MAX_POINT_LIGHTS + ", passed " + pointLights.length);

		for (int i = 0; i < pointLights.length; i++)
			setPointLight("pointLights[" + i + "]", pointLights[i]);

	}

	public void setSpotLights(SpotLight... spotLights) {
		if (spotLights.length > MAX_SPOT_LIGHTS)
			throw new RuntimeException("Error: Passed in too many spot lights. Max allowed is " + MAX_SPOT_LIGHTS + ", passed " + spotLights.length);

		for (int i = 0; i < spotLights.length; i++)
			setSpotLight("spotLights[" + i + "]", spotLights[i]);
	}
}
