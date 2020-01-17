package com.kuzu.engine.rendering;

import com.kuzu.engine.rendering.buffer.BufferElement;
import com.kuzu.engine.rendering.buffer.BufferLayout;
import com.kuzu.engine.rendering.buffer.IndexBuffer;
import com.kuzu.engine.rendering.buffer.VertexBuffer;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray {

	private int vao;
	private int vertexBufferIndex = 0;
	private ArrayList<VertexBuffer> vertexBuffers = new ArrayList<>();
	private IndexBuffer indexBuffer;

	public VertexArray() {
		vao = glGenVertexArrays();
	}


	public void dispose() {
		glDeleteVertexArrays(vao);
		vertexBuffers.forEach(VertexBuffer::dispose);
		vertexBuffers.clear();
		indexBuffer.dispose();
	}

	public void bind() {
		glBindVertexArray(vao);
	}

	public void unbind() {
		glBindVertexArray(0);
	}

	public void addVertexBuffer(VertexBuffer vertexBuffer) {
		if (vertexBuffer.getLayout() == null || vertexBuffer.getLayout().getElements().size() == 0)
			throw new RuntimeException("VertexBuffer has no layout!");

		glBindVertexArray(vao);
		vertexBuffer.bind();

		BufferLayout layout = vertexBuffer.getLayout();
		for (BufferElement element : layout.getElements()) {
			glEnableVertexAttribArray(vertexBufferIndex);
			glVertexAttribPointer(vertexBufferIndex,
					element.getComponentCount(),
					element.getType(),
					element.isNormalized(),
					layout.getStride(),
					element.getOffset());
			vertexBufferIndex++;
		}

		vertexBuffers.add(vertexBuffer);
	}

	public ArrayList<VertexBuffer> getVertexBuffers() {
		return vertexBuffers;
	}

	public IndexBuffer getIndexBuffer() {
		return indexBuffer;
	}

	public void setIndexBuffer(IndexBuffer indexBuffer) {
		glBindVertexArray(vao);
		indexBuffer.bind();
		this.indexBuffer = indexBuffer;
	}
}
