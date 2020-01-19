package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.core.Util;
import com.kuzu.engine.rendering.Material;
import com.kuzu.engine.rendering.resources.ShaderResource;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class Shader {
	private static final Pattern INCLUDE_PATTERN = Pattern.compile("^\\s*#include \"(\\S+?\\.\\S{1,3})\"", Pattern.MULTILINE);
	private static final Pattern ATTRIB_PATTERN = Pattern.compile("^\\s*attribute (\\S+) (\\S+);", Pattern.MULTILINE);
	private static final Pattern ARRAY_CONST_PATTERN = Pattern.compile("^\\s*const (\\S+) (\\S+) = (\\S*);", Pattern.MULTILINE);
	private static final Pattern UNIFORM_PATTERN = Pattern.compile("^\\s*uniform (\\S+) (\\S+);", Pattern.MULTILINE);
	private static final Pattern STRUCT_PATTERN = Pattern.compile("^\\s*struct (\\S+?) \\{\\s*((?:\\s*.*? .*?;)+)\\s*};", Pattern.MULTILINE);
	private static final Pattern STRUCT_ELEMENT_PATTERN = Pattern.compile("^\\s*([^/\\s]+?) (\\S+?);", Pattern.MULTILINE);
	private static final Pattern ARRAY_PATTERN = Pattern.compile("\\[(\\S+?)]");

	private static HashMap<String, ShaderResource> loadedShaders = new HashMap<>();

	protected ShaderResource resource;
	private String fileName;

	public Shader(String fileName, boolean geometry) {
		this.fileName = fileName;
		ShaderResource oldResource = loadedShaders.get(fileName);

		if (oldResource != null) {
			resource = oldResource;
			resource.addReference();
		} else {
			resource = new ShaderResource();

			String vertexShaderText = loadShader(fileName + ".vs");
			addVertexShader(vertexShaderText);
			addAllAttributes(vertexShaderText);
			findConsts(vertexShaderText);

			if (geometry) {
				String geometryText = loadShader(fileName + ".gs");
				addGeometryShader(geometryText);
				addAllUniforms(geometryText);
				findConsts(geometryText);
			}

			String fragmentShaderText = loadShader(fileName + ".fs");
			addFragmentShader(fragmentShaderText);
			findConsts(fragmentShaderText);

			linkShader();

			addAllUniforms(vertexShaderText);
			addAllUniforms(fragmentShaderText);

			loadedShaders.put(fileName, resource);
		}
	}

	private static String loadShader(String fileName) {

		StringBuilder shaderSource = new StringBuilder();

		try {
			InputStream in = Shader.class.getResourceAsStream("/res/shaders/" + fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			Stream<String> lines = reader.lines();
			lines.forEachOrdered(line -> {
				Matcher m = INCLUDE_PATTERN.matcher(line);
				if (m.matches())
					shaderSource.append(loadShader(m.group(1))).append('\n');
				else
					shaderSource.append(line).append('\n');
			});
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.exit(1);
		}

		return shaderSource.toString();
	}

	@Override
	protected void finalize() throws Throwable {
		if (resource.removeReference() && !fileName.isEmpty())
			loadedShaders.remove(fileName);
	}

	public void dispose() {
		if (resource.removeReference()) {
			resource.dispose();
			if (!fileName.isEmpty())
				loadedShaders.remove(fileName);
		}
	}

	public void unbind() {
		glUseProgram(0);
	}

	public void bind() {
		glUseProgram(resource.getProgram());
	}

	public void updateUniforms(Matrix4f transformMat, Camera camera, Material material) {
	}

	private void addAllAttributes(String shaderText) {
		Matcher m = ATTRIB_PATTERN.matcher(shaderText);

		int attribNumber = 0;
		while (m.find()) {
			String attributeName = m.group(2);
			setAttribLocation(attributeName, attribNumber++);
		}
	}

	private HashMap<String, ArrayList<GLSLStruct>> findUniformStructs(String shaderText) {
		HashMap<String, ArrayList<GLSLStruct>> result = new HashMap<>();

		Matcher structMatcher = STRUCT_PATTERN.matcher(shaderText);
		while (structMatcher.find()) {
			String structName = structMatcher.group(1);
			String structComponents = structMatcher.group(2);

			ArrayList<GLSLStruct> glslStructs = new ArrayList<>();
			Matcher componentMatcher = STRUCT_ELEMENT_PATTERN.matcher(structComponents);
			while (componentMatcher.find()) {
				String componentType = componentMatcher.group(1);
				String componentName = componentMatcher.group(2);
				GLSLStruct struct = new GLSLStruct(componentName, componentType);
				glslStructs.add(struct);
			}

			result.put(structName, glslStructs);
		}

		return result;
	}

	private void findConsts(String shaderText) {
		Matcher m = ARRAY_CONST_PATTERN.matcher(shaderText);
		while (m.find()) {
			String constType = m.group(1);
			String constName = m.group(2);
			if (constType.equals("int")) {
				int constValue = Integer.parseInt(m.group(3));
				resource.getIntegerConsts().put(constName, constValue);
			} else if (constType.equals("float")) {
				float constValue = Float.parseFloat(m.group(3));
				resource.getFloatConsts().put(constName, constValue);
			}
		}
	}

	private void addAllUniforms(String shaderText) {
		HashMap<String, ArrayList<GLSLStruct>> structs = findUniformStructs(shaderText);
		Matcher uniformMatcher = UNIFORM_PATTERN.matcher(shaderText);
		while (uniformMatcher.find()) {
			String uniformType = uniformMatcher.group(1);
			String uniformName = uniformMatcher.group(2);

			resource.getUniformNames().add(uniformName);
			resource.getUniformTypes().add(uniformType);

			Matcher m = ARRAY_PATTERN.matcher(uniformName);
			if (m.find()) {
				String countString = m.group(1);
				int count;
				try {
					count = Integer.parseInt(countString);
				} catch (NumberFormatException e) {
					count = resource.getIntegerConsts().get(countString);
				}

				for (int i = 0; i < count; i++) {
					addUniform(m.replaceFirst('[' + String.valueOf(i) + ']'), uniformType, structs);
				}
			} else {
				addUniform(uniformName, uniformType, structs);
			}
		}
	}

	private void addUniform(String uniformName, String uniformType, HashMap<String, ArrayList<GLSLStruct>> structs) {
		boolean addThis = true;
		ArrayList<GLSLStruct> structComponents = structs.get(uniformType);
		if (structComponents != null) {
			addThis = false;
			for (GLSLStruct struct : structComponents) {
				Matcher m = ARRAY_PATTERN.matcher(struct.name);
				if (m.find()) {
					String countString = m.group(1);
					int count;
					try {
						count = Integer.parseInt(countString);
					} catch (NumberFormatException e) {
						count = resource.getIntegerConsts().get(countString);
					}

					for (int i = 0; i < count; i++) {
						addUniform(uniformName + "." + m.replaceFirst('[' + String.valueOf(i) + ']'), struct.type, structs);
					}
				} else {
					addUniform(uniformName + "." + struct.name, struct.type, structs);
				}
			}
		}

		if (!addThis)
			return;

		int uniformLocation = glGetUniformLocation(resource.getProgram(), uniformName);
		if (uniformLocation == -1) {
			throw new IllegalArgumentException("Error: Could not find uniform: " + uniformName);
		}

		resource.getUniforms().put(uniformName, uniformLocation);
	}

	private void addVertexShader(String text) {
		addShader(text, GL_VERTEX_SHADER);
	}

	private void addGeometryShader(String text) {
		addShader(text, GL_GEOMETRY_SHADER);
	}

	private void addFragmentShader(String text) {
		addShader(text, GL_FRAGMENT_SHADER);
	}

	private void setAttribLocation(String name, int index) {
		glBindAttribLocation(resource.getProgram(), index, name);
	}

	public void linkShader() {
		glLinkProgram(resource.getProgram());

		int success = glGetProgrami(resource.getProgram(), GL_LINK_STATUS);
		if (success == GL_FALSE) {
			String info = glGetProgramInfoLog(resource.getProgram());
			glDeleteProgram(resource.getProgram());
			throw new RuntimeException("Program linking failed: " + info);
		}

		glValidateProgram(resource.getProgram());
		success = glGetProgrami(resource.getProgram(), GL_VALIDATE_STATUS);
		if (success == GL_FALSE) {
			String info = glGetProgramInfoLog(resource.getProgram());
			glDeleteProgram(resource.getProgram());
			throw new RuntimeException("Program validation failed: " + info);
		}
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

		glAttachShader(resource.getProgram(), shader);
	}

	public void setUniformi(String uniformName, int value) {
		glUniform1i(resource.getUniforms().get(uniformName), value);
	}

	public void setUniformf(String uniformName, float value) {
		glUniform1f(resource.getUniforms().get(uniformName), value);
	}

	public void setUniform(String uniformName, Vector3f value) {
		glUniform3f(resource.getUniforms().get(uniformName), value.x, value.y, value.z);
	}

	public void setUniform(String uniformName, Matrix4f value) {
		glUniformMatrix4fv(resource.getUniforms().get(uniformName), false, Util.createFlippedBuffer(value));
	}

	private class GLSLStruct {
		public String name;
		public String type;

		public GLSLStruct(String name, String type) {
			this.name = name;
			this.type = type;
		}
	}
}
