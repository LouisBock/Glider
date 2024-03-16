package dev.louis.gliders;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import dev.louis.gliders.display.Display;
import dev.louis.gliders.input.GliderConfig;
import dev.louis.gliders.input.KeyManager;
import dev.louis.gliders.input.MouseManager;

public class GliderGame implements Runnable{
	
	private Display display;
	private int width, height, sharpness;
	public String title;
	
	private boolean running = false;
	private Thread thread;
	private int fps = 120;
	
	private BufferStrategy bs;
	private Graphics g;
	private BufferedImage img;
	private final Color clearCol = new Color(GliderConfig.clearColor[0], GliderConfig.clearColor[1], GliderConfig.clearColor[2], GliderConfig.clearColor[3]);
	
	//Input
	private KeyManager keyManager;
	private MouseManager mouseManager;
	
	private Glider[] gliders;
	
	private float lookRange, lookRangeSquare, lookDist;
	private int gridSquareLength, gridWidth, gridHeight;
	private LinkedList<Integer>[][] grid;
	
	@SuppressWarnings("unchecked")
	public GliderGame() {
		title = "Gliders";
		
		width = GliderConfig.width;
		height = GliderConfig.height;
		sharpness = GliderConfig.sharpness;
		gliders = new Glider[GliderConfig.gliderAmount];
		lookRange = GliderConfig.lookRange;
		lookRangeSquare = (float) Math.pow(lookRange, 2);
		lookDist = GliderConfig.lookDist;
		
		gridSquareLength = (int) Math.ceil(lookRange + lookDist);
		gridWidth = (int) Math.ceil(width/gridSquareLength);
		gridHeight = (int) Math.ceil(height/gridSquareLength);
		
		grid = new LinkedList[gridHeight][gridWidth];
		
		for(int i = 0; i < gridHeight; i++) {
			for(int j = 0; j < gridWidth; j++) {
				grid[i][j] = new LinkedList<Integer>();
			}
		}
		
		keyManager = new KeyManager();
		mouseManager = new MouseManager();
	}
	
	private void init() {
		display = new Display(title, width, height);
		display.getFrame().addKeyListener(keyManager);
		display.getCanvas().addMouseListener(mouseManager);
		display.getCanvas().createBufferStrategy(3);
		bs = display.getCanvas().getBufferStrategy();
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
		
		updateGrid();
//		printGrid();
	}
	
	private void tick() {
		keyManager.tick();
		mouseManager.tick();
		
		for(Glider glider : gliders) {
			glider.tick();
		}
		
		updateGrid();
//		printGrid();
		
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
		if(sharpness > 0) img = blur(img);
		
		g = img.getGraphics();
		
		//Draw Here!
		
		//soft clear
		g.setColor(clearCol);//low alpha
		g.fillRect(0, 0, width, height);
		
		for(Glider glider : gliders) {
			glider.render(g);
		}
		
		//End Drawing!
		
		Graphics gbs = bs.getDrawGraphics();
		gbs.drawImage(img, 0, 0, null);
		bs.show();
		gbs.dispose();
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
		int xi = (int) Math.round(x/gridSquareLength);
		int yi = (int) Math.round(y/gridSquareLength);
		for(int i = xi-1; i <= xi+1; i++) {
			for(int j = yi-1; j <= yi+1; j++) {
				if(i >= 0 && i < gridWidth && j >= 0 && j < gridHeight) {
					LinkedList<Integer> list = grid[j][i];
					for(Integer k : list) {
						Glider glider = gliders[k];
						if((Math.pow(glider.getX()-x, 2) + Math.pow(glider.getY()-y, 2)) < lookRangeSquare) {
							if(glider.getGroup() == group) {
								count++;
							} else {
								count--;
							}
						}
					}
				}
			}
		}
			
		return count;
	}
	
//	public int getNeighborCount(float x, float y, int group){
//		int count = 0;
//		for(Glider glider : gliders) {
//			if((Math.pow(glider.getX()-x, 2) + Math.pow(glider.getY()-y, 2)) < lookRangeSquare) {
//				if(glider.getGroup() == group) {
//					count++;
//				} else {
//					count--;
//				}
//			}
//		}
//		return count;
//	}
	
	private void updateGrid(){
		for(int i = 0; i < gridHeight; i++) {
			for(int j = 0; j < gridWidth; j++) {
				grid[i][j].clear();
			}
		}
		
		for(int i = 0; i < gliders.length; i++) {
			int x = (int) Math.round(gliders[i].getX()/gridSquareLength);
			int y = (int) Math.round(gliders[i].getY()/gridSquareLength);
			
			if(x < 0) x = 0;
			if(x >= gridWidth) x = gridWidth - 1;
			if(y < 0) y = 0;
			if(y >= gridHeight) y = gridHeight - 1;
			
			grid[y][x].add(i);
		}
	}
	
	private BufferedImage blur(BufferedImage bi) {
		BufferedImage blured = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for(int x = 1; x < width - 1; x++) {
			for(int y = 1; y < height - 1; y++) {
				blured.setRGB(x, y, getBluredPixel(bi, x, y));
			}
		}
		
		return blured;
	}
	
	private int getBluredPixel(BufferedImage bi, int x, int y) {
		int blue = 0;
		int green = 0;
		int red = 0;
		int alpha = 0;
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				Color color = new Color(bi.getRGB(x+i, y+j));
				
				int strength = (i==0 && j==0)?sharpness:1;
				
				blue += color.getBlue() * strength;
				green += color.getGreen()* strength;
				red += color.getRed()* strength;
				alpha += color.getAlpha()* strength;
			}
		}
		int count = 8 + sharpness;
		blue /= count;
		green /= count;
		red /= count;
		alpha /= count;
		
		return new Color(red, green, blue, alpha).getRGB();
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
	
	private void printGrid() {
		for(int i = 0; i < grid.length; i++) {
			for(LinkedList<Integer> l : grid[i]) {
				System.out.print(l.size() + " ");
			}
			System.out.print("\n");
		}
		System.out.println("___________________________________________");
	}
}