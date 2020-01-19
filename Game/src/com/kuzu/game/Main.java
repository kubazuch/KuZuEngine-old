package com.kuzu.game;

import com.kuzu.engine.core.CoreEngine;

import java.io.IOException;

public class Main {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final double FRAMERATE = 500;
	public static final String TITLE = "3D Game Engine";

	public static void main(String[] args) throws IOException {
		CoreEngine game = new CoreEngine(WIDTH, HEIGHT, FRAMERATE, TITLE);
		game.pushLayer(new GameLayer());
		game.start();
	}
}
