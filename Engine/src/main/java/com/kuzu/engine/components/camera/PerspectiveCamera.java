package com.kuzu.engine.components.camera;

import com.kuzu.engine.core.Transform;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PerspectiveCamera extends Camera {
	private float fov;
	private float zNear;
	private float zFar;

	public PerspectiveCamera(float fov, float aspect, float zNear, float zFar) {
		super(new Matrix4f().perspective(fov, aspect, zNear, zFar), new Transform());
		this.fov = fov;
		this.zNear = zNear;
		this.zFar = zFar;
	}

	public PerspectiveCamera(float fov, float aspect, float zNear, float zFar, Vector3f position, Quaternionf rotation) {
		super(new Matrix4f().perspective(fov, aspect, zNear, zFar), new Transform(position, rotation));
		this.fov = fov;
		this.zNear = zNear;
		this.zFar = zFar;
	}

	@Override
	public void updateAspectRatio(float aspectRatio) {
		setProjection(fov, aspectRatio, zNear, zFar);
	}

	public void setProjection(float fov, float aspect, float zNear, float zFar) {
		this.projectionMatrix.setPerspective(fov, aspect, zNear, zFar);
	}
}
