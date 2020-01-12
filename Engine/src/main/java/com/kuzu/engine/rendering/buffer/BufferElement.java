package com.kuzu.engine.rendering.buffer;

public class BufferElement {
	private String name;
	private ShaderDataType type;
	private boolean normalized;
	private int offset;

	public BufferElement(ShaderDataType type, String name) {
		this(type, name, false);
	}

	public BufferElement(ShaderDataType type, String name, boolean normalized) {
		this.name = name;
		this.type = type;
		this.normalized = normalized;
	}

	public int getComponentCount() {
		return type.getCount();
	}

	public String getName() {
		return name;
	}

	public boolean isNormalized() {
		return normalized;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getSize() {
		return type.getSize();
	}

	public int getType() {
		return type.getGLType();
	}
}
