package com.kuzu.engine.rendering;

import com.kuzu.engine.core.Input;
import com.kuzu.engine.event.KeyEvents.KeyPressedEvent;
import com.kuzu.engine.event.KeyEvents.KeyReleasedEvent;
import com.kuzu.engine.event.KeyEvents.KeyTypedEvent;
import com.kuzu.engine.event.MouseEvents.MouseButtonPressedEvent;
import com.kuzu.engine.event.MouseEvents.MouseButtonReleasedEvent;
import com.kuzu.engine.event.MouseEvents.MouseMovedEvent;
import com.kuzu.engine.event.MouseEvents.MouseScrolledEvent;
import com.kuzu.engine.event.WindowCloseEvent;
import com.kuzu.engine.event.WindowResizeEvent;
import com.kuzu.event.api.Event;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.jni.JNINativeInterface;

import java.util.function.Function;

import static org.lwjgl.glfw.GLFW.*;

public class Window {
	private static int GLFWWindowCount = 0;

	private long window;
	private WindowData data = new WindowData();
	private Input input;

	private GLFWErrorCallback errorCallback;
	private GLFWWindowSizeCallback windowSizeCallback;
	private GLFWWindowCloseCallback windowCloseCallback;
	private GLFWKeyCallback keyCallback;
	private GLFWCharCallback charCallback;
	private GLFWMouseButtonCallback mouseButtonCallback;
	private GLFWScrollCallback scrollCallback;
	private GLFWCursorPosCallback cursorPosCallback;

	public Window() {
		this(800, 600, "3D Engine");
	}

	public Window(int width, int height, String title) {
		this.data.width = width;
		this.data.height = height;
		this.data.title = title;
		this.input = new Input(this);
		init(width, height, title);
	}

	private void init(int width, int height, String title) {
		System.out.println("Creating window " + data.title + " (" + data.width + ", " + data.height + ")");
		if (GLFWWindowCount == 0) {
			boolean success = glfwInit();
			if (!success) throw new RuntimeException("Could not initialize GLFW!");
			glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		}

		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

		this.window = glfwCreateWindow(data.width, data.height, data.title, 0, 0);
		if (window == 0) throw new RuntimeException("Failed to create window!");
		++GLFWWindowCount;

		glfwMakeContextCurrent(this.window);
		GL.createCapabilities();
		glfwSetWindowUserPointer(this.window, JNINativeInterface.NewGlobalRef(this.data));
		setVSync(false);
		glfwShowWindow(this.window);

		//GLFW callbacks
		glfwSetWindowSizeCallback(this.window, (windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				WindowData data = MemoryUtil.memGlobalRefToObject(glfwGetWindowUserPointer(window));
				data.width = width;
				data.height = height;

				WindowResizeEvent event = new WindowResizeEvent(width, height);
				data.eventCallbackFn.apply(event);
			}
		}));

		glfwSetWindowCloseCallback(this.window, (windowCloseCallback = new GLFWWindowCloseCallback() {
			@Override
			public void invoke(long window) {
				WindowData data = MemoryUtil.memGlobalRefToObject(glfwGetWindowUserPointer(window));

				WindowCloseEvent event = new WindowCloseEvent();
				data.eventCallbackFn.apply(event);
			}
		}));

		glfwSetKeyCallback(this.window, (keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == -1) return;
				WindowData data = MemoryUtil.memGlobalRefToObject(glfwGetWindowUserPointer(window));

				switch (action) {
					case GLFW_PRESS: {
						KeyPressedEvent event = new KeyPressedEvent(key, 0, input);
						data.eventCallbackFn.apply(event);
						break;
					}
					case GLFW_RELEASE: {
						KeyReleasedEvent event = new KeyReleasedEvent(key, input);
						data.eventCallbackFn.apply(event);
						break;
					}
					case GLFW_REPEAT: {
						KeyPressedEvent event = new KeyPressedEvent(key, 1, input);
						data.eventCallbackFn.apply(event);
						break;
					}
				}
			}
		}));

		glfwSetCharCallback(this.window, (charCallback = new GLFWCharCallback() {
			@Override
			public void invoke(long window, int codepoint) {
				WindowData data = MemoryUtil.memGlobalRefToObject(glfwGetWindowUserPointer(window));

				KeyTypedEvent event = new KeyTypedEvent(codepoint, input);
				data.eventCallbackFn.apply(event);
			}
		}));

		glfwSetMouseButtonCallback(this.window, (mouseButtonCallback = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				WindowData data = MemoryUtil.memGlobalRefToObject(glfwGetWindowUserPointer(window));
				switch (action) {
					case GLFW_PRESS: {
						MouseButtonPressedEvent event = new MouseButtonPressedEvent(button, input);
						data.eventCallbackFn.apply(event);
						break;
					}
					case GLFW_RELEASE: {
						MouseButtonReleasedEvent event = new MouseButtonReleasedEvent(button, input);
						data.eventCallbackFn.apply(event);
						break;
					}
				}
			}
		}));

		glfwSetScrollCallback(this.window, (scrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				WindowData data = MemoryUtil.memGlobalRefToObject(glfwGetWindowUserPointer(window));

				MouseScrolledEvent event = new MouseScrolledEvent((float) xoffset, (float) yoffset, input);
				data.eventCallbackFn.apply(event);
			}
		}));

		glfwSetCursorPosCallback(this.window, (cursorPosCallback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				WindowData data = MemoryUtil.memGlobalRefToObject(glfwGetWindowUserPointer(window));

				MouseMovedEvent event = new MouseMovedEvent((float) xpos, (float) ypos, input);
				data.eventCallbackFn.apply(event);
			}
		}));
	}

	public void render() {
		glfwPollEvents();
		glfwSwapBuffers(this.window);
	}

	public int getWidth() {
		return this.data.width;
	}

	public int getHeight() {
		return this.data.height;
	}

	public String getTitle() {
		return this.data.title;
	}

	public void setTitle(String title) {
		glfwSetWindowTitle(window, title);
	}

	public boolean isVSync() {
		return this.data.vSync;
	}

	public void setVSync(boolean enabled) {
		glfwSwapInterval(enabled ? 1 : 0);
		this.data.vSync = enabled;
	}

	public void dispose() {
		glfwDestroyWindow(this.window);
		--GLFWWindowCount;
		if (GLFWWindowCount == 0) glfwTerminate();
	}

	public void setEventCallback(Function<Event, Boolean> fn) {
		this.data.eventCallbackFn = fn;
	}

	public boolean isCloseRequested() {
		return glfwWindowShouldClose(this.window);
	}

	public long getWindowHandle() {
		return window;
	}

	public Input getInput() {
		return input;
	}

	public static class WindowData {
		public String title;
		public int width, height;
		public boolean vSync;
		public Function<Event, Boolean> eventCallbackFn;
	}
}
