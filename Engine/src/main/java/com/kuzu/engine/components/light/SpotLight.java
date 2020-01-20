package com.kuzu.engine.components.light;

import com.kuzu.engine.math.MathUtils;
import com.kuzu.engine.rendering.shader.ForwardSpotShader;
import org.joml.Vector3f;

public class SpotLight extends PointLight {
	private float cutoff;

	public SpotLight(Vector3f color, float intensity, Attenuation atten, float cutoff) {
		super(color, intensity, atten);
		this.cutoff = cutoff;
		setShader(new ForwardSpotShader());
	}

	public Vector3f getDirection() {
		return MathUtils.getForward(getTransform().getTransformedRot());
	}

	public float getCutoff() {
		return cutoff;
	}

	public void setCutoff(float cutoff) {
		this.cutoff = cutoff;
	}
}
