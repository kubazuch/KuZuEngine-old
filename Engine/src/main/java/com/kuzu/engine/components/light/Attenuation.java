package com.kuzu.engine.components.light;

import org.joml.Vector3f;

public class Attenuation extends Vector3f {
	public Attenuation(float constant, float linear, float exponent) {
		super(constant, linear, exponent);
	}

	public Attenuation() {
		super();
	}

	public float getConstant() {
		return x;
	}

	public float getLinear() {
		return y;
	}

	public float getExponent() {
		return z;
	}
}
