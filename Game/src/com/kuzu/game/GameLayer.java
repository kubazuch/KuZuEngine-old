package com.kuzu.game;

import com.kuzu.engine.components.MeshRenderer;
import com.kuzu.engine.components.camera.CameraComponent;
import com.kuzu.engine.components.camera.PerspectiveCamera;
import com.kuzu.engine.components.light.Attenuation;
import com.kuzu.engine.components.light.DirectionalLight;
import com.kuzu.engine.components.light.PointLight;
import com.kuzu.engine.components.light.SpotLight;
import com.kuzu.engine.core.CoreEngine;
import com.kuzu.engine.core.GameObject;
import com.kuzu.engine.core.Input;
import com.kuzu.engine.event.MouseEvents;
import com.kuzu.engine.math.MathUtils;
import com.kuzu.engine.rendering.*;
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

	private EventBus gameEventBus;

	private GameObject root;
	private GameObject monkey;
	private GameObject plane;
	private GameObject camera;
	private GameObject sun;

	private GameObject[] pointLights;

	private float temp = 0f;

	public GameLayer() {
		gameEventBus = new EventBus();

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
		camera = new GameObject(new Vector3f(0, 10, 5)).addComponent(cameraComponent);
		camera.getTransform().lookAt(monkey.getTransform().getPos(), MathUtils.UP);

		DirectionalLight directionalLight = new DirectionalLight(COLOR1, 0.6f);
		sun = new GameObject(new Vector3f(5, 10, 0)).addComponent(directionalLight);
		sun.getTransform().getRot().lookAlong(new Vector3f(1, 1, 1), MathUtils.UP);

		SpotLight spot = new SpotLight(COLOR2, 0.6f, new Attenuation(0, 0, 0.1f), 0.7f);
//		spotLight = new GameObject(new Vector3f(-5,1,0)).addComponent(spot);
		camera.addComponent(spot);

		int lightFieldWidth = 5;
		int lightFieldHeight = 5;

		float lightFieldStartX = 0;
		float lightFieldStartY = 0;
		float lightFieldStepX = 2;
		float lightFieldStepY = 2;

		pointLights = new GameObject[lightFieldWidth * lightFieldHeight];
		for (int i = 0; i < lightFieldWidth; i++) {
			for (int j = 0; j < lightFieldHeight; j++) {
				System.out.println(new Vector3f(lightFieldStartX + lightFieldStepX * i, 1, lightFieldStartY + lightFieldStepY * j));
				pointLights[i * lightFieldWidth + j] = new GameObject(new Vector3f(lightFieldStartX + lightFieldStepX * i, 1, lightFieldStartY + lightFieldStepY * j))
						.addComponent(new PointLight(COLOR3, 0.4f, new Attenuation(0, 0, 1)));
			}
		}

		root.addChildren(monkey, plane, sun, camera);
		root.addChildren(pointLights);
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
	}

	public void input(Input input, float delta) {
		root.inputAll(delta, input);
	}

	public void update(float delta) {
		temp += delta;
		root.updateAll(delta);
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
