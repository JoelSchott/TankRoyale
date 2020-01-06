package tanks;

import java.awt.Point;
import java.util.ArrayList;

public class Maze2 extends Field{
	
	public static ArrayList<Point> getLocations(){
		ArrayList<Point> a = new ArrayList<Point>();
		a.add(new Point(30,30));
		a.add(new Point(900, 30));
		a.add(new Point(900, 900));
		return a;
	}

	public Maze2() {
		
		super(1000,1200);
		
		super.addWall(new Wall(100,100,20, 150));
		super.addWall(new Wall(100,100,150, 20));
		
		super.addWall(new Wall(250, 250, 20, 300));
		super.addWall(new Wall(80,450, 100, 20));
		
		super.addWall(new Wall(850, 100, 20, 150));
		super.addWall(new Wall(700, 100, 150, 20));
		
		super.addWall(new Wall(400,0, 20, 200));
		
		super.addWall(new Wall(850, 400, 20, 150));
		
		super.addWall(new Wall(500,150, 20,200));
		super.addWall(new Wall(500,250, 100,20));
		
		super.addWall(new Wall(600,600, 20, 200));
		super.addWall(new Wall(600,700, 150, 20));
		
		super.addBush(new Bush(250,350));
		super.addBush(new Bush(500,350));
		super.addBush(new Bush(0,600));
		super.addBush(new Bush(350,600));
		
	}

	
}
