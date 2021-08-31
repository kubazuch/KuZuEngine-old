package com.kuzu.engine.event;

public class WindowResizeEvent {
	private int width;
	private int height;

	public WindowResizeEvent(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "WindowResizeEvent: " + width + ", " + height;
	}
}
