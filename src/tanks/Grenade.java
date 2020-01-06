package tanks;

import java.awt.Point;
import java.util.ArrayList;

public class Grenade extends Ammo{
	
	private static final int SPEED = 15;
	private static final int LIFESPAN = 800;

	public Grenade(int a, Point l, Tank t){
		
		super(a, l, t, SPEED, 0, Ammo.GRENADE, LIFESPAN);
			
	}
	
	public static int getLifespan(){
		return LIFESPAN;
		
	}
	
	public static int getSpeed(){
		return SPEED;
		
	}
	
	public ArrayList<Shrapnel> explode(){
		ArrayList<Shrapnel> list = new ArrayList<Shrapnel>();
		ArrayList<Integer> angles = new ArrayList<Integer>();
		int angle = 30;
		for (int i = 0; i < 360; i += angle){
			angles.add(i);
		}
		System.out.println(angles.size());
		for (int i : angles){
			Shrapnel s = new Shrapnel(i, this.location, this.source);
			list.add(s);
		}
		
		return list;
	}

}
