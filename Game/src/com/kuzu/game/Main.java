package com.kuzu.game;

import com.kuzu.engine.core.MainComponent;

public class Main {
	public static void main(String[] args) {
		MainComponent game = MainComponent.INSTANCE;
		game.pushLayer(new GameLayer());
//		game.pushOverlay(new GameLayer("Overlay",1.0f, 0.15f));
		game.start();
	}
}
