package com.kuzu.game;

import com.kuzu.engine.core.CoreEngine;

import java.io.IOException;

public class Main {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final double FRAMECAP = 5000;
	public static final String TITLE = "3D Game Engine";

	public static void main(String[] args) throws IOException {
		CoreEngine game = new CoreEngine(WIDTH, HEIGHT, FRAMECAP, TITLE);
		game.pushLayer(new GameLayer());
		game.start();
	}
}
