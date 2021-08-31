package com.kuzu.engine.components.camera;

import com.kuzu.engine.components.GameComponent;
import com.kuzu.engine.core.CoreEngine;
import com.kuzu.engine.core.GameObject;
import com.kuzu.engine.event.WindowResizeEvent;
import org.greenrobot.eventbus.Subscribe;

public class CameraComponent extends GameComponent {
	private Camera camera;

	public CameraComponent(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void addToEngine(CoreEngine engine) {
		engine.getRenderingEngine().setMainCamera(camera);
	}

	@Override
	public void setParent(GameObject parent) {
		super.setParent(parent);
		camera.setTransform(getTransform());
	}

	@Subscribe
	public void onWindowResizeEvent(WindowResizeEvent event) {
		float width = event.getWidth();
		float height = event.getHeight();
		float aspectRatio = width / height;

		camera.updateAspectRatio(aspectRatio);
	}
}
