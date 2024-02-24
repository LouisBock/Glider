package dev.louis.gliders.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyManager implements KeyListener{
	
	public boolean[] keys;
	public boolean space;
	public boolean k1, k2, k3, k4, k5, k6, k7, k8, k9;
	
	public KeyManager() {
		keys = new boolean[256];
	}
	
	public void tick() {
		space = (keys[KeyEvent.VK_SPACE]);
		k1 = (keys[KeyEvent.VK_1]);
		k2 = (keys[KeyEvent.VK_2]);
		k3 = (keys[KeyEvent.VK_3]);
		k4 = (keys[KeyEvent.VK_4]);
		k5 = (keys[KeyEvent.VK_5]);
		k6 = (keys[KeyEvent.VK_6]);
		k7 = (keys[KeyEvent.VK_7]);
		k8 = (keys[KeyEvent.VK_8]);
		k9 = (keys[KeyEvent.VK_9]);
		
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}