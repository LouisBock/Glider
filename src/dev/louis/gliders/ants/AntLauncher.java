package dev.louis.gliders.ants;

import dev.louis.gliders.input.AntConfig;

public class AntLauncher {
	public static void main(String[] args) {
		AntConfig.create("res//antConfig1.txt");
		
		AntGame game = new AntGame();
		game.start();
	}
}
