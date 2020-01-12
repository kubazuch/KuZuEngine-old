package com.kuzu.engine.rendering.buffer;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VertexBuffer {

	private int vbo;
	private BufferLayout layout;

	public VertexBuffer(FloatBuffer buffer) {
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}

	@Override
	protected void finalize() throws Throwable {
		cleanUp();
	}

	public void cleanUp() {
		glDeleteBuffers(vbo);
	}

	public void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
	}

	public void unbind() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public BufferLayout getLayout() {
		return layout;
	}

	public void setLayout(BufferLayout layout) {
		this.layout = layout;
	}
}
