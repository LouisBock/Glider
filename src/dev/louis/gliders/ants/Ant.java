package dev.louis.gliders.ants;

import java.awt.Color;
import java.awt.Graphics;

import dev.louis.gliders.input.AntConfig;

public class Ant {
	
	private AntGame game;
	
	private Color foodColor, homeColor, col;
	private final float width, height;
	private final float speed;
	private final float radius;
	private final float randomDirFactor;
	private final float pherosAtraction;
	private final float lookDist;
	private final int counterReset;
	private final int pheroStrength;
	
	private final int group;
	private float x, y;
	private float dir;
	private boolean hasFood = false, gotLost = false;
	private int counter;
	
	public Ant(AntGame game, float x, float y, int group) {
		speed = AntConfig.speed;
		radius = AntConfig.radius;
		randomDirFactor = AntConfig.randomDirFactor;
		pherosAtraction = AntConfig.pherosAtraction;
		lookDist = AntConfig.lookDist;
		counterReset = AntConfig.counterReset;
		pheroStrength = AntConfig.pheroStrength;
		
		
		homeColor = new Color(AntConfig.homeColor[0], AntConfig.homeColor[1], AntConfig.homeColor[2], AntConfig.homeColor[3]);
		foodColor = new Color(AntConfig.foodColor[0], AntConfig.foodColor[1], AntConfig.foodColor[2], AntConfig.foodColor[3]);
		
		this.game = game;
		this.width = game.getWidth() - radius;
		this.height = game.getHeight() - radius;
		this.group = group;
		
		this.x = x;
		this.y = y;
		dir = (float) (Math.random() * Math.PI * 2);
		counter = counterReset;
		
//		int[] colorValues = AntConfig.getGroupColor(group);
//		for(int i = 0; i < 3; i++) {
//			if(colorValues[i] < 0) {
//				colorValues[i] *= -1;
//				colorValues[i] = (int) (Math.random() * (256 - colorValues[i]) + colorValues[i]);
//			}
//		}
//		col = new Color(colorValues[0], colorValues[1], colorValues[2]);
		col = homeColor;
	}
	
	public void tick() {
		x += speed*(Math.cos(dir));
		y += speed*(Math.sin(dir));
		
		chasePheros();
		randomizeDirection();
		checkBounds();
		
		if(hasFood || gotLost) {
			searchHome();
		} else {
			searchFood();
		}
		
		dropPheros();
	}

	public void render(Graphics g) {
		if(counter > 0) {
			g.setColor(col);
			g.fillOval((int) x, (int) y, (int) radius, (int) radius);
		}
	}
	
	private void checkBounds() {
		if(x < 0) {
			x = 0;
			dir = (float) ((-dir + Math.PI * 3)%(Math.PI * 2));
		} else if(x > width){
			x = width;
			dir = (float) ((-dir + Math.PI * 3)%(Math.PI * 2));
		}
		
		if(y < 0) {
			y = 0;
			dir = (float) ((-dir + Math.PI * 2)%(Math.PI * 2));
		} else if(y > height){
			y = height;
			dir = (float) ((-dir + Math.PI * 2)%(Math.PI * 2));
		}
	}
	
	private void randomizeDirection() {
		float x = (float) (Math.random() * (Math.PI*randomDirFactor));
		dir += (Math.random() < 0.5d)?x:-x; 
		dir = (float) (dir%(Math.PI * 2));
	}
	
	private void chasePheros() {
		float xFront = (float) (x + lookDist*(Math.cos(dir)));
		float yFront = (float) (y + lookDist*(Math.sin(dir)));
		
		float degree = (float) (Math.PI/3);
		
		float xLeft = (float) (x + lookDist*(Math.cos((dir - degree)%(Math.PI * 2))));
		float yLeft = (float) (y + lookDist*(Math.sin((dir - degree)%(Math.PI * 2))));
		
		float xRight = (float) (x + lookDist*(Math.cos((dir + degree)%(Math.PI * 2))));
		float yRight = (float) (y + lookDist*(Math.sin((dir + degree)%(Math.PI * 2))));
		
		int front = game.getPheros(xFront, yFront, (gotLost || hasFood));
		int left = game.getPheros(xLeft, yLeft, (gotLost || hasFood));
		int right = game.getPheros(xRight, yRight, (gotLost || hasFood));
		
		if(front < left || front < right) {
			if(left > right) {
				dir = (float) ((dir + Math.PI * 2 - Math.PI * pherosAtraction)%(Math.PI * 2));
			} else {
				dir = (float) ((dir + Math.PI * 2 + Math.PI * pherosAtraction)%(Math.PI * 2));
			}
		}
	}
	
	private void dropPheros() {
		if(counter > 0) {
			game.dropPheros(x, y, hasFood, pheroStrength);
			counter--;
		} else if(!gotLost && !hasFood){
			gotLost = true;
			//change direction and do a 180
//			dir = (float) (dir + Math.PI);
//			dir = (float) (dir%(Math.PI * 2));
		}
	}
	
	private void searchFood() {
		if(game.hasFood(x, y)) {
			hasFood = true;
			col = foodColor;
			
			counter = counterReset;
			//change direction and do a 180
			dir = (float) (dir + Math.PI);
			dir = (float) (dir%(Math.PI * 2));
		}
	}

	private void searchHome() {
		if(game.isHome(x, y)) {
			hasFood = false;
			gotLost = false;
			col = homeColor;
			
			counter = counterReset;
			//change direction and do a 180
			dir = (float) (dir + Math.PI);
			dir = (float) (dir%(Math.PI * 2));
		}
	}
	
	public void chaseMouse(int xMouse, int yMouse) {
		float dirMouse = (float) Math.atan((yMouse - y)/(xMouse - x));
		if((xMouse - x)<0) {
			dirMouse += Math.PI;
		}
		dir = dirMouse;
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getDir() {
		return dir;
	}

	public int getGroup() {
		return group;
	}
}
