package tanks;

import java.awt.Point;

public class Shrapnel extends Ammo{
	
	private static final int SPEED = 20;
	private static final int LIFESPAN = 450;

	public Shrapnel(int a, Point l, Tank t){
		
		super(a, l, t, SPEED, 150, Ammo.SHRAPNEL, LIFESPAN);
			
	}
	
	public static int getLifespan(){
		return LIFESPAN;
		
	}
	
	public static int getSpeed(){
		return SPEED;
		
	}

}
