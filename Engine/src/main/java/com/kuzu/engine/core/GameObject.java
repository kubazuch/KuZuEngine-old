package com.kuzu.engine.core;

import com.kuzu.engine.components.GameComponent;
import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.rendering.shader.Shader;

import java.util.ArrayList;

public class GameObject {
	private ArrayList<GameObject> children;
	private ArrayList<GameComponent> components;
	private Transform transform;
	private CoreEngine engine;

	public GameObject() {
		children = new ArrayList<>();
		components = new ArrayList<>();
		transform = new Transform();
		engine = null;
	}

	public GameObject addChild(GameObject child) {
		children.add(child);
		child.setEngine(engine);
//		child.getTransform().setParent(transform);

		return this;
	}

	public GameObject addComponent(GameComponent component) {
		components.add(component);
		component.setParent(this);

		return this;
	}

	public void inputAll(float delta) {
		input(delta);

		for (GameObject child : children)
			child.inputAll(delta);
	}

	public void updateAll(float delta) {
		update(delta);

		for (GameObject child : children)
			child.updateAll(delta);
	}

	public void renderAll(Shader shader, Camera camera) {
		render(shader, camera);

		for (GameObject child : children)
			child.renderAll(shader, camera);
	}

	public void input(float delta) {
		for (GameComponent component : components)
			component.input(delta);
	}

	public void update(float delta) {
		for (GameComponent component : components)
			component.update(delta);
	}

	public void render(Shader shader, Camera camera) {
		for (GameComponent component : components)
			component.render(shader, camera);
	}

	public ArrayList<GameObject> getAllAttached() {
		ArrayList<GameObject> result = new ArrayList<>();

		for (GameObject child : children)
			result.addAll(child.getAllAttached());

		result.add(this);
		return result;
	}

	public Transform getTransform() {
		return transform;
	}

	public void setEngine(CoreEngine engine) {
		if (this.engine != engine) {
			this.engine = engine;

			for (GameComponent component : components)
				component.addToEngine(engine);

			for (GameObject child : children)
				child.setEngine(engine);
		}
	}
}
