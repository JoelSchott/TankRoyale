package tanks;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

public class Input {
	
	public Input(){
		pressed = new ArrayList<Integer>();
	}

	
	private ArrayList<Integer> pressed;

	
	public enum command{
		ARROW_P, WASD_Q, KEYPAD, AI
	}
	
	public void setDependency(JFrame f){
		f.addKeyListener(new KeyBoardListener());
		
	}
	
	public boolean isPressed(int i){
		if (pressed.contains(i)){
			return true;
		}
		else{
			return false;
		}
	}

	
	private class KeyBoardListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (! pressed.contains(e.getKeyCode())){
				pressed.add(e.getKeyCode());

			}
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			pressed.remove(Integer.valueOf(e.getKeyCode()));
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			
			
		}
		
		
	}

}
