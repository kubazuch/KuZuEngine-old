package com.kuzu.engine.rendering.buffer;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

public class IndexBuffer {
	private int ibo;

	public IndexBuffer(IntBuffer buffer) {
		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}

	public void dispose() {
		glDeleteBuffers(ibo);
	}

	public void bind() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
	}

	public void unbind() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
}
