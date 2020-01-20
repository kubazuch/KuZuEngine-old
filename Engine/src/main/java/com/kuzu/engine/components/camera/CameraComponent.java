package com.kuzu.engine.components.camera;

import com.kuzu.engine.components.GameComponent;
import com.kuzu.engine.core.*;
import com.kuzu.engine.event.MouseEvents;
import com.kuzu.engine.event.WindowResizeEvent;
import com.kuzu.engine.math.MathUtils;
import com.kuzu.event.api.EventSubscriber;
import org.joml.Vector2f;

public class CameraComponent extends GameComponent {
	private Camera camera;
	private boolean mouseLocked;
	private Vector2f windowCenter;

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

	@Override
	public void input(float delta, Input input) {
		float movAmt = 10 * delta;

		if (input.isKeyPressed(KeyCode.W)) {
			getTransform().getPos().fma(movAmt, MathUtils.getForward(camera.getTransform().getRot()));
		}
		if (input.isKeyPressed(KeyCode.S)) {
			getTransform().getPos().fma(-movAmt, MathUtils.getForward(camera.getTransform().getRot()));
		}
		if (input.isKeyPressed(KeyCode.A)) {
			getTransform().getPos().fma(-movAmt, MathUtils.getRight(camera.getTransform().getRot()));
		}
		if (input.isKeyPressed(KeyCode.D)) {
			getTransform().getPos().fma(movAmt, MathUtils.getRight(camera.getTransform().getRot()));
		}
		if (input.isKeyPressed(KeyCode.Q)) {
			getTransform().getPos().fma(-movAmt, MathUtils.UP);
		}
		if (input.isKeyPressed(KeyCode.E)) {
			getTransform().getPos().fma(movAmt, MathUtils.UP);
		}

		if (input.isKeyPressed(KeyCode.ESCAPE)) {
			input.setCursor(true);
			mouseLocked = false;
		}

		if (mouseLocked) {
			Vector2f deltaPos = input.getMousePosition().sub(windowCenter);
			boolean rotY = deltaPos.x != 0;
			boolean rotX = deltaPos.y != 0;

			if (rotY) {
				getTransform().getRot().rotateY((float) Math.toRadians(deltaPos.x) / 2);
			}

			if (rotX) {
				getTransform().getRot().integrate((float) Math.toRadians(deltaPos.y), 0.5f, 0, 0);
			}

			if (rotX || rotY) {
				input.setMousePosition(windowCenter);
			}
		}
	}

	public void setWindowCenter(Vector2f windowCenter) {
		this.windowCenter = windowCenter;
	}

	@EventSubscriber
	public void onMouseDown(MouseEvents.MouseButtonPressedEvent e) {
		if (e.getMouseButton() == MouseCode.BUTTON_LEFT) {
			e.getInput().setMousePosition(windowCenter);
			e.getInput().setCursor(false);
			mouseLocked = true;
		}
	}

	@EventSubscriber
	public void onWindowResizeEvent(WindowResizeEvent event) {
		float width = event.getWidth();
		float height = event.getHeight();
		float aspectRatio = width / height;

		camera.updateAspectRatio(aspectRatio);
		setWindowCenter(new Vector2f(width / 2, height / 2));
	}
}
