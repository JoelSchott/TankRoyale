package tanks;

import java.awt.Color;

public class Bush extends Material{
	
	public final static Color DEFAULT_COLOR = new Color(115,225,45);
	public final static int WIDTH = 50;

	public Bush(int x, int y) {
		super(x, y, WIDTH, WIDTH, Bush.DEFAULT_COLOR);
		
	}

}
