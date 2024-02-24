package dev.louis.gliders.ants;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import dev.louis.gliders.display.Display;
import dev.louis.gliders.input.AntConfig;
import dev.louis.gliders.input.KeyManager;
import dev.louis.gliders.input.MouseManager;

public class AntGame implements Runnable {
	private Display display;
	private int width, height;
	public String title;
	
	private boolean running = false;
	private Thread thread;
	private int fps = 60;
	
	private BufferStrategy bs;
	private Graphics g;
	
	//Input
	private KeyManager keyManager;
	private MouseManager mouseManager;
	
	private Ant[] ants;
	
	private int[][] homePheros, foodPheros, food; 
			
	private int lookRange;
	//private float radius;
	private int homeX = 400, homeY = 300, homeRadius = 15;
	
	private Color clearColor;//foodColor, homeColor,
	
	public AntGame() {
		title = "Ants";
		
		width = AntConfig.width;
		height = AntConfig.height;
		//radius = AntConfig.radius/2;
		ants = new Ant[AntConfig.antAmount];
		lookRange = AntConfig.lookRange;
		
		//homeColor = new Color(AntConfig.homeColor[0], AntConfig.homeColor[1], AntConfig.homeColor[2], AntConfig.homeColor[3]);
		//foodColor = new Color(AntConfig.foodColor[0], AntConfig.foodColor[1], AntConfig.foodColor[2], AntConfig.foodColor[3]);
		clearColor = new Color(AntConfig.clearColor[0], AntConfig.clearColor[1], AntConfig.clearColor[2], 5);
		
		homePheros = new int[height][width];
		foodPheros = new int[height][width];
		food = new int[height][width];

		keyManager = new KeyManager();
		mouseManager = new MouseManager();
	}
	
	private void init() {
		display = new Display(title, width, height);
		display.getFrame().addKeyListener(keyManager);
		display.getCanvas().addMouseListener(mouseManager);
		
		display.getCanvas().createBufferStrategy(3);
		bs = display.getCanvas().getBufferStrategy();
		
		//background black
		g = bs.getDrawGraphics();
		Color fullClear = new Color(clearColor.getRed(), clearColor.getGreen(), clearColor.getBlue(), 255);
		g.setColor(fullClear);
		g.fillRect(0, 0, width, height);
		bs.show();
		g.dispose();
		
		//init ants
		for(int i = 0; i < ants.length; i++) {
			ants[i] = new Ant(this, homeX, homeY, i % AntConfig.groupAmount);
		}
		
		//init food
		for(int i = 200; i < height-200; i++) {
			for(int j = 0; j < 10; j++) {
				food[i][j] = 1000;//TODO clean up
			}
		}
	}
	
	private void tick() {
		keyManager.tick();
		mouseManager.tick();
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if(homePheros[i][j] > 0) {
					homePheros[i][j]--;
				}
				if(foodPheros[i][j] > 0) {
					foodPheros[i][j]--;
				}
			}
		}
		
		for(Ant ant : ants) {
			ant.tick();
		}
	}
	
	private void render() {
		g = bs.getDrawGraphics();
		
		//Draw Here!
		g.setColor(clearColor);
		g.fillRect(0, 0, width, height);
		
		for(Ant ant : ants) {
			ant.render(g);
		}
		
//		renderPheros();
		
		g.setColor(Color.cyan);
		g.fillOval(homeX - homeRadius, homeY - homeRadius, 2*homeRadius, 2*homeRadius);
		
		//End Drawing!
		
		bs.show();
		g.dispose();
	}
	
	/*
	private void renderPheros() {
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if(homePheros[i][j] > 0) {
//					g.setColor(new Color(homeColor.getRed(), homeColor.getGreen(), homeColor.getBlue(), (int) homePheros[i][j]*255/pheroStrength));
					g.fillOval(j, i, (int)radius, (int)radius);
				}
				if(foodPheros[i][j] > 0) {
//					g.setColor(new Color(foodColor.getRed(), foodColor.getGreen(), foodColor.getBlue(), (int) foodPheros[i][j]*255/pheroStrength));
					g.fillOval(j, i, (int)radius, (int)radius);
				}
				
				if(food[i][j] > 0) {
					g.setColor(Color.orange);
					g.fillOval(j, i, (int)radius, (int)radius);
				}
			}
		}
	}*/

	public void run() {
		init();
		
		double timePerTick = 1000000000 / fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		
		while(running) {
			now = System.nanoTime();
			delta += (now - lastTime)/ timePerTick;
			timer += now - lastTime;
			lastTime = now;
			
			if(delta >= 1) {
				tick();
				render();
				delta--;
			}
			
			if (timer >= 1000000000) {
				timer = 0;
			}
		}
		stop();
	}
	
	public synchronized void start() {
		if(running) 
			return;
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		if(!running)
			return;
		
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void dropPheros(float xPos, float yPos, boolean hasFood, int pheroStrength) {
		int x = Math.round(xPos);
		int y = Math.round(yPos);
		
		if(hasFood) {
			foodPheros[y][x] = pheroStrength;
		} else {//food
			homePheros[y][x] = pheroStrength;
		}
	}
	
	public int getPheros(float xPos, float yPos, boolean hasFood){
		int x = Math.round(xPos);
		int y = Math.round(yPos);
		int count = 0;
		for(int i = Math.max(0, y-lookRange); i < Math.min(height, y+lookRange); i++) {
			for(int j = Math.max(0, x-lookRange); j < Math.min(width, x+lookRange); j++) {
				if(hasFood) {
					count += homePheros[i][j];
				} else {//food
					count += foodPheros[i][j];
				}
			}
		}
		return count;
	}
	
	public boolean hasFood(float xPos, float yPos){
		int x = Math.round(xPos);
		int y = Math.round(yPos);
		
		if(food[y][x] > 0) {
			food[y][x]--;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isHome(float xPos, float yPos){
		int x = Math.round(xPos);
		int y = Math.round(yPos);
		
		if((x > homeX-homeRadius && x < homeX+homeRadius) && (y > homeY-homeRadius && y < homeY+homeRadius)) {
			return true;
		} else {
			return false;
		}
	}
	
	public KeyManager getKeyManager() {
		return keyManager;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

}
