package tanks;

import java.awt.Point;

public class Bullet extends Ammo{
	
	private final static int LIFESPAN = 1000;
	private final static int SPEED = 20;
	
	public Bullet(int a, Point l, Tank t){

		super(a,l,t, SPEED, 600, Ammo.BULLET, LIFESPAN);
			
	}

	public static int getLifespan(){
		return LIFESPAN;
		
	}
	
	public static int getSpeed(){
		return SPEED;
		
	}


	
}
