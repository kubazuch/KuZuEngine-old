package com.kuzu.engine.core;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
	private Transform parent;
	private Matrix4f parentMatrix;

	private Vector3f pos;
	private Quaternionf rot;
	private Vector3f scale;

	private Vector3f oldPos;
	private Quaternionf oldRot;
	private Vector3f oldScale;

	public Transform() {
		this(new Vector3f(), new Quaternionf(), new Vector3f());
	}

	public Transform(Vector3f pos) {
		this(pos, new Quaternionf(), new Vector3f(1));
	}

	public Transform(Vector3f pos, Quaternionf rot) {
		this(pos, rot, new Vector3f(1));
	}

	public Transform(Vector3f pos, Quaternionf rot, Vector3f scale) {
		this.pos = pos;
		this.rot = rot;
		this.scale = scale;
		this.parentMatrix = new Matrix4f();
	}

	public void update() {
		if (oldPos != null) {
			oldPos.set(pos);
			oldRot.set(rot);
			oldScale.set(scale);
		} else {
			oldPos = pos.add(1f, 1f, 1f, new Vector3f());
			oldRot = rot.mul(0.5f, 0.5f, 0.5f, 0.5f, new Quaternionf());
			oldScale = scale.add(1f, 1f, 1f, new Vector3f());
		}
	}

	public void rotate(Vector3f axis, float angle) {
		rotate(new Quaternionf().fromAxisAngleRad(axis, angle));
	}

	public void rotate(Quaternionf rotation) {
		rot = rotation.mul(rot, new Quaternionf()).normalize();
	}

	public void lookAt(Vector3f point, Vector3f up) {
		rot = getLookAtRotation(point, up);
	}

	public Quaternionf getLookAtRotation(Vector3f point, Vector3f up) {
		return new Quaternionf().lookAlong(point.sub(pos, new Vector3f()).normalize(), up);
	}

	public boolean hasChanged() {
		return (parent != null && parent.hasChanged()) || !pos.equals(oldPos) || !rot.equals(oldRot) || !scale.equals(oldScale);
	}

	public Matrix4f getTransformation() {
		return new Matrix4f(getParentMatrix()).translate(pos).rotate(rot).scale(scale);
	}

	public Matrix4f getParentMatrix() {
		if (parent != null && parent.hasChanged())
			parentMatrix = parent.getTransformation();

		return parentMatrix;
	}

	public void setParent(Transform parent) {
		this.parent = parent;
	}

	public Vector3f getTransformedPos() {
		return pos.mulPosition(getParentMatrix(), new Vector3f());
	}

	public Quaternionf getTransformedRot() {
		Quaternionf parentRotation = new Quaternionf();

		if (parent != null)
			parentRotation = parent.getTransformedRot();

		return parentRotation.mul(rot, new Quaternionf());
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Quaternionf getRot() {
		return rot;
	}

	public void setRot(Quaternionf rot) {
		this.rot = rot;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

}
