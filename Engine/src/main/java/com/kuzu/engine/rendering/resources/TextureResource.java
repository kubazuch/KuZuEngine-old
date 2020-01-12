package com.kuzu.engine.rendering.resources;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;

public class TextureResource {
	private int id;
	private int refCount;

	public TextureResource() {
		this.id = glGenTextures();
		this.refCount = 1;
	}

	@Override
	protected void finalize() throws Throwable {
		glDeleteTextures(id);
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
