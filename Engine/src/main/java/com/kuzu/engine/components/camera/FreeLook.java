package com.kuzu.engine.components.camera;

import com.kuzu.engine.components.GameComponent;
import com.kuzu.engine.core.Input;
import com.kuzu.engine.core.KeyCode;
import com.kuzu.engine.core.MouseCode;
import com.kuzu.engine.event.MouseEvents;
import com.kuzu.engine.event.WindowResizeEvent;
import org.greenrobot.eventbus.Subscribe;
import org.joml.Vector2f;

public class FreeLook extends GameComponent {
	private boolean mouseLocked;
	private float sensitivity;
	private boolean body;
	private Vector2f windowCenter;

	public FreeLook(float sensitivity, boolean body) {
		this.sensitivity = sensitivity;
		this.body = body;
	}

	@Override
	public void input(float delta, Input input) {

		if (input.isKeyPressed(KeyCode.ESCAPE)) {
			input.setCursor(true);
			mouseLocked = false;
		}

		if (mouseLocked) {
			Vector2f deltaPos = input.getMousePosition().sub(windowCenter);
			boolean rotY = body && deltaPos.x != 0;
			boolean rotX = !body && deltaPos.y != 0;


			if (rotY) {
				getTransform().getRot().rotateLocalY(-(float) Math.toRadians(deltaPos.x) * sensitivity);
			}

			if (rotX) {
				getTransform().getRot().rotateX(-(float) Math.toRadians(deltaPos.y) * sensitivity);
			}

			if (rotX || rotY) {
				input.setMousePosition(windowCenter);
			}
		}
	}

	public void setWindowCenter(Vector2f windowCenter) {
		this.windowCenter = windowCenter;
	}

	@Subscribe
	public void onMouseDown(MouseEvents.MouseButtonPressedEvent e) {
		if (e.getMouseButton() == MouseCode.BUTTON_LEFT) {
			e.getInput().setMousePosition(windowCenter);
			e.getInput().setCursor(false);
			mouseLocked = true;
		}
	}

	@Subscribe
	public void onWindowResizeEvent(WindowResizeEvent event) {
		float width = event.getWidth();
		float height = event.getHeight();
		float aspectRatio = width / height;

		setWindowCenter(new Vector2f(width / 2, height / 2));
	}
}
