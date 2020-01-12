package com.kuzu.engine.core;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
	private Vector3f translation;
	private Quaternionf rotation;
	private Vector3f scale;

	public Transform() {
		translation = new Vector3f();
		rotation = new Quaternionf();
		scale = new Vector3f(1);
	}

	public Matrix4f getTransformation() {
		return new Matrix4f().translate(this.translation).rotate(rotation).scale(scale);
	}

	public Vector3f getTranslation() {
		return translation;
	}

	public void setTranslation(Vector3f translation) {
		this.translation = translation;
	}

	public void setTranslation(float x, float y, float z) {
		this.translation = new Vector3f(x, y, z);
	}

	public Quaternionf getRotation() {
		return rotation;
	}

	public void setRotation(Quaternionf rotation) {
		this.rotation = rotation;
	}

	public void setRotation(float x, float y, float z) {
		this.rotation = new Quaternionf().rotateXYZ(x, y, z);
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	public void setScale(float x, float y, float z) {
		this.scale = new Vector3f(x, y, z);
	}

}
