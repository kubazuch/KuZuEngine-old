package com.kuzu.engine.rendering;

import com.kuzu.engine.core.CoreEngine;
import com.kuzu.engine.core.Input;

public abstract class Layer {
	protected String debugName;

	public Layer() {
		this("Layer");
	}

	public Layer(String debugName) {
		this.debugName = debugName;
	}

	public void onAttach(CoreEngine engine) {

	}

	public void onDetach() {

	}

	public void dispose() {

	}

	public abstract void onUpdate(float delta, Input input);

	public String getName() {
		return debugName;
	}

	public abstract void render(RenderingEngine engine);

	public abstract double displayTimes(double dividend);

}
