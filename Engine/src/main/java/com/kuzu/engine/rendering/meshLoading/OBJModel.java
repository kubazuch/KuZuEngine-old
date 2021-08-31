package com.kuzu.engine.rendering.meshLoading;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class OBJModel {

	private ArrayList<Vector3f> positions;
	private ArrayList<Vector2f> texCoords;
	private ArrayList<Vector3f> normals;
	private ArrayList<OBJIndex> indices;
	private boolean hasTexCoords;
	private boolean hasNormals;

	public OBJModel(String fileName) {
		positions = new ArrayList<>();
		texCoords = new ArrayList<>();
		normals = new ArrayList<>();
		indices = new ArrayList<>();
		hasTexCoords = false;
		hasNormals = false;

		InputStream in = getClass().getResourceAsStream(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		reader.lines()
				.map(line -> line.split(" "))
				.map(split -> Arrays.stream(split)
						.filter(token -> !token.equals("")))
				.map(tokens -> tokens.toArray(String[]::new))
				.filter(tokenArray -> !(tokenArray.length == 0 || tokenArray[0].equals("#")))
				.forEachOrdered(tokenArray -> {
					switch (tokenArray[0]) {
						case "v":
							positions.add(new Vector3f(Float.parseFloat(tokenArray[1]),
									Float.parseFloat(tokenArray[2]),
									Float.parseFloat(tokenArray[3])));
							break;
						case "vt":
							texCoords.add(new Vector2f(Float.parseFloat(tokenArray[1]),
									Float.parseFloat(tokenArray[2])));
							break;
						case "vn":
							normals.add(new Vector3f(Float.parseFloat(tokenArray[1]),
									Float.parseFloat(tokenArray[2]),
									Float.parseFloat(tokenArray[3])));
							break;
						case "f":
							for (int i = 0; i < tokenArray.length - 3; i++) {
								indices.add(parseOBJIndex(tokenArray[1]));
								indices.add(parseOBJIndex(tokenArray[2 + i]));
								indices.add(parseOBJIndex(tokenArray[3 + i]));
							}
							break;
					}
				});

	}

	public IndexedModel toIndexedModel() {
		IndexedModel result = new IndexedModel();
		IndexedModel normalModel = new IndexedModel();
		HashMap<OBJIndex, Integer> resultIndexMap = new HashMap<>();
		HashMap<Integer, Integer> normalIndexMap = new HashMap<>();
		HashMap<Integer, Integer> indexMap = new HashMap<>();

		{
			int i = 0;
			while (i < indices.size()) {
				OBJIndex currentIndex = indices.get(i);

				Vector3f currentPosition = positions.get(currentIndex.getVertexIndex());
				Vector2f currentTexCoord;
				Vector3f currentNormal;

				if (hasTexCoords)
					currentTexCoord = texCoords.get(currentIndex.getTexCoordIndex());
				else
					currentTexCoord = new Vector2f();

				if (hasNormals)
					currentNormal = normals.get(currentIndex.getNormalIndex());
				else
					currentNormal = new Vector3f();

				Integer modelVertexIndex = resultIndexMap.get(currentIndex);
				if (modelVertexIndex == null) {
					modelVertexIndex = result.getPositions().size();
					resultIndexMap.put(currentIndex, modelVertexIndex);

					result.getPositions().add(currentPosition);
					result.getTexCoords().add(currentTexCoord);
					if (hasNormals)
						result.getNormals().add(currentNormal);
				}

				Integer normalModelIndex = normalIndexMap.get(currentIndex.getVertexIndex());
				if (normalModelIndex == null) {
					normalModelIndex = normalModel.getPositions().size();
					normalIndexMap.put(currentIndex.getVertexIndex(), normalModelIndex);

					normalModel.getPositions().add(currentPosition);
					normalModel.getTexCoords().add(currentTexCoord);
					normalModel.getNormals().add(currentNormal);
					normalModel.getTangents().add(new Vector3f());
					normalModel.getBinormals().add(new Vector3f());
				}

				result.getIndices().add(modelVertexIndex);
				normalModel.getIndices().add(normalModelIndex);
				indexMap.put(modelVertexIndex, normalModelIndex);
				i++;
			}
		}

		if (!hasNormals) {
			normalModel.calcNormals();
			for (int i = 0; i < result.getPositions().size(); i++) {
				result.getNormals().add(normalModel.getNormals().get(indexMap.get(i)));
			}
		}

		normalModel.calcTangentsAndBinromals();
		for (int i = 0; i < result.getPositions().size(); i++) {
			result.getTangents().add(normalModel.getTangents().get(indexMap.get(i)));
			result.getBinormals().add(normalModel.getBinormals().get(indexMap.get(i)));
		}

		for (int i = 0; i < result.getTexCoords().size(); i++) {
			result.getTexCoords().get(i).y = 1 - result.getTexCoords().get(i).y;
		}

		return result;
	}

	private OBJIndex parseOBJIndex(String token) {
		String[] values = token.split("/");
		OBJIndex result = new OBJIndex();
		result.setVertexIndex(Integer.parseInt(values[0]) - 1);

		if (values.length > 1) {
			if (!values[1].isEmpty()) {
				hasTexCoords = true;
				result.setTexCoordIndex(Integer.parseInt(values[1]) - 1);
			}

			if (values.length > 2) {
				hasNormals = true;
				result.setNormalIndex(Integer.parseInt(values[2]) - 1);
			}
		}

		return result;
	}
}
