package tanks;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import tanks.Input.command;
import tanks.Tank.team;

public class Maze1 extends Field{
	

	public static ArrayList<Point> getLocations(){
		ArrayList<Point> a = new ArrayList<Point>();
		a.add(new Point(60,160));
		a.add(new Point(725, 300));
		a.add(new Point(450, 400));
		return a;
	}
	
	
	public Maze1(){
		
		super(1000,1000);

		
		super.addWall(new Wall(360,270,100,20));
		super.addWall(new Wall(600,200,20,300));
		super.addWall(new Wall(130,90, 300,20));
		super.addWall(new Wall(100,300,20,180));
		
		super.addBush(new Bush (0, 0));
		super.addBush(new Bush(200, 190));
		super.addBush(new Bush(200, 400));
		super.addBush(new Bush(460, 100));

		
	}
	

}
