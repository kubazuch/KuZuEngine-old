package com.kuzu.engine.rendering;

import com.kuzu.engine.components.camera.Camera;
import com.kuzu.engine.components.light.BaseLight;
import com.kuzu.engine.core.GameObject;
import com.kuzu.engine.rendering.resources.MappedValues;
import com.kuzu.engine.rendering.shader.ForwardAmbientShader;
import com.kuzu.engine.rendering.shader.Shader;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class RenderingEngine extends MappedValues {
	private final Window window;

//	private ProfileTimer renderProfileTimer = new ProfileTimer();

	private HashMap<String, Integer> samplerMap;
	private ArrayList<BaseLight> lights;
	private BaseLight activeLight;

	private Shader forwardAmbient;
	private Camera mainCamera;

	//TODO: temp
	private Vector3f ambientLight;

	public RenderingEngine(Window window) {
		super();
		this.window = window;

		lights = new ArrayList<>();

		samplerMap = new HashMap<>();
		samplerMap.put("diffuse", 0);
		samplerMap.put("normalMap", 1);
		samplerMap.put("dispMap", 2);
		samplerMap.put("shadowMap", 3);

		addVector3f("ambient", new Vector3f(0.1f, 0.1f, 0.1f));

		forwardAmbient = new ForwardAmbientShader();

		glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

		glFrontFace(GL_CCW);
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);

//		glEnable(GL_DEPTH_CLAMP);

		glEnable(GL_TEXTURE_2D);

		ambientLight = new Vector3f(0.2f, 0.2f, 0.2f);
	}

	public double displayRenderTime(double dividend) {
//		return renderProfileTimer.displayAndReset("Render time: ", dividend);
		return 0;
	}

	public Vector3f getAmbientLight() {
		return ambientLight;
	}

	public static String getOpenGLVersion() {
		return glGetString(GL_VERSION);
	}

	public void render(GameObject object) {
		if (mainCamera == null) System.err.println("Main camera not found.");
//		renderProfileTimer.startInvocation();
		glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		object.renderAll(forwardAmbient, this, mainCamera);

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		glDepthMask(false);
		glDepthFunc(GL_EQUAL);

		for (BaseLight light : lights) {
			activeLight = light;
			object.renderAll(light.getShader(), this, mainCamera);
		}

		glDepthFunc(GL_LESS);
		glDepthMask(true);
		glDisable(GL_BLEND);
//		renderProfileTimer.stopInvocation();
	}

	public void addLight(BaseLight light) {
		lights.add(light);
	}

	public int getSamplerSlot(String samplerName) {
		return samplerMap.get(samplerName);
	}

	public BaseLight getActiveLight() {
		return activeLight;
	}

	public Camera getMainCamera() {
		return mainCamera;
	}

	public void setMainCamera(Camera mainCamera) {
		this.mainCamera = mainCamera;
	}
}
