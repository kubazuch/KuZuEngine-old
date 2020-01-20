package com.kuzu.engine.components;

import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.rendering.Material;
import com.kuzu.engine.rendering.Mesh;
import com.kuzu.engine.rendering.RenderingEngine;
import com.kuzu.engine.rendering.shader.Shader;

public class MeshRenderer extends GameComponent {
	private Mesh mesh;
	private Material material;

	public MeshRenderer(Mesh mesh, Material material) {
		this.mesh = mesh;
		this.material = material;
	}

	@Override
	public void render(Shader shader, RenderingEngine engine, Camera camera) {
		shader.bind();
		shader.updateUniforms(getTransform(), material, engine, camera);
		mesh.draw();
	}
}
