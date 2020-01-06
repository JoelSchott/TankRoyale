package tanks;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import tanks.Input.command;

public class Rover extends Tank{

	
	private int shots = Tank.MAXSHOTS;
	
	private int timeSinceFired = 0;
	
	public final static int MAX_HEALTH = 1400;
	public final static int RELOAD_TIME = 2000;
	public final static String RANGE = "Medium";
	public final static String DAMAGE_DESCRIPTION = "Broad Spread of Shells";
	public final static String NAME = "Rover";
	public final static String COLOR = "Blue";
	
	public Rover(Point p, team t, command c){
		
		super(p,t,c, new File("C:\\Users\\Computer\\IdeaProjects\\TankRoyale\\src\\images\\blueTank.png"), Tank.ROVER, Rover.MAX_HEALTH, 6);

	}


	@Override
	public ArrayList<Ammo> fire() {
		
		viewTime = Tank.VIEWTIME;
		shots -= 1;
		ArrayList<Ammo> tempAmmo = new ArrayList<Ammo>();
		Point center = new Point (this.location.x + this.width/2, this.location.y + this.height/2);
		
		tempAmmo.add(new Shell(this.angle, center, this));
		tempAmmo.add(new Shell(this.angle - 2, center, this));
		tempAmmo.add(new Shell(this.angle + 2, center, this));
		tempAmmo.add(new Shell(this.angle - 5, center, this));
		tempAmmo.add(new Shell(this.angle + 5, center, this));
		tempAmmo.add(new Shell(this.angle - 8, center, this));
		tempAmmo.add(new Shell(this.angle -14, center, this));
		tempAmmo.add(new Shell(this.angle + 8, center, this));
		tempAmmo.add(new Shell(this.angle + 14, center, this));
		return tempAmmo;
		
	}

	@Override
	public int getShots() {
		return shots;
		
	}
	
	

	@Override
	public void reload(int time) {
		timeSinceFired += time;
		if (timeSinceFired > RELOAD_TIME && this.shots < Tank.MAXSHOTS){
			shots ++;
			timeSinceFired = 0;
		}
		
	}

	

}
