package com.kuzu.engine.core;

import com.kuzu.engine.components.GameComponent;
import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.rendering.RenderingEngine;
import com.kuzu.engine.rendering.shader.Shader;
import com.kuzu.event.EventBus;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class GameObject {
	private ArrayList<GameObject> children;
	private ArrayList<GameComponent> components;
	private Transform transform;
	private CoreEngine engine;

	public GameObject() {
		this(new Vector3f(), new Quaternionf(), new Vector3f(1));
	}

	public GameObject(Vector3f pos) {
		this(pos, new Quaternionf(), new Vector3f(1));
	}

	public GameObject(Vector3f pos, Quaternionf rot) {
		this(pos, rot, new Vector3f(1));
	}

	public GameObject(Vector3f pos, Quaternionf rot, Vector3f scale) {
		children = new ArrayList<>();
		components = new ArrayList<>();
		transform = new Transform(pos, rot, scale);
	}

	public GameObject addChild(GameObject child) {
		children.add(child);
		child.setEngine(engine);
		child.getTransform().setParent(transform);

		return this;
	}

	public GameObject addComponent(GameComponent component) {
		components.add(component);
		component.setParent(this);

		return this;
	}

	public void inputAll(float delta, Input input) {
		input(delta, input);

		for (GameObject child : children)
			child.inputAll(delta, input);
	}

	public void updateAll(float delta) {
		update(delta);

		for (GameObject child : children)
			child.updateAll(delta);
	}

	public void renderAll(Shader shader, RenderingEngine engine, Camera camera) {
		render(shader, engine, camera);

		for (GameObject child : children)
			child.renderAll(shader, engine, camera);
	}

	private void input(float delta, Input input) {
		for (GameComponent component : components)
			component.input(delta, input);
	}

	private void update(float delta) {
		for (GameComponent component : components)
			component.update(delta);
	}

	private void render(Shader shader, RenderingEngine engine, Camera camera) {
		for (GameComponent component : components)
			component.render(shader, engine, camera);
	}

	public ArrayList<GameObject> getAllAttached() {
		ArrayList<GameObject> result = new ArrayList<>();

		for (GameObject child : children)
			result.addAll(child.getAllAttached());

		result.add(this);
		return result;
	}

	public GameObject registerToEventBus(EventBus bus) {
		for (GameObject child : children)
			child.registerToEventBus(bus);

		bus.register(this);
		components.forEach(bus::register);

		return this;
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
