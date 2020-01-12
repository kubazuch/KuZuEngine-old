package com.kuzu.engine.event;

import com.kuzu.event.api.Event;

public class KeyEvents {
	public static class KeyEvent extends Event {
		protected int keyCode;

		protected KeyEvent(int keyCode) {
//			Optional<KeyCode> code = Arrays.stream(KeyCode.values()).filter(x -> x.getCode() == keyCode).findAny();
//			this.keyCode = code.orElseThrow(() -> new NoSuchElementException("Unknown key code: " + keyCode));
			this.keyCode = keyCode;
		}

		public int getKeyCode() {
			return keyCode;
		}
	}

	public static class KeyPressedEvent extends KeyEvent {
		private int repeatCount;

		public KeyPressedEvent(int keyCode, int repeatCount) {
			super(keyCode);
			this.repeatCount = repeatCount;
		}

		public int getRepeatCount() {
			return repeatCount;
		}

		@Override
		public String toString() {
			return "KeyPressedEvent: " + keyCode + "(" + repeatCount + " repeats)";
		}
	}

	public static class KeyReleasedEvent extends KeyEvent {
		public KeyReleasedEvent(int keyCode) {
			super(keyCode);
		}

		@Override
		public String toString() {
			return "KeyReleasedEvent: " + keyCode;
		}
	}

	public static class KeyTypedEvent extends KeyEvent {
		public KeyTypedEvent(int keyCode) {
			super(keyCode);
		}

		@Override
		public String toString() {
			return "KeyTypedEvent: " + keyCode;
		}
	}
}
