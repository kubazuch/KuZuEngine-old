package com.kuzu.engine.components.light;

import com.kuzu.engine.math.MathUtils;
import com.kuzu.engine.rendering.shader.ForwardDirectionalShader;
import org.joml.Vector3f;

public class DirectionalLight extends BaseLight {
	private Vector3f direction;

	public DirectionalLight(Vector3f color, float intensity) {
		super(color, intensity);
		setShader(new ForwardDirectionalShader());
	}

	public Vector3f getDirection() {
		return MathUtils.getForward(getTransform().getTransformedRot());
	}
}
