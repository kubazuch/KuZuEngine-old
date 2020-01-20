package com.kuzu.engine.components.camera;

import com.kuzu.engine.core.Transform;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OrthographicCamera extends Camera {
	private float zoomLevel = 1f;

	public OrthographicCamera(float aspectRatio, float zoomLevel) {
		this(-aspectRatio * zoomLevel, aspectRatio * zoomLevel, -zoomLevel, zoomLevel);
		this.zoomLevel = zoomLevel;
	}

	public OrthographicCamera(float left, float right, float bottom, float top) {
		super(new Matrix4f().ortho(left, right, bottom, top, 0.1f, 1000f), new Transform());
	}

	public OrthographicCamera(float left, float right, float bottom, float top, Vector3f position, Quaternionf rotation) {
		super(new Matrix4f().ortho(left, right, bottom, top, 0.1f, 1000f), new Transform(position, rotation));
	}

	@Override
	public void updateAspectRatio(float aspectRatio) {
		setProjection(-aspectRatio * zoomLevel, aspectRatio * zoomLevel, -zoomLevel, zoomLevel);
	}

	public void setProjection(float left, float right, float bottom, float top) {
		this.projectionMatrix = new Matrix4f().ortho(left, right, bottom, top, 0.1f, 1000f);
	}
}
