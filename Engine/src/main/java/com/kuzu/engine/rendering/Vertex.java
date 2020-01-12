package com.kuzu.engine.rendering;

import com.kuzu.engine.rendering.buffer.BufferElement;
import com.kuzu.engine.rendering.buffer.BufferLayout;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static com.kuzu.engine.rendering.buffer.ShaderDataType.FLOAT2;
import static com.kuzu.engine.rendering.buffer.ShaderDataType.FLOAT3;

public class Vertex {
	public static final BufferLayout LAYOUT = new BufferLayout(
			new BufferElement(FLOAT3, "a_Position"),
			new BufferElement(FLOAT2, "a_texCoord"),
			new BufferElement(FLOAT3, "a_normal"),
			new BufferElement(FLOAT3, "a_binormal"),
			new BufferElement(FLOAT3, "a_tangent")
	);

	private Vector3f pos;
	private Vector2f texCoord;
	private Vector3f normal;
	private Vector3f binormal;
	private Vector3f tangent;

	public Vertex(Vector3f pos) {
		this(pos, new Vector2f());
	}

	public Vertex(Vector3f pos, Vector2f texCoord) {
		this(pos, texCoord, new Vector3f());
	}

	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal) {
		this(pos, texCoord, normal, new Vector3f());
	}

	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal, Vector3f tangent) {
		this(pos, texCoord, normal, new Vector3f(), tangent);
	}

	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal, Vector3f binormal, Vector3f tangent) {
		this.pos = pos;
		this.texCoord = texCoord;
		this.normal = normal;
		this.binormal = binormal;
		this.tangent = tangent;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Vector2f getTexCoord() {
		return texCoord;
	}

	public void setTexCoord(Vector2f texCoord) {
		this.texCoord = texCoord;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}

	public Vector3f getBinormal() {
		return binormal;
	}

	public void setBinormal(Vector3f binormal) {
		this.binormal = binormal;
	}

	public Vector3f getTangent() {
		return tangent;
	}

	public void setTangent(Vector3f tangent) {
		this.tangent = tangent;
	}
}
