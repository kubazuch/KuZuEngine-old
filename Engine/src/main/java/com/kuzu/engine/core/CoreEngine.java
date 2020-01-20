package com.kuzu.engine.core;

import com.kuzu.engine.event.WindowCloseEvent;
import com.kuzu.engine.event.WindowResizeEvent;
import com.kuzu.engine.rendering.Layer;
import com.kuzu.engine.rendering.LayerStack;
import com.kuzu.engine.rendering.RenderingEngine;
import com.kuzu.engine.rendering.Window;
import com.kuzu.event.EventBus;
import com.kuzu.event.api.EventPriority;
import com.kuzu.event.api.EventSubscriber;

import java.util.ListIterator;

import static org.lwjgl.opengl.GL11.glViewport;

public class CoreEngine {
	private boolean minimized = false;

	private boolean isRunning;
	private RenderingEngine renderingEngine;
	private int width;
	private int height;
	private String title;
	private Window window;
	private Input input;
	private EventBus windowEventBus;
	private double frameTime;

	private LayerStack layerStack = new LayerStack();

	public CoreEngine(int width, int height, double framerate, String title) {
		this.isRunning = false;
		this.width = width;
		this.height = height;
		this.title = title;
		this.frameTime = 1.0 / framerate;

		this.window = new Window(width, height, title);
		this.windowEventBus = new EventBus();
		this.window.setEventCallback(windowEventBus::post);

		this.renderingEngine = new RenderingEngine(window);

		this.windowEventBus.register(this);
		this.windowEventBus.register(renderingEngine);
	}

	public void pushLayer(Layer layer) {
		for (Layer l : layerStack)
			windowEventBus.unregister(l);

		layerStack.pushLayer(layer);
		layer.onAttach(this);

		ListIterator<Layer> reverse = layerStack.reverseIterator();
		while (reverse.hasPrevious())
			windowEventBus.register(reverse.previous());
	}

	public void pushOverlay(Layer overlay) {
		for (Layer l : layerStack)
			windowEventBus.unregister(l);

		layerStack.pushOverlay(overlay);
		overlay.onAttach(this);

		ListIterator<Layer> reverse = layerStack.reverseIterator();
		while (reverse.hasPrevious())
			windowEventBus.register(reverse.previous());
	}

	public void start() {
		if (isRunning) return;

		run();
	}

	public void stop() {
		if (!isRunning) return;
		isRunning = false;
	}

	private void run() {
		isRunning = true;

		int frames = 0;
		double frameCounter = 0;

		double lastTime = Time.getTime();
		double unprocessedTime = 0;

		while (isRunning) {
			boolean render = false;

			double startTime = Time.getTime();
			double passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime;
			frameCounter += passedTime;

			while (unprocessedTime > frameTime) {
				render = true;

				unprocessedTime -= frameTime;
				if (!minimized) {
					for (Layer layer : layerStack)
						layer.onUpdate((float) frameTime, window.getInput());
				}

				if (frameCounter >= 1.0) {
					window.setTitle(title + " | " + frames + "FPS");
					System.out.println(frames + "FPS");
					frames = 0;
					frameCounter = 0;
				}
			}

			if (render) {
				render();
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		cleanUp();
	}

	private void render() {
		for (Layer layer : layerStack) {
			layer.render(renderingEngine);
		}

		window.render();
	}

	private void cleanUp() {
		window.dispose();
		windowEventBus.shutdown();
		layerStack.dispose();
	}

	public RenderingEngine getRenderingEngine() {
		return renderingEngine;
	}

	public EventBus getWindowEventBus() {
		return windowEventBus;
	}

	@EventSubscriber
	public void onWindowCloseEvent(WindowCloseEvent ignored) {
		stop();
	}

	@EventSubscriber(priority = EventPriority.LOW)
	public void onWindowResizeEvent(WindowResizeEvent event) {
		glViewport(0, 0, event.getWidth(), event.getHeight());

		if (event.getWidth() == 0 || event.getHeight() == 0) {
			minimized = true;
			return;
		}

		minimized = false;
	}

	public int getWidth() {
		return this.window.getWidth();
	}

	public int getHeight() {
		return this.window.getHeight();
	}
}
