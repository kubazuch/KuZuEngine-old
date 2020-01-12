package com.kuzu.game;

import com.kuzu.engine.core.Input;
import com.kuzu.engine.core.MainComponent;
import com.kuzu.engine.core.MouseCode;
import com.kuzu.engine.core.Transform;
import com.kuzu.engine.event.MouseEvents;
import com.kuzu.engine.event.WindowResizeEvent;
import com.kuzu.engine.rendering.*;
import com.kuzu.engine.rendering.buffer.BufferLayout;
import com.kuzu.engine.rendering.camera.PerspectiveCamera;
import com.kuzu.engine.rendering.shader.BasicShader;
import com.kuzu.event.api.EventSubscriber;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GameLayer extends Layer {
	private static final float FOV = (float) Math.toRadians(70f);
	private static final float zNear = 0.1f;
	private static final float zFar = 1000f;

	private Mesh mesh;
	private BasicShader basicShader;
	//	private PhongShader phongShader;
	private PerspectiveCamera camera;
	private Transform transform;
	private Material material;

	private float temp = 0f;

	public GameLayer() {
		BufferLayout layout = Vertex.LAYOUT;
		System.out.println(layout);

		mesh = new Mesh("cube2.obj");
		material = new Material(new Texture("uv.png"));
		basicShader = new BasicShader();
//		phongShader = PhongShader.getInstance();
		transform = new Transform();
		transform.setTranslation(0, 0, -5);

		float width = MainComponent.INSTANCE.getWidth();
		float height = MainComponent.INSTANCE.getHeight();
		float aspectRatio = width / height;
		Vector3f camPos = new Vector3f(0, 3, 0);
		Quaternionf rotation = new Quaternionf().lookAlong(transform.getTranslation().sub(camPos, new Vector3f()), new Vector3f(0, 1, 0));
		camera = new PerspectiveCamera(FOV, aspectRatio, zNear, zFar, camPos, rotation);
		camera.setWindowCenter(new Vector2f(width / 2, height / 2));

		basicShader.bind();
		basicShader.setProjectionMatrix(camera.getProjection());
		basicShader.unbind();

//		phongShader.bind();
//		phongShader.setUniformMat4("projectionMat", camera.getProjection());
//		phongShader.unbind();
	}

	@Override
	public void onAttach() {
		System.out.println(RenderUtil.getOpenGLVersion());
	}

	@Override
	public void onUpdate(float delta) {
		input(delta);
		update(delta);
	}

	public void input(float delta) {
		camera.input(delta);
	}

	public void update(float delta) {
		temp += delta;
//		transform.setRotation(0, temp %= 2*(float)Math.PI, 0);
//		transform.getRotation().rotateX((float) Math.PI/2);
	}

	@Override
	public void render() {
		basicShader.bind();
		basicShader.updateUniforms(transform.getTransformation(), camera.getView());

		material.getTexture("diffuse").bind();
		mesh.draw();
		basicShader.unbind();

//		phongShader.bind();
//		phongShader.setUniformMat4("transformMat", transform.getTransformation());
//		phongShader.setUniformMat4("viewMat", camera.getView());
//		phongShader.setUniformMat4("normalMat", camera.getView()
//				.mul(transform.getTransformation(), new Matrix4f())
//				.invert()
//				.transpose()
//		);
//		material.getTexture().bind();
//		mesh.drawNormals();
//		phongShader.unbind();
	}

	@EventSubscriber
	public void onMouseDown(MouseEvents.MouseButtonPressedEvent e) {
		System.out.println("Button: " + e.getMouseButton());
		System.out.println(Input.getMousePosition());
		if (e.getMouseButton() == MouseCode.BUTTON_LEFT) {
			camera.onLMBDown();
		}
	}

	@EventSubscriber
	public void onWindowResizeEvent(WindowResizeEvent event) {
		float width = event.getWidth();
		float height = event.getHeight();
		float aspectRatio = width / height;
		camera.setProjection(FOV, aspectRatio, zNear, zFar);
		camera.setWindowCenter(new Vector2f(width / 2, height / 2));

		basicShader.bind();
		basicShader.setProjectionMatrix(camera.getProjection());
		basicShader.unbind();

//		phongShader.bind();
//		phongShader.setUniformMat4("projectionMat", camera.getProjection());
//		phongShader.unbind();
	}

}
