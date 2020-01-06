package tanks;

import java.awt.Point;

public abstract class Element {
	
	public final static int HEALTH = 0;
	public final static int WIDTH = 15;
	
	private Point location;
	
	private int width;
	
	public Point getLocation(){
		return location;
	}
	
	public void setLocation(Point p){
		location = p;
	}
	
	public int getWidth(){
		return width;
	}
	
	public void setWidth(int w){
		width = w;
	}
	
	public abstract void getBenefit(Tank t);

}
