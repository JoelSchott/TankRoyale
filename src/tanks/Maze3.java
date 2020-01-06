package tanks;

import java.awt.Point;
import java.util.ArrayList;

public class Maze3 extends Field{

	public Maze3() {
		super(1000,1000);
		
		addStructure(200,200);
		addStructure(750, 300);
		addStructure(450, 570);
		
		addWall(new Wall(500, 350, 150, 20));
		addWall(new Wall(650, 300, 20 , 70));
		
		for (int x = 50; x < 350; x += Bush.WIDTH){
			for (int y = 600; y < 950; y += Bush.WIDTH){
				super.addBush(new Bush(x,y));
			}
		}
	}

	public void addStructure(int a, int b){
		super.addWall(new Wall(a, b, 20, 350));
		super.addWall(new Wall(a + 200, b - 100,20,350));
		for (int x = a; x < (a+200); x += Bush.WIDTH){
			for (int y = b - 100; y < (b + 350); y += Bush.WIDTH){
				super.addBush(new Bush(x,y));
			}
		}
		
	}
	
	public static ArrayList<Point> getLocations(){
		ArrayList<Point> a = new ArrayList<Point>();
		a.add(new Point(50,50));
		a.add(new Point(920,50));
		a.add(new Point(50,920));
		return a;
	}

}
