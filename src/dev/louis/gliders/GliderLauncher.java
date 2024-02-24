package dev.louis.gliders;

import dev.louis.gliders.input.GliderConfig;

public class GliderLauncher {
	
	private static GliderGame game;
	
	public static void main(String[] args) {
		setGame(1);
	}
	
	public static void setGame(int config) {
		if(game != null) game.stopRunning();
		
		if(config > 0 && config <= 9) {
			GliderConfig.create("res//gameConfig" + config + ".txt");
		} else {
			GliderConfig.create("res//gameConfig1.txt");
		}
		
		game = new GliderGame();
		game.start();
	}
	
	public static void stopGame() {
		if(game != null) game.stop();
		System.exit(0);
	}
}
