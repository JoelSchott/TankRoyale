package tanks;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import tanks.Input.command;
import tanks.Tank.team;

public class Sniper extends Tank{

	
	private int shots = 2;
	
	private int timeSinceFired = 0;
	
	public final static int MAX_HEALTH = 1400;
	public final static int RELOAD_TIME = 2000;
	public final static String RANGE = "Long";
	public final static String DAMAGE_DESCRIPTION = "Single heavy-damage shot";
	public final static String NAME = "Sniper";
	public final static String COLOR = "Red";

	
	public Sniper(Point p, team t, command c){
		
		super(p,t,c, new File("C:\\Users\\Computer\\IdeaProjects\\TankRoyale\\src\\images\\redTank.png"), Tank.SNIPER, Sniper.MAX_HEALTH, 6);


	}


	@Override
	public ArrayList<Ammo> fire() {
		
		viewTime = Tank.VIEWTIME;
		shots -= 1;
		ArrayList<Ammo> tempAmmo = new ArrayList<Ammo>();
		Point center = new Point (this.location.x + this.width/2, this.location.y + this.height/2);
		tempAmmo.add(new Bullet(this.angle, center, this));

		return tempAmmo;
		
	}

	@Override
	public int getShots() {
		return shots;
		
	}

	@Override
	public void reload(int time) {
		timeSinceFired += time;
		if (timeSinceFired > RELOAD_TIME && this.shots < 2){
			shots ++;
			timeSinceFired = 0;
		}
		if (viewTime > 0){
			viewTime -= time;
		}
		if (viewTime < 0){
			viewTime = 0;
		}
		
	}


	
}
