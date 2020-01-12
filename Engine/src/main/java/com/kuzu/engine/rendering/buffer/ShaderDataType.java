package com.kuzu.engine.rendering.buffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;

public enum ShaderDataType {
	FLOAT(1, 4, GL_FLOAT),
	FLOAT2(2, 4, GL_FLOAT),
	FLOAT3(3, 4, GL_FLOAT),
	FLOAT4(4, 4, GL_FLOAT),
	MAT3(9, 4, GL_FLOAT),
	MAT4(16, 4, GL_FLOAT),
	INT(1, 4, GL_INT),
	INT2(2, 4, GL_INT),
	INT3(3, 4, GL_INT),
	INT4(4, 4, GL_INT);

	private int count;
	private int size;
	private int glType;

	ShaderDataType(int count, int sizeOfOne, int glType) {
		this.count = count;
		this.size = count * sizeOfOne;
		this.glType = glType;
	}

	public int getCount() {
		return count;
	}

	public int getSize() {
		return size;
	}

	public int getGLType() {
		return glType;
	}
}
