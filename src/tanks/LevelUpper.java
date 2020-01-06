package tanks;

import java.awt.Point;

public class LevelUpper extends Element{

	
	public LevelUpper(Point loc){
		super.setLocation(loc);
		super.setWidth(Element.WIDTH);
	}

	@Override
	public void getBenefit(Tank t) {
		
		t.level ++;
		t.damageMultiplier += 0.25;
		System.out.println("increasing damage multiplier");
		
	}

}
