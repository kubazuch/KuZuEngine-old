package com.kuzu.game;

import com.kuzu.engine.components.MeshRenderer;
import com.kuzu.engine.components.camera.CameraComponent;
import com.kuzu.engine.components.camera.FreeLook;
import com.kuzu.engine.components.camera.FreeMove;
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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
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
	private GameObject cube;
	private GameObject plane;
	private GameObject player;
	private GameObject camera;
	private GameObject sun;

	private GameObject[] pointLights;

	private SpotLight spot;

	private float temp = 0f;

	public GameLayer() {
		gameEventBus = EventBus.builder().logNoSubscriberMessages(false).build();

		root = new GameObject();

		MeshRenderer susanRenderer = new MeshRenderer(new Mesh("susan.obj"), new Material(new Texture("uv.png")));
		MeshRenderer cubeRenderer = new MeshRenderer(new Mesh("cube2.obj"), new Material(new Texture("uv.png")));
		MeshRenderer planeRenderer = new MeshRenderer(new Mesh("plane.obj"), new Material(new Texture("color.png")));

		monkey = new GameObject(new Vector3f(0, 1.5f, 0)).addComponent(susanRenderer);
		cube = new GameObject(new Vector3f(0, 0, 0)).addComponent(cubeRenderer);
		plane = new GameObject().addComponent(planeRenderer);
		plane.getTransform().setScale(new Vector3f(10f));

		float width = Main.WIDTH;
		float height = Main.HEIGHT;
		float aspectRatio = width / height;
		PerspectiveCamera perspectiveCamera = new PerspectiveCamera(FOV, aspectRatio, zNear, zFar);
		CameraComponent cameraComponent = new CameraComponent(perspectiveCamera);

		FreeMove freeMove = new FreeMove(2);
		FreeLook freeLookBody = new FreeLook(1, true);
		FreeLook freeLookHead = new FreeLook(1, false);
		freeLookBody.setWindowCenter(new Vector2f(width / 2, height / 2));
		freeLookHead.setWindowCenter(new Vector2f(width / 2, height / 2));

		camera = new GameObject();
		camera.addComponent(cameraComponent);
		camera.getTransform().setPos(new Vector3f(0, 0, -7));
		camera.getRot().rotateY((float) Math.PI);

		DirectionalLight directionalLight = new DirectionalLight(COLOR1, 0.6f);
		sun = new GameObject(new Vector3f(0f, 0, 0)).addComponent(directionalLight);
		sun.getTransform().lookAt(cube.getTransform().getTransformedPos(), MathUtils.UP);
		System.out.println(sun.getTransform().getTransformedRot());

		spot = new SpotLight(COLOR2, 0.6f, new Attenuation(0, 0, 0.1f), 0.7f);
		GameObject spotLight = new GameObject();
		spotLight.getTransform().setPos(new Vector3f(0, 3, 0));
		spotLight.getRot().rotateX((float) Math.PI / 2);
		spotLight.addComponent(spot);

		int lightFieldWidth = 5;
		int lightFieldHeight = 5;

		float lightFieldStepX = 2;
		float lightFieldStepY = 2;
		float lightFieldStartX = -2 * lightFieldStepX;
		float lightFieldStartY = -2 * lightFieldStepY;

		pointLights = new GameObject[lightFieldWidth * lightFieldHeight];
		for (int i = 0; i < lightFieldWidth; i++) {
			for (int j = 0; j < lightFieldHeight; j++) {
				pointLights[i * lightFieldWidth + j] = new GameObject(new Vector3f(lightFieldStartX + lightFieldStepX * i, -3, lightFieldStartY + lightFieldStepY * j))
						.addComponent(new PointLight(COLOR3, 0.4f, new Attenuation(0, 0, 1)));
			}
		}

		monkey.addChild(camera);
		monkey.addComponent(freeLookHead);
		player = new GameObject(new Vector3f(0, 0, 0));
		player.addComponents(freeMove, freeLookBody, cubeRenderer);
		player.addChild(monkey);
		player.addChild(spotLight);

		root.addChildren(plane, sun, player);
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
		temp %= 2 * (float) Math.PI;
		root.updateAll(delta);
//		camera.getTransform().setPos(new Vector3f(5*(float)Math.cos(temp), 5, 5*(float)Math.sin(temp)));
//		camera.getTransform().lookAt(monkey.getTransform().getPos(), MathUtils.UP);
//		monkey.getTransform().setRot(new Quaternionf().rotateXYZ(0, temp, 0));
//		monkey.getTransform().getRot().rotateY(delta);
//		float radius = 2 - 2*(float)Math.sin(temp) + (float)Math.sin(temp)*(float)Math.sqrt(Math.abs(Math.cos(temp)))/(float)(Math.sin(temp)+1.4f);
//		float radius = (float)Math.sin(Math.pow(2, temp))-1.7f;
		float radius = 3.0f * (float) Math.cos(2 * temp);
//		sun.getTransform().setPos(new Vector3f(radius*3,10,0));
//		cube.getTransform().setPos(new Vector3f(radius*3, 0, -4));
//		monkey.getTransform().setPos(new Vector3f(radius*3,0,0));
//		cube.getTransform().setPos(MathUtils.getRight(camera.getTransform().getRot().conjugate(new Quaternionf())).add(0,0,-4));
//		Quaternionf rot = camera.getTransform().getRot();
//		camera.getTransform().setRot(new Quaternionf((float)Math.sin(temp),0,0,rot.w));
//		camera.getTransform().setRot(monkey.getTransform().getRot());
//		monkey.p
	}

	@Override
	public void render(RenderingEngine engine) {
		engine.render(root);
	}

	@Override
	public double displayTimes(double dividend) {
		return 0;
	}

	@Subscribe
	public void onMouseDown(MouseEvents.MouseButtonPressedEvent e) {
		System.out.println("Button: " + e.getMouseButton());
		System.out.println(e.getInput().getMousePosition());
		System.out.println(cube.getTransform().getTransformedPos());
		System.out.println(MathUtils.getForward(camera.getTransform().getTransformedRot()));
	}
}
