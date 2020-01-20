package com.kuzu.engine.components.light;

import com.kuzu.engine.math.MathUtils;
import com.kuzu.engine.rendering.shader.ForwardPointShader;
import org.joml.Vector3f;

public class PointLight extends BaseLight {
	private static final int COLOR_DEPTH = 256;

	private Attenuation atten;
	private float range;

	public PointLight(Vector3f color, float intensity, Attenuation atten) {
		super(color, intensity);
		this.atten = atten;

		float a = atten.getExponent();
		float b = atten.getLinear();
		float c = atten.getConstant() - COLOR_DEPTH * getIntensity() * MathUtils.max(getColor());

		this.range = (float) ((-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a));
		setShader(new ForwardPointShader());
	}

	public Attenuation getAtten() {
		return atten;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}
}
