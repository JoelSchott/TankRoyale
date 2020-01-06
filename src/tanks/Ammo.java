package tanks;

import java.awt.Point;

public abstract class Ammo {
	
	public final static int OFFSET = 28;

	public Ammo(int ang, Point loc, Tank ta, int spe, int dama, int typ, int span){
		angle = ang;
		location = new Point(loc);
		source = ta;
		speed = spe;
		damage = dama;
		type = typ;
		lifespan = span;
	}
	
	public static final int SHELL = 0;
	public static final int BULLET = 1;
	public static final int GRENADE = 2;
	public static final int SHRAPNEL = 3;
	
	public Tank source;
	
	public int type;

	public Point location;
	
	public int damage;
	
	public int angle;
	
	public int speed;
	
	public int lifespan;
	
	public static int getLifespan(){
		throw new IllegalStateException("not set up");
	}
	
	public static int getSpeed(){
		throw new IllegalStateException("not set up");
	}
	
	public void move(){
		this.location.x += Math.cos(Math.toRadians(this.angle )) * speed;
		this.location.y -= Math.sin(Math.toRadians(this.angle ))* speed;
	}
	

}
