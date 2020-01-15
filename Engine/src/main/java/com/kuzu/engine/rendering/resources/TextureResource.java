package com.kuzu.engine.rendering.resources;

import static org.lwjgl.opengl.GL11.glGenTextures;

public class TextureResource {
	private int id;
	private int refCount;

	public TextureResource() {
		this.id = glGenTextures();
		this.refCount = 1;
	}

	public void addReference() {
		refCount++;
	}

	public boolean removeReference() {
		refCount--;
		return refCount == 0;
	}

	public int getId() {
		return id;
	}
}
