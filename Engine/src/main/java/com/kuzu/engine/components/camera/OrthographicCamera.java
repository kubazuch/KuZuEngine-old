package com.kuzu.engine.components.camera;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OrthographicCamera extends Camera {

	public OrthographicCamera(float left, float right, float bottom, float top) {
		this(left, right, bottom, top, new Vector3f(), new Quaternionf());
	}

	public OrthographicCamera(float left, float right, float bottom, float top, Vector3f position, Quaternionf rotation) {
		super(position, rotation);
		this.projectionMatrix = new Matrix4f().ortho(left, right, bottom, top, 0.1f, 1000f);
		recalculateViewMatrix();
	}

	public void setProjection(float left, float right, float bottom, float top) {
		this.projectionMatrix = new Matrix4f().ortho(left, right, bottom, top, 0.1f, 1000f);
	}
}
