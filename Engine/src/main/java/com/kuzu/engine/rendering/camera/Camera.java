package com.kuzu.engine.rendering.camera;

import com.kuzu.engine.core.Input;
import com.kuzu.engine.core.KeyCode;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class Camera {
	protected Vector3f position;
	protected Quaternionf rotation;

	protected Matrix4f projectionMatrix;
	protected Matrix4f viewMatrix;

	private boolean mouseLocked;
	private Vector2f windowCenter;

	public Camera(Vector3f position, Quaternionf rotation) {
		this.position = position;
		this.rotation = rotation;
	}

	public void input(float delta) {
		float movAmt = 10 * delta;

		if (Input.isKeyPressed(KeyCode.W)) {
			position.fma(movAmt, getForward());
			recalculateViewMatrix();
		}
		if (Input.isKeyPressed(KeyCode.S)) {
			position.fma(-movAmt, getForward());
			recalculateViewMatrix();
		}
		if (Input.isKeyPressed(KeyCode.A)) {
			position.fma(-movAmt, getRight());
			recalculateViewMatrix();
		}
		if (Input.isKeyPressed(KeyCode.D)) {
			position.fma(movAmt, getRight());
			recalculateViewMatrix();
		}
		if (Input.isKeyPressed(KeyCode.Q)) {
			position.fma(-movAmt, new Vector3f(0, 1, 0));
			recalculateViewMatrix();
		}
		if (Input.isKeyPressed(KeyCode.E)) {
			position.fma(movAmt, new Vector3f(0, 1, 0));
			recalculateViewMatrix();
		}

		if (Input.isKeyPressed(KeyCode.ESCAPE)) {
			Input.setCursor(true);
			mouseLocked = false;
		}

		if (mouseLocked) {
			Vector2f deltaPos = Input.getMousePosition().sub(windowCenter);
			boolean rotY = deltaPos.x != 0;
			boolean rotX = deltaPos.y != 0;

			if (rotY) {
				rotation.rotateY((float) Math.toRadians(deltaPos.x) / 2);
			}

			if (rotX) {
				rotation.integrate((float) Math.toRadians(deltaPos.y), 0.5f, 0, 0);
			}

			if (rotX || rotY) {
				Input.setMousePosition(windowCenter);
				recalculateViewMatrix();
			}
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
		recalculateViewMatrix();
	}

	public Quaternionf getRotation() {
		return rotation;
	}

	public void setRotation(Quaternionf rotation) {
		this.rotation = rotation;
		recalculateViewMatrix();
	}

	public Vector3f getRight() {
		return rotation.positiveX(new Vector3f());
	}

	public Vector3f getUp() {
		return rotation.positiveY(new Vector3f());
	}

	public Vector3f getForward() {
		return rotation.normalizedPositiveZ(new Vector3f()).negate();
	}

	public Matrix4f getView() {
		return viewMatrix;
	}

	public Matrix4f getProjection() {
		return projectionMatrix;
	}

	public void setWindowCenter(Vector2f windowCenter) {
		this.windowCenter = windowCenter;
	}

	protected void recalculateViewMatrix() {
		Matrix4f view = new Matrix4f();
//		view.rotate(this.rotation);
//		Vector3f negPos = this.position.negate(new Vector3f());

		this.viewMatrix = view.rotate(this.rotation).translate(-this.position.x, -this.position.y, -this.position.z);
	}

	public void onLMBDown() {
		Input.setMousePosition(windowCenter);
		Input.setCursor(false);
		mouseLocked = true;
	}
}
