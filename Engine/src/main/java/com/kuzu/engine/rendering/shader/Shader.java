package com.kuzu.engine.rendering.shader;

import com.kuzu.engine.core.Util;
import com.kuzu.engine.rendering.resources.ShaderResource;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

public class Shader {
	private static HashMap<String, ShaderResource> loadedShaders = new HashMap<>();

	private ShaderResource resource;
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

			if (geometry) {
				String geometryText = loadShader(fileName + ".gs");
				addGeometryShader(geometryText);
				addAllUniforms(geometryText);
			}

			String fragmentShaderText = loadShader(fileName + ".fs");
			addFragmentShader(fragmentShaderText);

			linkShader();

			addAllUniforms(vertexShaderText);
			addAllUniforms(fragmentShaderText);

			loadedShaders.put(fileName, resource);
		}
	}

	private static String loadShader(String fileName) {
		final String INCLUDE_DIRECTIVE = "#include";
		StringBuilder shaderSource = new StringBuilder();

		try {
			InputStream in = Shader.class.getResourceAsStream("/res/shaders/" + fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			Stream<String> lines = reader.lines();
			lines.forEachOrdered(line -> {
				if (line.startsWith(INCLUDE_DIRECTIVE))
					shaderSource.append(loadShader(line.substring(INCLUDE_DIRECTIVE.length() + 2, line.length() - 1)));
				else
					shaderSource.append(line).append("\n");
			});
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.exit(1);
		}

		return shaderSource.toString();
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

	private void addAllAttributes(String shaderText) {
		final String ATTRIBUTE_KEYWORD = "attribute";
		int attributeStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD);
		int attribNumber = 0;

		while (attributeStartLocation != -1) {
			if (!(attributeStartLocation != 0 &&
					(Character.isWhitespace(shaderText.charAt(attributeStartLocation - 1)) || shaderText.charAt(attributeStartLocation - 1) == ';') &&
					Character.isWhitespace(shaderText.charAt(attributeStartLocation + ATTRIBUTE_KEYWORD.length())))) {
				attributeStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD, attributeStartLocation + ATTRIBUTE_KEYWORD.length());
				continue;
			}

			int begin = attributeStartLocation + ATTRIBUTE_KEYWORD.length() + 1;
			int end = shaderText.indexOf(";", begin);

			String attributeLine = shaderText.substring(begin, end).trim();
			String attributeName = attributeLine.substring(attributeLine.indexOf(' ') + 1).trim();

			setAttribLocation(attributeName, attribNumber);
			attribNumber++;

			attributeStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD, attributeStartLocation + ATTRIBUTE_KEYWORD.length());
		}
	}

	private HashMap<String, ArrayList<GLSLStruct>> findUniformStructs(String shaderText) {
		HashMap<String, ArrayList<GLSLStruct>> result = new HashMap<>();

		final String STRUCT_KEYWORD = "struct";
		int structStartLocation = shaderText.indexOf(STRUCT_KEYWORD);
		while (structStartLocation != -1) {
			if (!(structStartLocation != 0 &&
					(Character.isWhitespace(shaderText.charAt(structStartLocation - 1)) || shaderText.charAt(structStartLocation - 1) == ';') &&
					Character.isWhitespace(shaderText.charAt(structStartLocation + STRUCT_KEYWORD.length())))) {
				structStartLocation = shaderText.indexOf(STRUCT_KEYWORD, structStartLocation + STRUCT_KEYWORD.length());
				continue;
			}

			int nameBegin = structStartLocation + STRUCT_KEYWORD.length() + 1;
			int braceBegin = shaderText.indexOf("{", nameBegin);
			int braceEnd = shaderText.indexOf("}", braceBegin);

			String structName = shaderText.substring(nameBegin, braceBegin).trim();
			ArrayList<GLSLStruct> glslStructs = new ArrayList<>();

			int componentSemicolonPos = shaderText.indexOf(";", braceBegin);
			while (componentSemicolonPos != -1 && componentSemicolonPos < braceEnd) {
				int componentNameEnd = componentSemicolonPos + 1;
				while (Character.isWhitespace(shaderText.charAt(componentNameEnd - 1)) || shaderText.charAt(componentNameEnd - 1) == ';')
					componentNameEnd--;

				int componentNameStart = componentSemicolonPos;
				while (!Character.isWhitespace(shaderText.charAt(componentNameStart - 1)))
					componentNameStart--;

				int componentTypeEnd = componentNameStart;

				while (Character.isWhitespace(shaderText.charAt(componentTypeEnd - 1)))
					componentTypeEnd--;

				int componentTypeStart = componentTypeEnd;

				while (!Character.isWhitespace(shaderText.charAt(componentTypeStart - 1)))
					componentTypeStart--;

				String componentName = shaderText.substring(componentNameStart, componentNameEnd);
				String componentType = shaderText.substring(componentTypeStart, componentTypeEnd);

				GLSLStruct glslStruct = new GLSLStruct();
				glslStruct.name = componentName;
				glslStruct.type = componentType;

				glslStructs.add(glslStruct);

				componentSemicolonPos = shaderText.indexOf(";", componentSemicolonPos + 1);
			}

			result.put(structName, glslStructs);

			structStartLocation = shaderText.indexOf(STRUCT_KEYWORD, structStartLocation + STRUCT_KEYWORD.length());
		}

		return result;
	}

	private void addAllUniforms(String shaderText) {
		HashMap<String, ArrayList<GLSLStruct>> structs = findUniformStructs(shaderText);

		final String UNIFORM_KEYWORD = "uniform";
		int uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD);
		while (uniformStartLocation != -1) {
			if (!(uniformStartLocation != 0
					&& (Character.isWhitespace(shaderText.charAt(uniformStartLocation - 1)) || shaderText.charAt(uniformStartLocation - 1) == ';')
					&& Character.isWhitespace(shaderText.charAt(uniformStartLocation + UNIFORM_KEYWORD.length())))) {
				uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD, uniformStartLocation + UNIFORM_KEYWORD.length());
				continue;
			}

			int begin = uniformStartLocation + UNIFORM_KEYWORD.length() + 1;
			int end = shaderText.indexOf(";", begin);

			String uniformLine = shaderText.substring(begin, end).trim();

			int whiteSpacePos = uniformLine.indexOf(' ');
			String uniformName = uniformLine.substring(whiteSpacePos + 1).trim();
			String uniformType = uniformLine.substring(0, whiteSpacePos).trim();

			resource.getUniformNames().add(uniformName);
			resource.getUniformTypes().add(uniformType);
			addUniform(uniformName, uniformType, structs);

			uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD, uniformStartLocation + UNIFORM_KEYWORD.length());
		}
	}

	private void addUniform(String uniformName, String uniformType, HashMap<String, ArrayList<GLSLStruct>> structs) {
		boolean addThis = true;
		ArrayList<GLSLStruct> structComponents = structs.get(uniformType);

		if (structComponents != null) {
			addThis = false;
			for (GLSLStruct struct : structComponents) {
				addUniform(uniformName + "." + struct.name, struct.type, structs);
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
	}
}
