package com.kuzu.engine.components.camera;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PerspectiveCamera extends Camera {
	public PerspectiveCamera(float fov, float aspect, float zNear, float zFar) {
		this(fov, aspect, zNear, zFar, new Vector3f(), new Quaternionf());
	}

	public PerspectiveCamera(float fov, float aspect, float zNear, float zFar, Vector3f position, Quaternionf rotation) {
		super(position, rotation);
		this.projectionMatrix = new Matrix4f().perspective(fov, aspect, zNear, zFar);
		recalculateViewMatrix();
	}

	public void setProjection(float fov, float aspect, float zNear, float zFar) {
		this.projectionMatrix.setPerspective(fov, aspect, zNear, zFar);
	}
}
