package tanks;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import tanks.Input.command;
import tanks.Tank.team;

public class Grenadier extends Tank{
	
	private int shots = Tank.MAXSHOTS;
	
	private int timeSinceFired = 0;
	
	public final static int MAX_HEALTH = 1200;
	public final static int RELOAD_TIME = 3500;
	public final static String RANGE = "Medium";
	public final static String DAMAGE_DESCRIPTION = "Exploding Grendade";
	public final static String NAME = "Grenadier";
	public final static String COLOR = "Green";
	
	public Grenadier(Point p, team t, command c){
		
		super(p,t,c, new File("C:\\Users\\Computer\\IdeaProjects\\TankRoyale\\src\\images\\greenTank.png"), Tank.GRENADIER, Grenadier.MAX_HEALTH, 7);

	}


	@Override
	public ArrayList<Ammo> fire() {
		
		viewTime = Tank.VIEWTIME;
		shots -= 1;
		ArrayList<Ammo> tempAmmo = new ArrayList<Ammo>();
		Point center = new Point (this.location.x + this.width/2, this.location.y + this.height/2);
		tempAmmo.add(new Grenade(this.angle, center, this));
		
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
