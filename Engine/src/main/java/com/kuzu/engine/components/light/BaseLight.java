package com.kuzu.engine.components.light;

import com.kuzu.engine.components.GameComponent;
import com.kuzu.engine.core.CoreEngine;
import com.kuzu.engine.rendering.shader.Shader;
import org.joml.Vector3f;

public class BaseLight extends GameComponent {
	private Vector3f color;
	private float intensity;
	private Shader shader;

	public BaseLight(Vector3f color, float intensity) {
		this.color = color;
		this.intensity = intensity;
	}

	@Override
	public void addToEngine(CoreEngine engine) {
		engine.getRenderingEngine().addLight(this);
	}

	public Shader getShader() {
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
}
