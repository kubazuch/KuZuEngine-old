package com.kuzu.engine.rendering;

import com.kuzu.engine.rendering.meshLoading.IndexedModel;
import com.kuzu.engine.rendering.meshLoading.OBJModel;
import com.kuzu.engine.rendering.resources.MeshResource;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;

public class Mesh {
	private static HashMap<String, MeshResource> loadedModels = new HashMap<>();
	private MeshResource resource;
	private String fileName;
	private int count = 0;

	public Mesh(String fileName) {
		this.fileName = fileName;
		MeshResource oldResource = loadedModels.get(fileName);
		if (oldResource != null) {
			this.resource = oldResource;
			this.resource.addReference();
		} else {
			loadMesh(fileName);
			loadedModels.put(fileName, this.resource);
		}
	}

	public Mesh(Vertex[] vertices, int[] indices) {
		this(vertices, indices, false);
	}

	public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals) {
		this.fileName = "";
		addVertices(vertices, indices, calcNormals);
	}

	@Override
	protected void finalize() throws Throwable {
		if (resource.removeReference() && !fileName.isEmpty()) {
			loadedModels.remove(fileName);
		}
	}

	public void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals) {
		if (calcNormals)
			calcNormals(vertices, indices);

		resource = new MeshResource(vertices, indices);
	}

	public void draw() {
		resource.draw();
	}

	private void calcNormals(Vertex[] vertices, int[] indices) {
		for (int i = 0; i < indices.length; i += 3) {
			int i0 = indices[i];
			int i1 = indices[i + 1];
			int i2 = indices[i + 2];

			Vector3f v1 = vertices[i1].getPos().sub(vertices[i0].getPos(), new Vector3f());
			Vector3f v2 = vertices[i2].getPos().sub(vertices[i0].getPos(), new Vector3f());

			Vector3f normal = v1.cross(v2).normalize();

			vertices[i0].getNormal().add(normal);
			vertices[i1].getNormal().add(normal);
			vertices[i2].getNormal().add(normal);
		}

		for (Vertex vertex : vertices) vertex.getNormal().normalize();
	}

	private Mesh loadMesh(String fileName) {
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length - 1];

		if (!ext.equals("obj")) throw new RuntimeException("Error: File format not supported for mesh data: " + ext);

		OBJModel m = new OBJModel("/res/models/" + fileName);
		IndexedModel model = m.toIndexedModel();

		ArrayList<Vertex> vertices = new ArrayList<>();
		for (int i = 0; i < model.getPositions().size(); i++) {
			vertices.add(new Vertex(model.getPositions().get(i),
					model.getTexCoords().get(i),
					model.getNormals().get(i),
					model.getBinormals().get(i),
					model.getTangents().get(i)));
		}

		Vertex[] vertexData = vertices.toArray(new Vertex[0]);
		int[] indexData = model.getIndices().stream().mapToInt(i -> i).toArray();

		addVertices(vertexData, indexData, false);

		return this;
	}

	public void dispose() {
		if (resource.removeReference()) {
			resource.dispose();
			if (!fileName.isEmpty())
				loadedModels.remove(fileName);
		}
	}
}
