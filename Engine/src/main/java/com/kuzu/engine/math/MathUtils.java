package com.kuzu.engine.math;

public class MathUtils {
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
}
