package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.components.light.DirectionalLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PhongShader extends Shader {
	public PhongShader() {
		super("phong", false);
	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		setUniform("projectionMat", projectionMatrix);
	}

	public void updateUniforms(Matrix4f transformMat, Matrix4f viewMat) {
		setUniform("transformMat", transformMat);
		setUniform("viewMat", viewMat);
//		setUniform("normalMat", transformMat.mul(viewMat, new Matrix4f()));
		setUniform("baseColor", new Vector3f(1f, 1f, 1f));
	}

	public void setAmbientLight(Vector3f ambientLight) {
		setUniform("ambientLight", ambientLight);
	}

	public void setDirectionalLight(String name, DirectionalLight light) {
		setUniform(name + ".base.color", light.getBase().getColor());
		setUniformf(name + ".base.intensity", light.getBase().getIntensity());
		setUniform(name + ".direction", light.getDirection());
	}
}
