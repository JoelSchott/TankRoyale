package tanks;

import java.awt.Point;

public class Shell extends Ammo {
	
	private final static int LIFESPAN = 600;
	private final static int SPEED = 15;

	public Shell(int a, Point l, Tank t){
		
		super(a,l,t, SPEED, 60, Ammo.SHELL, LIFESPAN);

			
	}
	
	public static int getLifespan(){
		return LIFESPAN;	
	}
	
	public static int getSpeed(){
		return SPEED;	
	}


}
