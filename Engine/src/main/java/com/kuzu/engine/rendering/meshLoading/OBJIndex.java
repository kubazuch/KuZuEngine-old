package com.kuzu.engine.rendering.meshLoading;

public class OBJIndex {
	private int vertexIndex;
	private int texCoordIndex;
	private int normalIndex;

	public int getVertexIndex() {
		return vertexIndex;
	}

	public void setVertexIndex(int vertexIndex) {
		this.vertexIndex = vertexIndex;
	}

	public int getTexCoordIndex() {
		return texCoordIndex;
	}

	public void setTexCoordIndex(int texCoordIndex) {
		this.texCoordIndex = texCoordIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OBJIndex objIndex = (OBJIndex) o;
		return vertexIndex == objIndex.vertexIndex &&
				texCoordIndex == objIndex.texCoordIndex &&
				normalIndex == objIndex.normalIndex;
	}

	@Override
	public int hashCode() {
		final int BASE = 17;
		final int MULTIPLIER = 31;

		int result = BASE;

		result = MULTIPLIER * result + vertexIndex;
		result = MULTIPLIER * result + texCoordIndex;
		result = MULTIPLIER * result + normalIndex;

		return result;
	}
}
