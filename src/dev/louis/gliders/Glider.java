package dev.louis.gliders;

import java.awt.Color;
import java.awt.Graphics;

import dev.louis.gliders.input.GliderConfig;

public class Glider {

	private GliderGame game;
	
	private Color col;
	private final float width, height;
	private final float speed;
	private final float radius;
	private final float randomDirFactor;
	private final float neighborAtraction;
	private final float lookDist;
	
	private final int group;
	private float x, y;
	private float dir;
	
	public Glider(GliderGame game, float x, float y, int group) {
		speed = GliderConfig.speed;
		radius = GliderConfig.radius;
		randomDirFactor = GliderConfig.randomDirFactor;
		neighborAtraction = GliderConfig.neighborAtraction;
		lookDist = GliderConfig.lookDist;
		
		this.game = game;
		this.width = game.getWidth() - radius;
		this.height = game.getHeight() - radius;
		this.group = group;
		
		this.x = x;
		this.y = y;
		dir = (float) (Math.random() * Math.PI * 2);
		
		int[] colorValues = GliderConfig.getGroupColor(group);
		for(int i = 0; i < 3; i++) {
			if(colorValues[i] < 0) {
				colorValues[i] *= -1;
				colorValues[i] = (int) (Math.random() * (256 - colorValues[i]) + colorValues[i]);
			}
		}
		col = new Color(colorValues[0], colorValues[1], colorValues[2]);
	}
	
	public void tick() {
		//direction calculation
		chaseNeighbors();
		randomizeDirection();
		
		//check bounds
//		checkBoundsRebound();
		checkBoundsTeleport();
		
		//manipulation
//		straighten();
	}

	public void render(Graphics g) {
		x += speed*(Math.cos(dir));
		y += speed*(Math.sin(dir));
		
		g.setColor(col);
		g.fillOval((int) x, (int) y, (int) radius, (int) radius);
	}
	
	/*
	private void checkBoundsRebound() {
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
	}*/
	
	private void checkBoundsTeleport() {
		if(x < 0) {
			x = width;
		} else if(x > width){
			x = 0;
		}
		
		if(y < 0) {
			y = height;
		} else if(y > height){
			y = 0;
		}
	}
	
	
	private void randomizeDirection() {
		float x = (float) (Math.random() * (Math.PI*randomDirFactor));
		dir += (Math.random() < 0.5d)?x:-x; 
		dir = (float) (dir%(Math.PI * 2));
	}
	
	private void chaseNeighbors() {
		float xFront = (float) (x + lookDist*(Math.cos(dir)));
		float yFront = (float) (y + lookDist*(Math.sin(dir)));
		
		float degree = (float) (Math.PI/3);
		
		float xLeft = (float) (x + lookDist*(Math.cos((dir - degree)%(Math.PI * 2))));
		float yLeft = (float) (y + lookDist*(Math.sin((dir - degree)%(Math.PI * 2))));
		
		float xRight = (float) (x + lookDist*(Math.cos((dir + degree)%(Math.PI * 2))));
		float yRight = (float) (y + lookDist*(Math.sin((dir + degree)%(Math.PI * 2))));
		
		int front = game.getNeighborCount(xFront, yFront, group);
		int left = game.getNeighborCount(xLeft, yLeft, group);
		int right = game.getNeighborCount(xRight, yRight, group);
		
		if(front < left || front < right) {
			if(left > right) {
				dir = (float) ((dir + Math.PI * 2 - Math.PI * neighborAtraction)%(Math.PI * 2));
			} else {
				dir = (float) ((dir + Math.PI * 2 + Math.PI * neighborAtraction)%(Math.PI * 2));
			}
		}
	}
	
	public void chaseMouse(int xMouse, int yMouse) {
		float dirMouse = (float) Math.atan((yMouse - y)/(xMouse - x));
		if((xMouse - x)<0) {
			dirMouse += Math.PI;
		}
		dir = dirMouse;
	}
	
	/*
	private void straighten() {
		float amount = 1.55f;
		float big = dir * amount;
		float round = Math.round(big);
		dir = round/amount;
	}*/
	
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
