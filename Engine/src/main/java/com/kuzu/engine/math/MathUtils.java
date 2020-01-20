package com.kuzu.engine.math;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MathUtils {

	public static final Vector3f FORWARD = new Vector3f(0, 0, -1);
	public static final Vector3f BACK = new Vector3f(0, 0, 1);
	public static final Vector3f UP = new Vector3f(0, 1, 0);
	public static final Vector3f DOWN = new Vector3f(0, -1, 0);
	public static final Vector3f RIGHT = new Vector3f(1, 0, 0);
	public static final Vector3f LEFT = new Vector3f(-1, 0, 0);

	@SuppressWarnings("unchecked")
	public static <T extends Number & Comparable> T clamp(T a, T min, T max) {
		if (a.compareTo(min) < 0) return min;
		else if (a.compareTo(max) > 0) return max;
		else return a;
	}

	public static float map(float value, float startFrom, float stopFrom, float startTo, float stopTo) {
		float out = startTo + (stopTo - startTo) * ((value - startFrom) / (stopFrom - startFrom));
		String err = null;
		if (out != out) {
			err = "NaN";
		} else if (out == Float.NEGATIVE_INFINITY || out == Float.POSITIVE_INFINITY) {
			err = "infinity";
		}

		if (err != null) {
			System.err.println(String.format("map(%s, %s, %s, %s, %s) called, returned %s",
					value, startFrom, stopFrom, startTo, stopTo, err));
		}

		return out;
	}

	public static Vector3f getForward(Quaternionf rot) {
		return rot.positiveZ(new Vector3f()).negate();
	}

	public static Vector3f getBack(Quaternionf rot) {
		return rot.positiveZ(new Vector3f());
	}

	public static Vector3f getUp(Quaternionf rot) {
		return rot.positiveY(new Vector3f());
	}

	public static Vector3f getDown(Quaternionf rot) {
		return rot.positiveY(new Vector3f()).negate();
	}

	public static Vector3f getRight(Quaternionf rot) {
		return rot.positiveX(new Vector3f());
	}

	public static Vector3f getLeft(Quaternionf rot) {
		return rot.positiveX(new Vector3f()).negate();
	}
}
