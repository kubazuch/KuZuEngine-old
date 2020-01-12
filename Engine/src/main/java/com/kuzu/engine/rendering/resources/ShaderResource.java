package com.kuzu.engine.rendering.resources;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;

public class ShaderResource {
	private int program;
	private HashMap<String, Integer> uniforms;
	private ArrayList<String> uniformNames;
	private ArrayList<String> uniformTypes;
	private int refCount;

	public ShaderResource() {
		this.program = glCreateProgram();
		this.refCount = 1;

		if (program == 0) {
			throw new RuntimeException("Shader construction failed: Could not find valid memory location in constructor!");
		}

		uniforms = new HashMap<>();
		uniformNames = new ArrayList<>();
		uniformTypes = new ArrayList<>();
	}

	@Override
	protected void finalize() throws Throwable {
		glDeleteProgram(program);
	}

	public void addReference() {
		refCount++;
	}

	public boolean removeReference() {
		refCount--;
		return refCount == 0;
	}

	public int getProgram() {
		return program;
	}

	public HashMap<String, Integer> getUniforms() {
		return uniforms;
	}

	public ArrayList<String> getUniformNames() {
		return uniformNames;
	}

	public ArrayList<String> getUniformTypes() {
		return uniformTypes;
	}
}
