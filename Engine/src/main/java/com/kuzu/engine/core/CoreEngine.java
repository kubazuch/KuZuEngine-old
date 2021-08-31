package com.kuzu.engine.core;

import com.kuzu.engine.event.EventUtil;
import com.kuzu.engine.event.WindowCloseEvent;
import com.kuzu.engine.event.WindowResizeEvent;
import com.kuzu.engine.rendering.Layer;
import com.kuzu.engine.rendering.LayerStack;
import com.kuzu.engine.rendering.RenderingEngine;
import com.kuzu.engine.rendering.Window;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ListIterator;

import static org.lwjgl.opengl.GL11.glViewport;

public class CoreEngine {
	private boolean minimized = false;

//	private ProfileTimer sleepTimer = new ProfileTimer();
//	private ProfileTimer swapBufferTimer = new ProfileTimer();
//	private ProfileTimer windowUpdateTimer = new ProfileTimer();

	private boolean isRunning;
	private RenderingEngine renderingEngine;
	private int width;
	private int height;
	private String title;
	private Window window;
	private Input input;
	private EventBus windowEventBus;
	private double frameTime;

	private int count = 0;

	private LayerStack layerStack = new LayerStack();

	public CoreEngine(int width, int height, double framerate, String title) {
		this.isRunning = false;
		this.width = width;
		this.height = height;
		this.title = title;
		this.frameTime = 1.0 / framerate;

		this.window = new Window(width, height, title);
		this.windowEventBus = EventBus.builder().logNoSubscriberMessages(false).build();
		this.window.setEventCallback(windowEventBus::post);

		this.renderingEngine = new RenderingEngine(window);

		EventUtil.register(this.windowEventBus, this);
		EventUtil.register(this.windowEventBus, renderingEngine);
	}

	public void pushLayer(Layer layer) {
		for (Layer l : layerStack)
			windowEventBus.unregister(l);

		layerStack.pushLayer(layer);
		layer.onAttach(this);

		ListIterator<Layer> reverse = layerStack.reverseIterator();
		while (reverse.hasPrevious())
			EventUtil.register(windowEventBus, reverse.previous());
	}

	public void pushOverlay(Layer overlay) {
		for (Layer l : layerStack)
			windowEventBus.unregister(l);

		layerStack.pushOverlay(overlay);
		overlay.onAttach(this);

		ListIterator<Layer> reverse = layerStack.reverseIterator();
		while (reverse.hasPrevious())
			EventUtil.register(windowEventBus, reverse.previous());
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

//					double totalTime = ((1000.0 * frameCounter)/((double)frames));
//					double totalMeasuredTime = 0.0;
//					for(Layer layer : layerStack)
//						totalMeasuredTime += layer.displayTimes(frames);
//					totalMeasuredTime += renderingEngine.displayRenderTime(frames);
//					totalMeasuredTime += sleepTimer.displayAndReset("Sleep time: ", frames);
//					totalMeasuredTime += windowUpdateTimer.displayAndReset("Window update time: ", frames);
//					totalMeasuredTime += swapBufferTimer.displayAndReset("Buffer swap time: ", frames);
//
//					System.out.println("Other time: " + (totalTime - totalMeasuredTime) + " ms");
//					System.out.println("Total time: " + totalTime + " ms");

					frames = 0;
					frameCounter = 0;
					if (count == 100)
						stop();
				}
			}

			if (render) {
				render();
				frames++;
			} else {
				try {
//					sleepTimer.startInvocation();
					Thread.sleep(1);
//					sleepTimer.stopInvocation();
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

		window.update();
		window.swapBuffers();
	}

	private void cleanUp() {
		window.dispose();
		layerStack.dispose();
	}

	public RenderingEngine getRenderingEngine() {
		return renderingEngine;
	}

	public EventBus getWindowEventBus() {
		return windowEventBus;
	}

	@Subscribe
	public void onWindowCloseEvent(WindowCloseEvent ignored) {
		stop();
	}

	@Subscribe(priority = -10)
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
