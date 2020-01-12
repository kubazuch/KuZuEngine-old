package com.kuzu.engine.rendering;

import com.kuzu.engine.rendering.resources.MappedValues;

import java.util.HashMap;

public class Material extends MappedValues {
	private HashMap<String, Texture> textureHashMap;

	public Material(Texture diffuse) {
		super();
		textureHashMap = new HashMap<>();
		addTexture("diffuse", diffuse);
	}

	public Material(Texture diffuse, float specularIntensity, float specularPower, Texture normal,
	                Texture dispMap, float dispMapScale, float dispMapOffset) {
		super();
		textureHashMap = new HashMap<>();
		addTexture("diffuse", diffuse);
		addFloat("specularIntensity", specularIntensity);
		addFloat("specularPower", specularPower);
		addTexture("normalMap", normal);
		addTexture("dispMap", dispMap);

		float baseBias = dispMapScale / 2f;
		addFloat("dispMapScale", dispMapScale);
		addFloat("dispMapBias", -baseBias + baseBias * dispMapOffset);
	}

	public void addTexture(String name, Texture texture) {
		textureHashMap.put(name, texture);
	}

	public Texture getTexture(String name) {
		return textureHashMap.getOrDefault(name, new Texture("color.png"));
	}
}
