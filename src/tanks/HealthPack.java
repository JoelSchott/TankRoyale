package tanks;

import java.awt.Point;

public class HealthPack extends Element{

	public HealthPack(Point loc){
		super.setLocation(loc);
		super.setWidth(Element.WIDTH);
	}
	
	private int healthBack = 400;

	@Override
	public void getBenefit(Tank t) {
		if (t.type == Tank.ROVER){
			if (t.health < Rover.MAX_HEALTH){
				t.health += healthBack;
			}
			if (t.health > Rover.MAX_HEALTH){
				t.health = Rover.MAX_HEALTH;
			}
		}
		
		else if (t.type == Tank.SNIPER){
			if (t.health < Sniper.MAX_HEALTH){
				t.health += healthBack;
			}
			if (t.health > Sniper.MAX_HEALTH){
				t.health = Sniper.MAX_HEALTH;
			}
		}
		
		else if (t.type == Tank.GRENADIER){
			if (t.health < Grenadier.MAX_HEALTH){
				t.health += healthBack;
			}
			if (t.health > Grenadier.MAX_HEALTH){
				t.health = Grenadier.MAX_HEALTH;
			}
		}
		
	}
	
	

}
