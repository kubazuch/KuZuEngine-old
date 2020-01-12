package com.kuzu.engine.rendering.resources;

import com.kuzu.engine.core.Util;
import com.kuzu.engine.rendering.Vertex;
import com.kuzu.engine.rendering.VertexArray;
import com.kuzu.engine.rendering.buffer.IndexBuffer;
import com.kuzu.engine.rendering.buffer.VertexBuffer;

import static org.lwjgl.opengl.GL15.*;

public class MeshResource {
	private VertexArray vao;
	private int size;
	private int refCount;

	public MeshResource(Vertex[] vertices, int[] indices) {
		this.size = indices.length;
		this.refCount = 1;

		vao = new VertexArray();
		VertexBuffer vbo = new VertexBuffer(Util.createFlippedBuffer(vertices));
		vbo.setLayout(Vertex.LAYOUT);
		IndexBuffer ibo = new IndexBuffer(Util.createFlippedBuffer(indices));
		vao.addVertexBuffer(vbo);
		vao.setIndexBuffer(ibo);
		vao.unbind();
	}

	public void draw() {
		vao.bind();
		glDrawElements(GL_TRIANGLES, size, GL_UNSIGNED_INT, 0);
		vao.unbind();
	}

	public void drawNormals() {
		vao.bind();
		glDrawElements(GL_POINTS, size, GL_UNSIGNED_INT, 0);
		vao.unbind();
	}

	@Override
	protected void finalize() throws Throwable {
		vao.cleanUp();
	}

	public void addReference() {
		refCount++;
	}

	public boolean removeReference() {
		refCount--;
		return refCount == 0;
	}
}
