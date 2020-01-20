package com.kuzu.engine.core;

import com.kuzu.engine.rendering.Window;
import org.joml.Vector2f;
import org.lwjglx.BufferUtils;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
	private Window window;

	public Input(Window window) {
		this.window = window;
	}

	public boolean isKeyPressed(KeyCode key) {
		int state = glfwGetKey(window.getWindowHandle(), key.getCode());
		return state == GLFW_PRESS || state == GLFW_REPEAT;
	}

	public boolean isMouseButtonPressed(MouseCode button) {
		int state = glfwGetMouseButton(window.getWindowHandle(), button.ordinal());
		return state == GLFW_PRESS;
	}

	public Vector2f getMousePosition() {
		DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(window.getWindowHandle(), x, y);
		return new Vector2f((float) x.get(), (float) y.get());
	}

	public void setMousePosition(Vector2f pos) {
		glfwSetCursorPos(window.getWindowHandle(), pos.x, pos.y);
	}

	public void setCursor(boolean enabled) {
		glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, enabled ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
	}
}
