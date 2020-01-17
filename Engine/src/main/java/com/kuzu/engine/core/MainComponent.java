package com.kuzu.engine.core;

import com.kuzu.engine.event.WindowCloseEvent;
import com.kuzu.engine.event.WindowResizeEvent;
import com.kuzu.engine.rendering.Layer;
import com.kuzu.engine.rendering.LayerStack;
import com.kuzu.engine.rendering.RenderUtil;
import com.kuzu.engine.rendering.Window;
import com.kuzu.event.EventBus;
import com.kuzu.event.api.EventSubscriber;

import java.util.ListIterator;

import static org.lwjgl.opengl.GL11.glViewport;

public class MainComponent {
	public static final double FRAME_CAP = 5000.0;
	public static MainComponent INSTANCE = new MainComponent();

	private boolean minimized = false;

	private boolean isRunning;
//	private RenderingEngine renderingEngine;
	private int width;
	private int height;
	private Window window;
	private EventBus windowEventBus;
	private double frameTime;

	private LayerStack layerStack = new LayerStack();

	public MainComponent() {
		isRunning = false;
		window = new Window();
		windowEventBus = new EventBus();
		window.setEventCallback(windowEventBus::post);
		Input.setWindow(window.getWindowHandle());
		RenderUtil.initGraphics();

		windowEventBus.register(this);
		windowEventBus.register(Input.class);
	}

	public void pushLayer(Layer layer) {
		for (Layer l : layerStack)
			windowEventBus.unregister(l);

		layerStack.pushLayer(layer);
		layer.onAttach();

		ListIterator<Layer> reverse = layerStack.reverseIterator();
		while (reverse.hasPrevious())
			windowEventBus.register(reverse.previous());
	}

	public void pushOverlay(Layer overlay) {
		for (Layer l : layerStack)
			windowEventBus.unregister(l);

		layerStack.pushOverlay(overlay);
		overlay.onAttach();

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

		final double frameTime = 1.0 / FRAME_CAP;

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
						layer.onUpdate((float) frameTime);
				}

				if (frameCounter >= 1.0) {
					System.out.println(minimized + " " + frames);
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
		RenderUtil.clearScreen();

		for (Layer layer : layerStack) {
			layer.render();
		}

		window.render();
	}

	private void cleanUp() {
		window.dispose();
		windowEventBus.shutdown();
		layerStack.dispose();
	}

	@EventSubscriber
	public void onWindowCloseEvent(WindowCloseEvent ignored) {
		stop();
	}

	@EventSubscriber
	public void onWindowResizeEvent(WindowResizeEvent event) {
		glViewport(0, 0, event.getWidth(), event.getHeight());

		if (event.getWidth() == 0 || event.getHeight() == 0) {
			minimized = true;
			return;
		}

		minimized = false;

		//TODO: Renderer on resize
	}

	public int getWidth() {
		return this.window.getWidth();
	}

	public int getHeight() {
		return this.window.getHeight();
	}

//	@EventSubscriber
//	public void onEvent(Event e){
//		System.out.println(e);
//	}
}
