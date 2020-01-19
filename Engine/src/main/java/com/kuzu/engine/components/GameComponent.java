package com.kuzu.engine.components;

import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.core.CoreEngine;
import com.kuzu.engine.core.GameObject;
import com.kuzu.engine.core.Transform;
import com.kuzu.engine.rendering.shader.Shader;

public abstract class GameComponent {
	private GameObject parent;

	public void input(float delta) {
	}

	public void update(float delta) {
	}

	public void render(Shader shader, Camera camera) {
	}

	public void setParent(GameObject parent) {
		this.parent = parent;
	}

	public Transform getTransform() {
		return parent.getTransform();
	}

	public void addToEngine(CoreEngine engine) {
	}
}
