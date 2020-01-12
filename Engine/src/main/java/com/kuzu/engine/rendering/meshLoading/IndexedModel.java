package com.kuzu.engine.rendering.meshLoading;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

public class IndexedModel {
	private ArrayList<Vector3f> positions;
	private ArrayList<Vector2f> texCoords;
	private ArrayList<Vector3f> normals;
	private ArrayList<Vector3f> binormals;
	private ArrayList<Vector3f> tangents;
	private ArrayList<Integer> indices;

	public IndexedModel() {
		positions = new ArrayList<>();
		texCoords = new ArrayList<>();
		normals = new ArrayList<>();
		binormals = new ArrayList<>();
		tangents = new ArrayList<>();
		indices = new ArrayList<>();
	}

	public void calcNormals() {
		for (int i = 0; i < indices.size(); i += 3) {
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);

			Vector3f v1 = positions.get(i1).sub(positions.get(i0), new Vector3f());
			Vector3f v2 = positions.get(i2).sub(positions.get(i0), new Vector3f());

			Vector3f normal = v1.cross(v2).normalize();

			normals.get(i0).add(normal);
			normals.get(i1).add(normal);
			normals.get(i2).add(normal);
		}

		for (Vector3f normal : normals) normal.normalize();
	}

	public void calcTangentsAndBinromals() {
		for (int i = 0; i < indices.size(); i += 3) {
			int i0 = indices.get(i);
			int i1 = indices.get(i + 1);
			int i2 = indices.get(i + 2);

			Vector3f edge1 = positions.get(i1).sub(positions.get(i0), new Vector3f());
			Vector3f edge2 = positions.get(i2).sub(positions.get(i0), new Vector3f());

			float deltaU1 = texCoords.get(i1).x - texCoords.get(i0).x;
			float deltaV1 = texCoords.get(i1).y - texCoords.get(i0).y;
			float deltaU2 = texCoords.get(i2).x - texCoords.get(i0).x;
			float deltaV2 = texCoords.get(i2).y - texCoords.get(i0).y;

			float dividend = (deltaU1 * deltaV2 - deltaU2 * deltaV1);
			float f = dividend == 0 ? 0.0f : 1.0f / dividend;

			Vector3f tangent = new Vector3f();
			tangent.x = f * (deltaV2 * edge1.x - deltaV1 * edge2.x);
			tangent.y = f * (deltaV2 * edge1.y - deltaV1 * edge2.y);
			tangent.z = f * (deltaV2 * edge1.z - deltaV1 * edge2.z);

			tangents.get(i0).add(tangent);
			tangents.get(i1).add(tangent);
			tangents.get(i2).add(tangent);

			Vector3f binormal = new Vector3f();
			binormal.x = f * (deltaU2 * edge2.x - deltaU1 * edge1.x);
			binormal.y = f * (deltaU2 * edge2.y - deltaU1 * edge1.y);
			binormal.z = f * (deltaU2 * edge2.z - deltaU1 * edge1.z);

			binormals.get(i0).add(binormal);
			binormals.get(i1).add(binormal);
			binormals.get(i2).add(binormal);
		}

		for (Vector3f tangent : tangents) tangent.normalize();
		for (Vector3f binormal : binormals) binormal.normalize();
	}

	public ArrayList<Vector3f> getPositions() {
		return positions;
	}

	public ArrayList<Vector2f> getTexCoords() {
		return texCoords;
	}

	public ArrayList<Vector3f> getNormals() {
		return normals;
	}

	public ArrayList<Vector3f> getBinormals() {
		return binormals;
	}

	public ArrayList<Vector3f> getTangents() {
		return tangents;
	}

	public ArrayList<Integer> getIndices() {
		return indices;
	}
}
