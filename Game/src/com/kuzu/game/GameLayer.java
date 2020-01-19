package com.kuzu.game;

import com.kuzu.engine.components.MeshRenderer;
import com.kuzu.engine.components.camera.PerspectiveCamera;
import com.kuzu.engine.core.GameObject;
import com.kuzu.engine.core.Input;
import com.kuzu.engine.core.MouseCode;
import com.kuzu.engine.core.Transform;
import com.kuzu.engine.event.MouseEvents;
import com.kuzu.engine.event.WindowResizeEvent;
import com.kuzu.engine.rendering.*;
import com.kuzu.engine.rendering.buffer.BufferLayout;
import com.kuzu.engine.rendering.shader.BasicShader;
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
	public static final Vector3f COLOR3 = new Vector3f(1f, 1f, 0f);

	//	private PhongShader phongShader;
	private BasicShader basicShader;
	private PerspectiveCamera camera;
	private GameObject root;

//	private DirectionalLight directionalLight;
//	private PointLight light1;
//	private PointLight light2;
//	private SpotLight spotLight;

	private float temp = 0f;

	public GameLayer() {
		BufferLayout layout = Vertex.LAYOUT;
//		phongShader = new PhongShader();
		basicShader = new BasicShader();
		Transform transform = new Transform();
		transform.setTranslation(0, 0, 0);

		float width = Main.WIDTH;
		float height = Main.HEIGHT;
		float aspectRatio = width / height;
		Vector3f camPos = new Vector3f(0, 0, 5);
		Quaternionf rotation = new Quaternionf().lookAlong(transform.getTranslation().sub(camPos, new Vector3f()), new Vector3f(0, 1, 0));
		camera = new PerspectiveCamera(FOV, aspectRatio, zNear, zFar, camPos, rotation);
		camera.setWindowCenter(new Vector2f(width / 2, height / 2));

		Mesh mesh = new Mesh("susan.obj");
		Material material = new Material(new Texture("uv.png"));
		MeshRenderer renderer = new MeshRenderer(mesh, material);
		root = new GameObject();
		root.addComponent(renderer);

		basicShader.bind();
		basicShader.setProjectionMatrix(camera.getProjection());
//		phongShader.setAmbientLight(new Vector3f(0.1f, 0.1f, 0.1f));
//		directionalLight = new DirectionalLight(
//				new BaseLight(new Vector3f(1, 1, 1), 0.1f),
//				new Vector3f(0.71f, 0.71f, 1)
//		);
//		light1 = new PointLight(
//				new BaseLight(COLOR1, 0.7f),
//				new Attenuation(0, 0, 1),
//				new Vector3f(0, 0, -5),
//				6
//		);
//		light2 = new PointLight(
//				new BaseLight(COLOR2, 0.7f),
//				new Attenuation(0, 0, 1),
//				new Vector3f(0, 0, -5),
//				6
//		);
//		spotLight = new SpotLight(
//				new PointLight(
//						new BaseLight(COLOR3, 0.7f),
//						new Attenuation(0, 0, 0.1f),
//						new Vector3f(0, 5, 0),
//						30
//				),
//				new Vector3f(0,-1,0),
//				0.7f
//		);
//		phongShader.setDirectionalLight("directionalLight", directionalLight);
//		phongShader.setPointLights(light1, light2);
		basicShader.unbind();
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
		basicShader.dispose();
	}

	public void input(float delta) {
		camera.input(delta);
		root.input(delta);
	}

	public void update(float delta) {
		temp += delta;
		root.update(delta);
//		temp %= 2 * (float) Math.PI;
//		float a = (float) Math.sin(temp);
//		float b = (float) Math.cos(temp);
//		light1.setPosition(new Vector3f(-2*a, 0, -2*b));
//		light2.setPosition(new Vector3f(2*a, 0, 2*b));
//		spotLight.getPointLight().setPosition(camera.getPosition());
//		spotLight.setDirection(camera.getForward());
		root.getTransform().setRotation(0, temp %= 2 * (float) Math.PI, 0);
	}

	@Override
	public void render() {
		root.render(basicShader, camera);
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
	}

}
