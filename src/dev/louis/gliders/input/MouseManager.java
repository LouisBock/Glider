package dev.louis.gliders.input;

import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseManager implements MouseListener{
	
	public boolean[] buttons;
	public boolean button1;
	public int xMouse = 0, yMouse = 0;
	
	public MouseManager() {
		buttons = new boolean[MouseInfo.getNumberOfButtons()];
	}
	
	public void tick() {
		button1 = (buttons[1]);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		buttons[e.getButton()] = true;
		xMouse = e.getX();
		yMouse = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttons[e.getButton()] = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

}
