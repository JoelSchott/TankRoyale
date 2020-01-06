package tanks;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import tanks.Input.command;


public abstract class Tank {
	
	public Tank (Point p, team t, command c, File f, int kind, int h, int s){
		
		initLocation = p;
		location = p;
		side = t;
		control = c;
		
		viewTime = 0;
		type = kind;
		
		imageFile = f;
		
		angle = 0;
		health = h;
		speed = s;
		
		width = getImage().getWidth();
		height = getImage().getHeight();
	}
	
	public final static int MAXSHOTS = 3;
	public final static int VIEWTIME = 750;
	
	public final static int ROVER = 0;
	public final static int SNIPER = 1;
	public final static int GRENADIER = 2;
	
	
	public double damageMultiplier = 1;
	public int level = 1;
	
	public boolean visible;
	public int viewTime;
	public boolean radar = false;
	public int radarTime = 0;
	public boolean showAim = true;
	
	public Point location;
	public Point initLocation;
	
	public team side;
	public int type;
	
	public int health;
	public int speed;
	
	public File imageFile;
	public BufferedImage image;
	
	public int angle;
	
	public command control;
	
	public int width;
	public int height;
	
	public enum team{
		RED, BLUE, SOLO
	}

	
	public void rotateBy(int degrees){
		
		angle += degrees;
	}
	public BufferedImage getImage(){
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(-angle + 90), image.getWidth()/2 , image.getHeight()/2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return image = op.filter(image,null);
	}
	public Point getCenter(){
		int x = this.location.x + this.width/2;
		int y = this.location.y + this.height/2;
		return new Point(x,y);
	}
	
	public int getMathmaticalAngle(){
		int a = this.angle;
		while (a < 0){
			a += 360;
		}
		return a % 360;
	}
	
	public abstract int getShots();
	
	public abstract void reload(int time);
	
	public void forward(){
		location.x += Math.sin(Math.toRadians(angle + 90))*speed;
		location.y += Math.cos(Math.toRadians(angle + 90))*speed;
	}
	
	public void reverse(){
		location.x += Math.sin(Math.toRadians(angle + 90))*(-speed/2);
		location.y += Math.cos(Math.toRadians(angle + 90))*(-speed/2);
	}
	
	public abstract ArrayList<Ammo> fire();
	
	public void handleViewTime(int time){
		if (viewTime > 0){
			viewTime -= time;
		}
		if (viewTime < 0){
			viewTime = 0;
		}	
		if (this.radar){
			radarTime -= time;
		}
		if (radarTime < 0){
			radar = false;
			radarTime = 0;
		}
		if (radarTime > 0){
			this.radar = true;
		}
	}
	
	
}
