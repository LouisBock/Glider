package dev.louis.gliders;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import dev.louis.gliders.display.Display;
import dev.louis.gliders.input.GliderConfig;
import dev.louis.gliders.input.KeyManager;
import dev.louis.gliders.input.MouseManager;

public class GliderGame implements Runnable{
	
	private Display display;
	private int width, height;
	public String title;
	
	private boolean running = false;
	private Thread thread;
	private int fps = 120;
	
	private BufferStrategy bs;
	private Graphics g;
	private final Color clearCol = new Color(GliderConfig.clearColor[0], GliderConfig.clearColor[1], GliderConfig.clearColor[2], GliderConfig.clearColor[3]);
	
	//Input
	private KeyManager keyManager;
	private MouseManager mouseManager;
	
	private Glider[] gliders;
	
	private float lookRange;
	
	public GliderGame() {
		title = "Gliders";
		
		width = GliderConfig.width;
		height = GliderConfig.height;
		gliders = new Glider[GliderConfig.gliderAmount];
		lookRange = GliderConfig.lookRange;
		
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
		Color fullClear = new Color(clearCol.getRed(), clearCol.getGreen(), clearCol.getBlue(), 255);
		g.setColor(fullClear);
		g.fillRect(0, 0, width, height);
		bs.show();
		g.dispose();
		
		for(int i = 0; i < gliders.length; i++) {
			if(GliderConfig.randomPos) {
				gliders[i] = new Glider(this, (float) (Math.random() * width), (float) (Math.random() * height), i%GliderConfig.groupAmount);
			} else {
				gliders[i] = new Glider(this, width/2, height/2, i%GliderConfig.groupAmount);
			}
		}
	}
	
	private void tick() {
		keyManager.tick();
		mouseManager.tick();
		
		for(Glider glider : gliders) {
			glider.tick();
		}
		
		if(mouseManager.button1) {
			for(Glider glider : gliders) {
				glider.chaseMouse(mouseManager.xMouse, mouseManager.yMouse);
			}
		}
		
		if(keyManager.space) GliderLauncher.stopGame();
		
		if(keyManager.k1) GliderLauncher.setGame(1);
		if(keyManager.k2) GliderLauncher.setGame(2);
		if(keyManager.k3) GliderLauncher.setGame(3);
		if(keyManager.k4) GliderLauncher.setGame(4);
		if(keyManager.k5) GliderLauncher.setGame(5);
		if(keyManager.k6) GliderLauncher.setGame(6);
		if(keyManager.k7) GliderLauncher.setGame(7);
		if(keyManager.k8) GliderLauncher.setGame(8);
		if(keyManager.k9) GliderLauncher.setGame(9);
		
	}
	
	private void render() {
		g = bs.getDrawGraphics();
		
		//Draw Here!
		
		//soft clear
		g.setColor(clearCol);//low alpha
		g.fillRect(0, 0, width, height);
		
		for(Glider glider : gliders) {
			glider.render(g);
		}
		
		blur();
		//End Drawing!
		
		bs.show();
		g.dispose();
	}
	
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
	
	public int getNeighborCount(float x, float y, int group){
		int count = 0;
		for(Glider glider : gliders) {
			if((Math.pow(glider.getX()-x, 2) + Math.pow(glider.getY()-y, 2)) < lookRange) {
				if(glider.getGroup() == group) {
					count++;
				} else {
					count--;
				}
			}
		}
		return count;
	}
	
	private void blur() {
		
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
	
	public synchronized void start() {
		if(running) 
			return;
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		running = false;
		display.getFrame().dispose();
		if(thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void stopRunning() {
		running = false;
	}
}