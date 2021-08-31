package com.kuzu.engine.core;

public class ProfileTimer {
	private int numInvocations;
	private double totalTime;
	private double startTime;

	public ProfileTimer() {
		numInvocations = 0;
		totalTime = 0;
		startTime = 0;
	}

	public void startInvocation() {
		startTime = Time.getTime();
	}

	public void stopInvocation() {
		if (startTime == 0) {
			throw new RuntimeException("Error: attempted stopping ProfileTimer without actually starting it");
		}

		numInvocations++;
		totalTime += (Time.getTime() - startTime);
		startTime = 0;
	}

	public double getTimeAndReset(double divisor) {
		divisor = (divisor == 0) ? numInvocations : divisor;
		double result = (totalTime == 0 && divisor == 0) ? 0 : (1000 * totalTime) / (divisor);
		totalTime = 0;
		numInvocations = 0;

		return result;
	}

	public double displayAndReset(String message, double divisor) {
		double time = getTimeAndReset(divisor);
		System.out.println(message + " " + time + " ms");
		return time;
	}
}
