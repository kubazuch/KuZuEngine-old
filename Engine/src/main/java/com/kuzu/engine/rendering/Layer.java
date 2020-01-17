package com.kuzu.engine.rendering;

public abstract class Layer {
	protected String debugName;

	public Layer() {
		this("Layer");
	}

	public Layer(String debugName) {
		this.debugName = debugName;
	}

	public void onAttach() {

	}

	public void onDetach() {

	}

	public void dispose() {

	}

	public abstract void onUpdate(float delta);

	public String getName() {
		return debugName;
	}

	public abstract void render();

}
