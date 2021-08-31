package com.kuzu.engine.event;

import com.kuzu.engine.core.Input;
import com.kuzu.engine.core.MouseCode;

import java.util.Arrays;

public class MouseEvents {
	public static class MouseMovedEvent {
		private float mouseX;
		private float mouseY;
		private Input input;

		public MouseMovedEvent(float mouseX, float mouseY, Input input) {
			this.mouseX = mouseX;
			this.mouseY = mouseY;
			this.input = input;
		}

		public float getX() {
			return mouseX;
		}

		public float getY() {
			return mouseY;
		}

		public Input getInput() {
			return input;
		}

		@Override
		public String toString() {
			return "MouseMovedEvent: " + mouseX + ", " + mouseY;
		}
	}

	public static class MouseScrolledEvent {
		private float xOffset;
		private float yOffset;
		private Input input;

		public MouseScrolledEvent(float xOffset, float yOffset, Input input) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
			this.input = input;
		}

		public float getxOffset() {
			return xOffset;
		}

		public float getyOffset() {
			return yOffset;
		}

		public Input getInput() {
			return input;
		}

		@Override
		public String toString() {
			return "MouseScrolledEvent: " + xOffset + ", " + yOffset;
		}
	}

	public static class MouseButtonEvent {
		protected MouseCode button;
		protected Input input;

		public MouseButtonEvent(int button, Input input) {
			this.button = Arrays.stream(MouseCode.values()).filter(x -> x.ordinal() == button).findAny().orElse(null);
			this.input = input;
		}

		public MouseCode getMouseButton() {
			return button;
		}

		public Input getInput() {
			return input;
		}
	}

	public static class MouseButtonPressedEvent extends MouseButtonEvent {
		public MouseButtonPressedEvent(int button, Input input) {
			super(button, input);
		}

		@Override
		public String toString() {
			return "MouseButtonPressedEvent: " + button;
		}
	}

	public static class MouseButtonReleasedEvent extends MouseButtonEvent {
		public MouseButtonReleasedEvent(int button, Input input) {
			super(button, input);
		}

		@Override
		public String toString() {
			return "MouseReleasedEvent: " + button;
		}
	}
}
