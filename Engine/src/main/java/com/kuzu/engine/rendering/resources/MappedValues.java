package com.kuzu.engine.rendering.resources;

import org.joml.Vector3f;

import java.util.HashMap;

public class MappedValues {
	private HashMap<String, Vector3f> vector3fHashMap;
	private HashMap<String, Float> floatHashMap;

	public MappedValues() {
		vector3fHashMap = new HashMap<>();
		floatHashMap = new HashMap<>();
	}

	public void addVector3f(String name, Vector3f vector3f) {
		vector3fHashMap.put(name, vector3f);
	}

	public void addFloat(String name, float floatValue) {
		floatHashMap.put(name, floatValue);
	}

	public Vector3f getVector3f(String name) {
		return vector3fHashMap.getOrDefault(name, new Vector3f());
	}

	public float getFloat(String name) {
		return floatHashMap.getOrDefault(name, 0f);
	}
}