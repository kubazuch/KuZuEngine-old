package com.kuzu.engine.rendering.buffer;

import java.util.Arrays;
import java.util.List;

public class BufferLayout {
	private List<BufferElement> elements;
	private int stride;

	public BufferLayout(BufferElement... elements) {
		this.elements = Arrays.asList(elements);
		calculateOffsetsAndStride();
	}

	private void calculateOffsetsAndStride() {
		stride = 0;
		for (BufferElement element : elements) {
			element.setOffset(stride);
			stride += element.getSize();
		}
	}

	public int getStride() {
		return stride;
	}

	public List<BufferElement> getElements() {
		return elements;
	}

	@Override
	public String toString() {
		return "BufferLayout{" +
				"elements=" + elements +
				", stride=" + stride +
				'}';
	}
}
