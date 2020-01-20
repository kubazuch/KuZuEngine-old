package com.kuzu.game;

import com.kuzu.engine.components.MeshRenderer;
import com.kuzu.engine.components.camera.CameraComponent;
import com.kuzu.engine.components.camera.PerspectiveCamera;
import com.kuzu.engine.core.CoreEngine;
import com.kuzu.engine.core.GameObject;
import com.kuzu.engine.core.Input;
import com.kuzu.engine.event.MouseEvents;
import com.kuzu.engine.math.MathUtils;
import com.kuzu.engine.rendering.*;
import com.kuzu.engine.rendering.shader.BasicShader;
import com.kuzu.event.EventBus;
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
	private EventBus gameEventBus;
	private BasicShader basicShader;

	private GameObject root;
	private GameObject monkey;
	private GameObject plane;
	private GameObject camera;

//	private DirectionalLight directionalLight;
//	private PointLight light1;
//	private PointLight light2;
//	private SpotLight spotLight;

	private float temp = 0f;

	public GameLayer() {
		gameEventBus = new EventBus();

//		phongShader = new PhongShader();
		basicShader = new BasicShader();

		root = new GameObject();

		MeshRenderer susanRenderer = new MeshRenderer(new Mesh("susan.obj"), new Material(new Texture("uv.png")));
		monkey = new GameObject(new Vector3f(0, 2, 0)).addComponent(susanRenderer);

		MeshRenderer planeRenderer = new MeshRenderer(new Mesh("plane.obj"), new Material(new Texture("color.png")));
		plane = new GameObject().addComponent(planeRenderer);
		plane.getTransform().setScale(new Vector3f(10f));

		float width = Main.WIDTH;
		float height = Main.HEIGHT;
		float aspectRatio = width / height;
		PerspectiveCamera perspectiveCamera = new PerspectiveCamera(FOV, aspectRatio, zNear, zFar);
		CameraComponent cameraComponent = new CameraComponent(perspectiveCamera);
		cameraComponent.setWindowCenter(new Vector2f(width / 2, height / 2));
		camera = new GameObject().addComponent(cameraComponent);
		camera.getTransform().setPos(new Vector3f(0, 10, 5));
		camera.getTransform().lookAt(monkey.getTransform().getPos(), MathUtils.UP);
		root.addChild(monkey).addChild(plane).addChild(camera);

		basicShader.bind();
		basicShader.setProjectionMatrix(perspectiveCamera.getProjection());
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
	public void onAttach(CoreEngine engine) {
		root.setEngine(engine);
		root.registerToEventBus(engine.getWindowEventBus());
		root.registerToEventBus(gameEventBus);
		System.out.println(RenderingEngine.getOpenGLVersion());
	}

	@Override
	public void onUpdate(float delta, Input input) {
		input(input, delta);
		update(delta);
	}

	@Override
	public void dispose() {
		basicShader.dispose();
	}

	public void input(Input input, float delta) {
		root.inputAll(delta, input);
	}

	public void update(float delta) {
		temp += delta;
		root.updateAll(delta);
//		temp %= 2 * (float) Math.PI;
//		float a = (float) Math.sin(temp);
//		float b = (float) Math.cos(temp);
//		light1.setPosition(new Vector3f(-2*a, 0, -2*b));
//		light2.setPosition(new Vector3f(2*a, 0, 2*b));
//		spotLight.getPointLight().setPosition(camera.getPosition());
//		spotLight.setDirection(camera.getForward());
		monkey.getTransform().setRot(new Quaternionf().rotateXYZ(0, temp %= 2 * (float) Math.PI, 0));
	}

	@Override
	public void render(RenderingEngine engine) {
		engine.render(root);
	}

	@EventSubscriber
	public void onMouseDown(MouseEvents.MouseButtonPressedEvent e) {
		System.out.println("Button: " + e.getMouseButton());
		System.out.println(e.getInput().getMousePosition());
	}
}
