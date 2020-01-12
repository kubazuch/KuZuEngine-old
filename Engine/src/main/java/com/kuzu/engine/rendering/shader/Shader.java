package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.core.Util;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class Shader {
	private int program;
	private HashMap<String, Integer> uniforms = new HashMap<>();

	public Shader() {
		program = glCreateProgram();

		if (program == 0) {
			throw new RuntimeException("Shader construction failed: Could not find valid memory location in constructor!");
		}
	}

	public void bind() {
		glUseProgram(program);
	}

	public void unbind() {
		glUseProgram(0);
	}

	protected void bindAttribLocation(int index, String name) {
		glBindAttribLocation(program, index, name);
	}

	protected void addUniform(String uniform) {
		if (uniforms.containsKey(uniform)) return;

		int uniformLocation = glGetUniformLocation(program, uniform);
		if (uniformLocation == -1) {
			throw new IllegalArgumentException("Error: Could not find uniform: " + uniform);
		}

		uniforms.put(uniform, uniformLocation);
	}

	public void addVertexShader(String text) {
		addShader(text, GL_VERTEX_SHADER);
	}

	public void addGeometryShader(String text) {
		addShader(text, GL_GEOMETRY_SHADER);
	}

	public void addFragmentShader(String text) {
		addShader(text, GL_FRAGMENT_SHADER);
	}

	public void addShader(String text, int type) {
		int shader = glCreateShader(type);
		if (shader == 0) {
			throw new RuntimeException("Shader construction failed: Could not find valid memory when adding shader!");
		}

		glShaderSource(shader, text);
		glCompileShader(shader);

		int success = glGetShaderi(shader, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			String info = glGetShaderInfoLog(shader);
			glDeleteShader(shader);
			switch (type) {
				case GL_VERTEX_SHADER:
					throw new RuntimeException("Vertex shader construction failed: " + info);
				case GL_GEOMETRY_SHADER:
					throw new RuntimeException("Geometry shader construction failed: " + info);
				case GL_FRAGMENT_SHADER:
					throw new RuntimeException("Fragment shader construction failed: " + info);
			}
		}

		glAttachShader(program, shader);
	}

	public void linkShader() {
		glLinkProgram(program);

		int success = glGetProgrami(program, GL_LINK_STATUS);
		if (success == GL_FALSE) {
			String info = glGetProgramInfoLog(program);
			glDeleteProgram(program);
			throw new RuntimeException("Program linking failed: " + info);
		}

		glValidateProgram(program);
		success = glGetProgrami(program, GL_VALIDATE_STATUS);
		if (success == GL_FALSE) {
			String info = glGetProgramInfoLog(program);
			glDeleteProgram(program);
			throw new RuntimeException("Program validation failed: " + info);
		}
	}

	public void setUniformi(String uniformName, int value) {
		glUniform1i(uniforms.get(uniformName), value);
	}

	public void setUniformf(String uniformName, float value) {
		glUniform1f(uniforms.get(uniformName), value);
	}

	public void setUniform3f(String uniformName, Vector3f value) {
		glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
	}

	public void setUniformMat4(String uniformName, Matrix4f value) {
		glUniformMatrix4fv(uniforms.get(uniformName), false, Util.createFlippedBuffer(value));
	}
}
