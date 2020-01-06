package tanks;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Field {
	
	public Field(int width, int height){
		
		this.width = width;
		this.height = height;
		
		walls = new ArrayList<Wall>();
		tanks = new ArrayList<Tank>();
		bushes = new ArrayList<Bush>();
	}
	
	public int width;
	public int height;

	public static ArrayList<Point> getLocations(){
		throw new IllegalStateException();
	}
	
	public ArrayList<Tank> tanks;
	
	public ArrayList<Wall> walls;
	
	public ArrayList<Bush> bushes;
	
	public void addTank(final Tank T){
		tanks.add(T);
	}
	
	public void addTank(int type, Tank.team team, Input.command control, Point location){
		if (type == Tank.ROVER){
			addTank(new Rover(location, team, control));
		}
		else if (type == Tank.SNIPER){
			addTank(new Sniper(location, team, control));
		}
		else if (type == Tank.GRENADIER){
			addTank(new Grenadier(location, team, control));
		}
	}
	
	public ArrayList<Tank> getTanks(){
		return tanks;
	}
	
	
	public void addWall(Wall w){
		walls.add(w);
	}
	
	public void addBush(Bush b){
		bushes.add(b);
	}
	
	
	
}
