package tanks;

import java.awt.Point;

public class Radar extends Element{

	public static final int TIME = 15000;
	
	public Radar(Point loc){
		super.setLocation(loc);
		super.setWidth(Element.WIDTH);
	}

	@Override
	public void getBenefit(Tank t) {
		
		t.radarTime = Radar.TIME;
		System.out.println("gave radar time");
		
	}
}
