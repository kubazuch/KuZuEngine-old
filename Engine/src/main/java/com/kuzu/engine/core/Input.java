package com.kuzu.engine.core;

import org.joml.Vector2f;
import org.lwjglx.BufferUtils;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
	private static long window;

	static void setWindow(long window) {
		Input.window = window;
	}

	public static boolean isKeyPressed(KeyCode key) {
		int state = glfwGetKey(window, key.getCode());
		return state == GLFW_PRESS || state == GLFW_REPEAT;
	}

	public static boolean isMouseButtonPressed(MouseCode button) {
		int state = glfwGetMouseButton(window, button.ordinal());
		return state == GLFW_PRESS;
	}

	public static Vector2f getMousePosition() {
		DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(window, x, y);
		return new Vector2f((float) x.get(), (float) y.get());
	}

	public static void setMousePosition(Vector2f pos) {
		glfwSetCursorPos(window, pos.x, pos.y);
	}

	public static void setCursor(boolean enabled) {
		glfwSetInputMode(window, GLFW_CURSOR, enabled ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
	}
}
