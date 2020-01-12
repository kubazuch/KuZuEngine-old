package com.kuzu.engine.event;

import com.kuzu.engine.core.MouseCode;
import com.kuzu.event.api.Event;

import java.util.Arrays;

public class MouseEvents {
	public static class MouseMovedEvent extends Event {
		private float mouseX;
		private float mouseY;

		public MouseMovedEvent(float mouseX, float mouseY) {
			this.mouseX = mouseX;
			this.mouseY = mouseY;
		}

		public float getX() {
			return mouseX;
		}

		public float getY() {
			return mouseY;
		}

		@Override
		public String toString() {
			return "MouseMovedEvent: " + mouseX + ", " + mouseY;
		}
	}

	public static class MouseScrolledEvent extends Event {
		private float xOffset;
		private float yOffset;

		public MouseScrolledEvent(float xOffset, float yOffset) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}

		public float getxOffset() {
			return xOffset;
		}

		public float getyOffset() {
			return yOffset;
		}

		@Override
		public String toString() {
			return "MouseScrolledEvent: " + xOffset + ", " + yOffset;
		}
	}

	public static class MouseButtonEvent extends Event {
		protected MouseCode button;

		public MouseButtonEvent(int button) {
			this.button = Arrays.stream(MouseCode.values()).filter(x -> x.ordinal() == button).findAny().orElse(null);
		}

		public MouseCode getMouseButton() {
			return button;
		}
	}

	public static class MouseButtonPressedEvent extends MouseButtonEvent {
		public MouseButtonPressedEvent(int button) {
			super(button);
		}

		@Override
		public String toString() {
			return "MouseButtonPressedEvent: " + button;
		}
	}

	public static class MouseButtonReleasedEvent extends MouseButtonEvent {
		public MouseButtonReleasedEvent(int button) {
			super(button);
		}

		@Override
		public String toString() {
			return "MouseReleasedEvent: " + button;
		}
	}
}
