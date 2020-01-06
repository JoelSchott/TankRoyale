package tanks;

import java.awt.Point;

public class Explosion {
	
	public Explosion (Point p){
		location =  p;
	}
	
	private Point location;
	
	private int time = 600;
	
	public void reduceTime(int t){
		time -= t;
	}
	
	public int getTime(){
		return time;
	}
	
	public Point getLocation(){
		return location;
	}

}
