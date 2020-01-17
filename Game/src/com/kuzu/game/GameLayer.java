package com.kuzu.game;

import com.kuzu.engine.components.camera.PerspectiveCamera;
import com.kuzu.engine.components.light.BaseLight;
import com.kuzu.engine.components.light.DirectionalLight;
import com.kuzu.engine.core.Input;
import com.kuzu.engine.core.MainComponent;
import com.kuzu.engine.core.MouseCode;
import com.kuzu.engine.core.Transform;
import com.kuzu.engine.event.MouseEvents;
import com.kuzu.engine.event.WindowResizeEvent;
import com.kuzu.engine.rendering.*;
import com.kuzu.engine.rendering.buffer.BufferLayout;
import com.kuzu.engine.rendering.shader.PhongShader;
import com.kuzu.event.api.EventSubscriber;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GameLayer extends Layer {
	private static final float FOV = (float) Math.toRadians(70f);
	private static final float zNear = 0.1f;
	private static final float zFar = 1000f;
	public static final Vector3f COLOR1 = new Vector3f(1f, 0, 1f);
	public static final Vector3f COLOR2 = new Vector3f(0f, 1f, 1f);

	private Mesh mesh;
	private PhongShader phongShader;
	private PerspectiveCamera camera;
	private Transform transform;
	private Material material;

	private DirectionalLight light1;
	private DirectionalLight light2;

	private float temp = 0f;

	public GameLayer() {
		BufferLayout layout = Vertex.LAYOUT;
		System.out.println(layout);

		if (new Texture("color.png").equals("")) System.out.println("k");

		mesh = new Mesh("susan.obj");
		material = new Material(new Texture("uv.png"));
		phongShader = new PhongShader();
		transform = new Transform();
		transform.setTranslation(0, 0, -5);

		float width = MainComponent.INSTANCE.getWidth();
		float height = MainComponent.INSTANCE.getHeight();
		float aspectRatio = width / height;
		Vector3f camPos = new Vector3f(0, 3, 0);
		Quaternionf rotation = new Quaternionf().lookAlong(transform.getTranslation().sub(camPos, new Vector3f()), new Vector3f(0, 1, 0));
		camera = new PerspectiveCamera(FOV, aspectRatio, zNear, zFar, camPos, rotation);
		camera.setWindowCenter(new Vector2f(width / 2, height / 2));

		phongShader.bind();
		phongShader.setProjectionMatrix(camera.getProjection());
		phongShader.setAmbientLight(new Vector3f(0.1f, 0.1f, 0.1f));
		light1 = new DirectionalLight(new BaseLight(COLOR1, 0.7f), new Vector3f(0.71f, 0.71f, 1));
		light2 = new DirectionalLight(new BaseLight(COLOR2, 0.7f), new Vector3f(-0.71f, 0.71f, 1));
		phongShader.setDirectionalLight("directionalLight1", light1);
		phongShader.setDirectionalLight("directionalLight2", light2);
		phongShader.unbind();
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

	@Override
	public void dispose() {
		mesh.dispose();
		phongShader.dispose();
		material.dispose();
	}

	public void input(float delta) {
		camera.input(delta);
	}

	public void update(float delta) {
		temp += delta;
		temp %= 2 * (float) Math.PI;
		float a = (float) Math.sin(temp);
		float b = (float) Math.cos(temp);
		light1.setDirection(new Vector3f(-b, 0, -a));
		light1.setDirection(new Vector3f(b, 0, a));
//		transform.setRotation(0, temp %= 2 * (float) Math.PI, 0);
	}

	@Override
	public void render() {
		phongShader.bind();
		phongShader.updateUniforms(transform.getTransformation(), camera.getView());
		phongShader.setDirectionalLight("directionalLight1", light1);
		phongShader.setDirectionalLight("directionalLight2", light2);
		material.getTexture("diffuse").bind();
		mesh.draw();
		phongShader.unbind();
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

		phongShader.bind();
		phongShader.setProjectionMatrix(camera.getProjection());
		phongShader.unbind();
	}

}
