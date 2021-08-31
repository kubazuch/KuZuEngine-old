package com.kuzu.engine.components.camera;

import com.kuzu.engine.core.Transform;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Camera {
	protected Transform transform;
	protected Matrix4f projectionMatrix;

	public Camera(Matrix4f projection, Transform transform) {
		this.transform = transform;
		this.projectionMatrix = projection;
	}

	public abstract void updateAspectRatio(float aspectRatio);

	public Vector3f getPosition() {
		return transform.getPos();
	}

	public void setPosition(Vector3f position) {
		this.transform.setPos(position);
	}

	public Quaternionf getRotation() {
		return transform.getRot();
	}

	public void setRotation(Quaternionf rotation) {
		this.transform.setRot(rotation);
	}

	public Transform getTransform() {
		return transform;
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
	}

	public Matrix4f getView() {
		return new Matrix4f()
				.rotate(transform.getTransformedRot().conjugate())
				.translate(-transform.getTransformedPos().x, -transform.getTransformedPos().y, -transform.getTransformedPos().z);
	}

	public Matrix4f getProjection() {
		return projectionMatrix;
	}

}
