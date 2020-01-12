package com.kuzu.engine.core;

import com.kuzu.engine.rendering.Vertex;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Util {

	public static FloatBuffer createFloatBuffer(int size) {
		return BufferUtils.createFloatBuffer(size);
	}

	public static IntBuffer createIntBuffer(int size) {
		return BufferUtils.createIntBuffer(size);
	}

	public static ByteBuffer createByteBuffer(int size) {
		return BufferUtils.createByteBuffer(size);
	}

	public static IntBuffer createFlippedBuffer(int... values) {
		IntBuffer buffer = createIntBuffer(values.length);
		buffer.put(values);
		buffer.flip();

		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(Vertex[] vertices) {
		FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.LAYOUT.getStride());

		for (Vertex vertex : vertices) {
			buffer.put(vertex.getPos().x);
			buffer.put(vertex.getPos().y);
			buffer.put(vertex.getPos().z);
			buffer.put(vertex.getTexCoord().x);
			buffer.put(vertex.getTexCoord().y);
			buffer.put(vertex.getNormal().x);
			buffer.put(vertex.getNormal().y);
			buffer.put(vertex.getNormal().z);
			buffer.put(vertex.getBinormal().x);
			buffer.put(vertex.getBinormal().y);
			buffer.put(vertex.getBinormal().z);
			buffer.put(vertex.getTangent().x);
			buffer.put(vertex.getTangent().y);
			buffer.put(vertex.getTangent().z);
		}

		buffer.flip();
		return buffer;
	}

	public static FloatBuffer createFlippedBuffer(Matrix4f value) {
		FloatBuffer buffer = createFloatBuffer(4 * 4);

		value.get(buffer);
		return buffer;
	}
}
