package com.kuzu.engine.rendering;

import com.kuzu.engine.core.MainComponent;
import com.kuzu.engine.rendering.meshLoading.IndexedModel;
import com.kuzu.engine.rendering.meshLoading.OBJModel;
import com.kuzu.engine.rendering.resources.MeshResource;
import org.joml.Vector3f;
import org.lwjglx.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

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

	public void drawNormals() {
		resource.drawNormals();
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

	private void saveImage() {
		glReadBuffer(GL_FRONT);
		int width = MainComponent.INSTANCE.getWidth();
		int height = MainComponent.INSTANCE.getHeight();
		int bpp = 4;
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		File file = new File("render/out_" + (count++) + ".png");
		String format = "png";
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int i = (x + (width * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;

				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		}

		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
