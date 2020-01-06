package tanks;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.*;

import tanks.Input.command;
import tanks.Tank.team;

public class Main extends JFrame {
	
	private Input input;
	private Field field;
	
	private State state;
	
	private int selection_width = 600;
	private int selection_height = 600;
	
	private int viewWidth = 500;
	private int viewHeight = 1000;
	
	private JPanel mainPanel;	
	private JButton singleWallButton;
	private JButton maze2Button;
	private JButton caveButton;
	private JButton addPlayerButton;
	private JButton removePlayerButton;
	private JButton resultsButton;
	
	private boolean stateSetUp = false;
	
	private int loopPause = 50;
	private final static int BUFFER = 2;
	private final static int CLOSE = 100;
	
	private int maxElementInterval = 5000;
	private int elementInterval = maxElementInterval;
	
	boolean pHeld = false;
	boolean qHeld = false;
	boolean oHeld = false;
	boolean eHeld = false;
	boolean zeroHeld = false;
	boolean oneHeld = false;
	
	private ArrayList<Ammo> ammo = new ArrayList<Ammo>();	
	private ArrayList<Ammo> tempWeapons = new ArrayList<Ammo>();
	private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
	private ArrayList<Element> elements = new ArrayList<Element>();
	
	private ArrayList<SelectionPanel> selectionPanels = new ArrayList<SelectionPanel>();

	
	private enum State{
		SELECTION, PLAYING, ENDGAME
	}
	
	private enum Task{
		COLLECTING, ESCAPING, ATTACKING
	}
	
	private enum preferedRange{
		CLOSER, EQUAL, FARTHER
	}

	
	public static void main (String [] args){
		
		Main game = new Main();
		game.run();

	}
	
	public void run(){
		
		input = new Input();
		input.setDependency(this);
		
		this.requestFocus();
		this.setTitle("Tanks!");
		BufferedImage image = null;
		try{
			image = ImageIO.read(new File("C:\\Eclipse\\workspace\\Java Udemy\\src\\blueTank.png"));
		}
		catch(Exception e) { 
			System.out.println("problem with frame image");
		}
		this.setIconImage(image);

		this.setSize(selection_width, selection_height);		
		this.setResizable(false);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		mainPanel = new MainPanel();
		this.add(mainPanel,BorderLayout.CENTER);
		this.pack();
		this.setVisible(true);
		
		state = State.SELECTION;
		
		
		while (true){
			
			switch (state){
			
				case ENDGAME:
					
					if (!stateSetUp){
						mainPanel.add(resultsButton);
						mainPanel.revalidate();
						mainPanel.repaint();
						
						stateSetUp = true;
					}
			
				case SELECTION:
					
					if (!stateSetUp){
						
						mainPanel.removeAll();
						
						this.setSize(1000, 400);
						mainPanel.setSize(1000, 400);
						
						mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
						
						ButtonPanel bPanel = new ButtonPanel();;
						bPanel.add(singleWallButton, BorderLayout.NORTH);
						bPanel.add(maze2Button, BorderLayout.NORTH);
						bPanel.add(caveButton);
						bPanel.add(addPlayerButton, BorderLayout.NORTH);
						bPanel.add(removePlayerButton);
						
						mainPanel.add(bPanel);
						
						SelectionPanel left = new SelectionPanel();
						selectionPanels.add(0, left);
						mainPanel.add(left, BorderLayout.WEST);
						
						SelectionPanel right = new SelectionPanel();
						selectionPanels.add(1,right);
						mainPanel.add(right, BorderLayout.EAST);
								
						mainPanel.revalidate();
						mainPanel.repaint();
						
						stateSetUp = true;
						
					}

					break;
				
				case PLAYING:
					
					if (!stateSetUp){

						mainPanel.removeAll();
						mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
						
						for (int i =0 ; i < field.getTanks().size(); i ++){
							if (field.getTanks().get(i).control != Input.command.AI){
								mainPanel.add(new TankViewPanel(field.getTanks().get(i)));
							}
							
						}
						
						mainPanel.revalidate();
						mainPanel.repaint();

						
						this.setSize(selectionPanels.size() * viewWidth,viewHeight);
						mainPanel.setSize(selectionPanels.size() * viewWidth, viewHeight);
						
						selectionPanels.clear();
						
						stateSetUp = true;

						
					}
					
					handleInput();
					
					if (oneTeam()){
						System.out.println("Game Over");
						ammo.clear();
						tempWeapons.clear();
						elements.clear();
						explosions.clear();
						state = State.ENDGAME;
						stateSetUp = false;
					}
					
					elementInterval -= loopPause;
					if (elementInterval <= 0){
						elementInterval = maxElementInterval;
						Point loc = new Point((int) (Math.random() * field.width), (int) (Math.random() * field.height));
						Point topRight = new Point(loc.x  + Element.WIDTH, loc.y);
						Point bottomLeft = new Point(loc.x, loc.y  + Element.WIDTH);
						Point bottomRight = new Point(loc.x  + Element.WIDTH, loc.y  + Element.WIDTH);
						while (!(validElementLocation(loc) && validElementLocation(topRight) && validElementLocation(bottomLeft) && validElementLocation(bottomRight))){
							loc = new Point((int) (Math.random() * field.width), (int) (Math.random() * field.height));
							topRight = new Point(loc.x  + Element.WIDTH, loc.y);
							bottomLeft = new Point(loc.x, loc.y  + Element.WIDTH);
							bottomRight = new Point(loc.x  + Element.WIDTH, loc.y  + Element.WIDTH);
						}
						double r = Math.random();
						if (r < 0.2){
							elements.add(new HealthPack(loc));
						}
						else if (r >= 0.2 && r < 0.6){
							elements.add(new Radar(loc));
						}
						else if (r >= 0.6){
							elements.add(new LevelUpper(loc));
						}

					}
					Iterator<Element> i = elements.iterator();
					while (i.hasNext()){
						Element e = i.next();
						Point elementCenter = new Point(e.getLocation().x + e.getWidth()/2, e.getLocation().y + e.getWidth()/2);
						for (Tank t: field.tanks){
							if (elementCenter.x > t.location.x && elementCenter.x < t.location.x + t.width && elementCenter.y > t.location.y && elementCenter.y < t.location.y + t.height){
								e.getBenefit(t);
								i.remove();
								System.out.println("gave benefit");
								break;
							}
						}
						
					}
					
					Iterator<Explosion> eit = explosions.iterator();
					while (eit.hasNext()){
						Explosion e = eit.next();
						if (e.getTime() <= 0){
							eit.remove();
						}
						else{
							e.reduceTime(loopPause);
						}
					}
					
					Iterator<Tank> it = field.tanks.iterator();
					while (it.hasNext()){
						Tank t = it.next();
						t.handleViewTime(loopPause);
						if (t.getClass() == Sniper.class){
							if (t.getShots() < 2){
								t.reload(loopPause);
							}
						}
						else if (t.getShots() < Tank.MAXSHOTS){
							t.reload(loopPause);
						}
						Point center = new Point (t.location.x + t.width/2, t.location.y + t.height/2);
						boolean isVisible = true;
						for (Bush b : field.bushes){
							if (center.x < (b.getX() + b.getWidth() + BUFFER) && center.x > b.getX() - BUFFER && center.y > b.getY() - BUFFER && center.y < (b.getY() + b.getHeight() + BUFFER)){
								isVisible = false;
								
							}
						}

						if (t.viewTime > 0){
							isVisible = true;
						}
						t.visible = isVisible;
						if (t.health <= 0){
							
							elements.add(new LevelUpper(t.location));
							elements.add(new LevelUpper(new Point(t.location.x + t.width, t.location.y)));
							elements.add(new LevelUpper(new Point(t.location.x, t.location.y + t.height)));
							elements.add(new LevelUpper(new Point(t.location.x + t.width, t.location.y + t.height)));
							
							try{
								it.remove();
							}
							catch(IllegalStateException e){
								System.out.println("problem removing tank");
							}
						}
						
					}


					Iterator<Ammo> iter = ammo.iterator();
					while (iter.hasNext()){
						Ammo a = iter.next();
						a.move();
						a.lifespan -= loopPause;
						if (a.lifespan < 0){
							if (a.getClass() == Grenade.class){
								Grenade g = (Grenade)(a);
								addToWeapons(g);
								
							}
							iter.remove();
							
						}
						for (Tank t : field.tanks){
							if (((t.side == team.BLUE || t.side == team.RED) && t.side != a.source.side) || (t.side == team.SOLO && t != a.source)){
								int deltaX = Math.abs(t.location.x + (t.width/2) - a.location.x);
								int deltaY = Math.abs(t.location.y + (t.height/2) - a.location.y);
								int distance = (int) Math.sqrt((deltaX*deltaX) + (deltaY * deltaY));

								if (distance < t.width/2){
									t.viewTime = Tank.VIEWTIME;
									explosions.add(new Explosion(a.location));
									t.health -= (int)(a.damage * a.source.damageMultiplier);
									System.out.println("damage multiplier was " + a.source.damageMultiplier);
									if (a.getClass() == Grenade.class){
										Grenade g = (Grenade)(a);
										addToWeapons(g);
										
									}
									try{
										iter.remove();
									}
									catch (IllegalStateException e){
										System.out.println("problem with ammo hitting tank");
									}

									break;
									
								}
							}
							
							
						}

						if (!inBounds(a.location)){

							try{
								iter.remove();
							}
							catch (IllegalStateException e){
								System.out.println("error with ammo hitting wall");
							}
								
						}
					}


					break;
				}
			ammo.addAll(tempWeapons);
			tempWeapons.clear();
			
			this.getContentPane().repaint();
			this.repaint();
			mainPanel.repaint();
			this.requestFocus();

			
			try{
				Thread.sleep(loopPause);
				}
			catch(Exception e){e.printStackTrace();}
		

		}
		
	}
	
	private void handleInput(){
		
		for (Tank t: field.tanks){
			
			if (t.control == Input.command.AI){
				
				preferedRange idealRange = preferedRange.EQUAL;
				
				
				Task action = Task.COLLECTING;
				ArrayList<Tank> visibleTanks = new ArrayList<Tank>();
				for (Tank tank : field.tanks){
					if (Math.abs(tank.location.y - t.location.y) < viewHeight/2 && Math.abs(tank.location.x - t.location.x) < viewWidth/2
							&& tank != t && (tank.visible || distance(tank, t) < Main.CLOSE) && (tank.side != t.side || t.side == Tank.team.SOLO)){
						visibleTanks.add(tank);
					}
				}
				
				if (visibleTanks.size() > 0){
					Tank closest = visibleTanks.get(0);
					for (Tank enemy : visibleTanks){
						if (distance(t, enemy) < distance(t,closest)){
							closest = enemy;
						}
					}
					int tankRange = 0;
					int enemyRange = 0;
					if (t.type == Tank.ROVER){
						tankRange = Shell.getLifespan() * Shell.getSpeed();
					}
					else if (t.type == Tank.SNIPER){
						tankRange = Bullet.getLifespan() * Bullet.getSpeed();
					}
					else if (t.type == Tank.GRENADIER){
						tankRange = Grenade.getLifespan() * Grenade.getSpeed();
					}
					if (closest.type == Tank.ROVER){
						enemyRange = Shell.getLifespan() * Shell.getSpeed();
					}
					else if (closest.type == Tank.SNIPER){
						enemyRange = Bullet.getLifespan() * Bullet.getSpeed();
					}
					else if (closest.type == Tank.GRENADIER){
						enemyRange = Grenade.getLifespan() * Grenade.getSpeed();
					}
					

					if (tankRange < enemyRange){
						idealRange = preferedRange.CLOSER;
					}
					else if (tankRange > enemyRange){
						idealRange = preferedRange.FARTHER;
					}
					System.out.println("ideal range is " + idealRange.toString());
					

					double targetx = closest.location.x + (Math.cos(Math.toRadians(closest.getMathmaticalAngle())) * (double)closest.speed * 1.6);
					double targety = closest.location.y - (Math.sin(Math.toRadians(closest.getMathmaticalAngle())) * (double)closest.speed * 1.6);
					
					double xDif = targetx - t.location.x;
					double yDif = t.location.y - targety;
					
					
					double targetAngle;
					try{
						targetAngle = Math.atan(yDif/xDif);
					}
					catch(ArithmeticException ae){
						targetAngle = 3 * Math.PI/2;
						System.out.println("DIRECTLY BELOW");
					}
					if (targetx <= t.location.x){
						targetAngle += Math.PI;
					}
					targetAngle = Math.toDegrees((targetAngle + 2*Math.PI) % (2*Math.PI));
					
					action = Task.ATTACKING;
					if (action == Task.ATTACKING){
						int angleDifference = Math.abs((int)(targetAngle) - t.getMathmaticalAngle());
						if (angleDifference > 5){
							if (angleDifference > 180){
								if (targetAngle > t.getMathmaticalAngle()){
									t.rotateBy(-5);
								}
								else{
									t.rotateBy(5);
								}
							}
							else{
								if (targetAngle > t.getMathmaticalAngle()){
									t.rotateBy(5);
								}
								else{
									t.rotateBy(-5);
								}
							}
						}
						
						angleDifference = Math.abs((int)(targetAngle) - t.getMathmaticalAngle());
						int range = 0;
						if (t.getClass() == Rover.class){
							range = (Shell.getLifespan()/loopPause) * Shell.getSpeed();
						}
						else if (t.getClass() == Sniper.class){
							range = (Bullet.getLifespan()/loopPause) * Bullet.getSpeed();
						}
						else if (t.getClass() == Grenadier.class){
							range = (Grenade.getLifespan()/loopPause) * Grenade.getSpeed();
						}
						
						Point predictedLoc = new Point((int)(targetx),(int)(targety));
						int dis = distance(t.location, predictedLoc) - (t.width*7/10);
						boolean wallInWay = true;
						if (wallBetween(t.getCenter(), closest.getCenter()) == null){
							wallInWay = false;
						}
						
						if (t.getClass() == Rover.class){
							if (t.getShots() > 0 && !wallInWay){
								if (angleDifference < 5 && dis < range){
									if (t.getShots() < 3){
										if (dis < range - 55){
											ammo.addAll(t.fire());
											
											System.out.println("close");
										}
											
									}
									else{
										ammo.addAll(t.fire());
										System.out.println("far");
									}
									
										
								}
							}
							
							
						}
						else if (t.getClass() == Grenadier.class && t.getShots() > 0 && !wallInWay){
							if (angleDifference < 5 && dis < range + 30){
								if (t.getShots() < 3){
									if (dis < range){
										ammo.addAll(t.fire());
										
									}
								}
								else{
									ammo.addAll(t.fire());
								}	
							}
						}
						else if (angleDifference < 5 && dis < range + 45 && !wallInWay){
							if(t.getShots() > 0){
								ammo.addAll(t.fire());
							}	
						}
						if (idealRange == preferedRange.CLOSER || idealRange == preferedRange.EQUAL){
							if ((t.getShots() >= closest.getShots() || closest.health < 600) && dis > 50 && Math.abs(t.getMathmaticalAngle() - targetAngle) < 90){
								Point tempLoc = new Point(t.location.x, t.location.y);
								t.forward();
								
								if (!inBounds(t)){
									t.location = tempLoc;
									System.out.println("out of bounds");
								}
							}
							else if ((t.getShots() < closest.getShots() || Math.abs(t.getMathmaticalAngle() - targetAngle) > 90) && closest.health > 600 && idealRange != preferedRange.CLOSER){
								Point tempLoc = new Point(t.location.x, t.location.y);
								t.reverse();
								
								if (!inBounds(t)){
									t.location = tempLoc;
									System.out.println("out of bounds");
								}
							}
							
						}
						else if (idealRange == preferedRange.FARTHER){
							Point tempLoc = new Point(t.location.x, t.location.y);
							t.reverse();
							
							if (!inBounds(t)){
								t.location = tempLoc;
								System.out.println("out of bounds");
							}
						}
						
						
						System.out.println("Wall between nearest is " + wallBetween(t.getCenter(), closest.getCenter()));
						System.out.println("distance is " + dis);
						System.out.println("range is " + range);
						
	
					}	
				}
				else{
					
					Element targetElement = null;
					if (elements.size() > 0){
						action = Task.COLLECTING;
						targetElement = elements.get(0);
						double closest = 99999999;
						for (Element e : elements){
							double deltax = Math.abs(t.location.x - e.getLocation().x);
							double deltay = Math.abs(t.location.y - e.getLocation().y);
							double distance = Math.abs((deltax * deltax) + (deltay * deltay));
							if (distance < closest){
								targetElement = e;
								closest = distance;
							}
						}
					}
					
					if (action == Task.COLLECTING && targetElement != null){
						double xDif = 0;
						double yDif = 0;
						Wall w = wallBetween(t.location, targetElement.getLocation());
						Wall wa = wallBetween(new Point(t.location.x + t.width, t.location.y), targetElement.getLocation());
						Wall wb = wallBetween(new Point(t.location.x, t.location.y + t.height), targetElement.getLocation());
						Wall wc = wallBetween(new Point(t.location.x + t.width, t.location.y + t.height), targetElement.getLocation());
						Wall[] wallsInWay = {w, wa, wb, wc};
						
						for (Wall wall : wallsInWay){
							if (wall != null){
								System.out.println("going around");
								Point aroundPoint = moveAround(t, wall, targetElement);
								xDif = aroundPoint.x - t.getCenter().x;
								yDif = t.getCenter().y - aroundPoint.y;
			
							}
						}
						if ( w == null && wa == null && wb== null && wc == null){
							xDif = targetElement.getLocation().x - t.getCenter().x;
							yDif = t.getCenter().y - targetElement.getLocation().y;
						}
						
						

						
						double angle = 0;
						try{
							angle = Math.atan(yDif/xDif);
						}
						catch(Exception e){
							angle = 3 * Math.PI/2;
						}
						if (targetElement.getLocation().x < t.getCenter().x){
							angle += Math.PI;
						}
						
						
						if (distance (new Point(0, t.getCenter().y), t.getCenter()) < t.width || distance (new Point(field.width, t.getCenter().y), t.getCenter()) < t.width ){
							if (Math.toDegrees(angle) < 120 && Math.toDegrees(angle) > 60){
								angle = Math.PI /2;
							}
							else if (Math.toDegrees(angle) > 240 && Math.toDegrees(angle) < 300){
								angle = Math.PI * 3/2;
							}
						}
						if (distance (new Point(t.getCenter().x, 0), t.getCenter()) < t.width || distance (new Point(t.getCenter().x, field.height), t.getCenter()) < t.width ){
							if (Math.toDegrees(angle) < 30 || Math.toDegrees(angle) > 330){
								angle = 0;
							}
							else if (Math.toDegrees(angle) > 150 && Math.toDegrees(angle) < 210){
								angle = Math.PI;
							}
						}
						
						angle = Math.toDegrees((angle + 2*Math.PI) % (2*Math.PI));
						
						if (Math.abs(angle - t.getMathmaticalAngle()) > 5){
							if (Math.abs(angle - t.getMathmaticalAngle()) < 180){
								if (angle > t.getMathmaticalAngle()){
									t.rotateBy(5);
								}
								else{
									t.rotateBy(-5);
								}
							}
							else{
								if (angle > t.getMathmaticalAngle()){
									t.rotateBy(-5);
								}
								else{
									t.rotateBy(5);
								}
							}
							
						}
						
						
						
						Point tempLoc = new Point(t.location.x, t.location.y);
						t.forward();
						
						if (!inBounds(t)){
							t.location = tempLoc;
							System.out.println("out of bounds");
						}
						
					}
				}
				//System.out.println("Seeing " + visibleTanks.size() + " tanks");
				
			}
		}
		if (input.isPressed(KeyEvent.VK_UP)){
			for (Tank t: field.tanks){
				if (t.control == command.ARROW_P){
					Point tempLoc = new Point(t.location.x, t.location.y);
					t.forward();
					
					if (!inBounds(t)){
						t.location = tempLoc;
						System.out.println("out of bounds");
					}
					
				}
			}
		}
		else if (input.isPressed(KeyEvent.VK_DOWN)){
			for (Tank t : field.tanks){
				if (t.control == command.ARROW_P){
					Point tempLoc = new Point(t.location.x, t.location.y);
					t.reverse();
					
					
					if (!inBounds(t)){
						t.location = tempLoc;
						System.out.println("out of bounds");
					}
				}
			}
		}
		if (input.isPressed(104)){
			for (Tank t: field.tanks){
				if (t.control == command.KEYPAD){
					Point tempLoc = new Point(t.location.x, t.location.y);
					t.forward();
					
					if (!inBounds(t)){
						t.location = tempLoc;
						System.out.println("out of bounds");
					}
					
				}
			}
		}
		else if (input.isPressed(101)){
			for (Tank t : field.tanks){
				if (t.control == command.KEYPAD){
					Point tempLoc = new Point(t.location.x, t.location.y);
					t.reverse();
					
					if (!inBounds(t)){
						t.location = tempLoc;
						System.out.println("out of bounds");
					}
				}
			}
		}
		if (input.isPressed(KeyEvent.VK_W)){
			for (Tank t: field.tanks){
				if (t.control == command.WASD_Q){
					Point tempLoc = new Point(t.location.x, t.location.y);
					t.forward();
					
					if (!inBounds(t)){
						t.location = tempLoc;
						System.out.println("out of bounds");
					}
					
				}
			}
		}
		else if (input.isPressed(KeyEvent.VK_S)){
			for (Tank t : field.tanks){
				if (t.control == command.WASD_Q){
					Point tempLoc = new Point(t.location.x, t.location.y);
					t.reverse();
					
					
					if (!inBounds(t)){
						t.location = tempLoc;
						System.out.println("out of bounds");
					}
				}
			}
		}
		
		
		if (input.isPressed(KeyEvent.VK_LEFT)){
			for (Tank t: field.tanks){
				if (t.control == command.ARROW_P){
					t.rotateBy(5);
									
					if (!inBounds(t)){
						t.rotateBy(-5);
						System.out.println("out of bounds");
					}
				}
			}
		}
		else if (input.isPressed(KeyEvent.VK_RIGHT)){
			for (Tank t: field.tanks){
				if (t.control == command.ARROW_P){
					t.rotateBy(-5);
					if (!inBounds(t)){
						t.rotateBy(5);
					}
				}
			}
		}
		if (input.isPressed(100)){
			for (Tank t: field.tanks){
				if (t.control == command.KEYPAD){
					t.rotateBy(5);
									
					if (!inBounds(t)){
						t.rotateBy(-5);
						System.out.println("out of bounds");
					}
				}
			}
		}
		else if (input.isPressed(102)){
			for (Tank t: field.tanks){
				if (t.control == command.KEYPAD){
					t.rotateBy(-5);
					if (!inBounds(t)){
						t.rotateBy(5);
					}
				}
			}
		}
		if (input.isPressed(KeyEvent.VK_A)){
			for (Tank t: field.tanks){
				if (t.control == command.WASD_Q){
					t.rotateBy(5);
					
					if (!inBounds(t)){
						t.rotateBy(-5);
					}
				}
			}
		}
		else if (input.isPressed(KeyEvent.VK_D)){
			for (Tank t: field.tanks){
				if (t.control == command.WASD_Q){
					t.rotateBy(-5);
					
					if (!inBounds(t)){
						t.rotateBy(5);
					}
				}
			}
		}
		if (input.isPressed(KeyEvent.VK_P) && pHeld == false){
			pHeld = true;
			for (Tank t: field.tanks){
				if (t.control == command.ARROW_P && t.getShots() > 0){
					ammo.addAll(t.fire());
				}
			}
		}
		if (! input.isPressed(KeyEvent.VK_P)){
			pHeld = false;
		}
		if (input.isPressed(KeyEvent.VK_Q) && qHeld == false){
			qHeld = true;
			for (Tank t: field.tanks){
				if (t.control == command.WASD_Q && t.getShots() > 0){
					ammo.addAll(t.fire());
				}
			}
		}
		
		if (! input.isPressed(KeyEvent.VK_Q)){
			qHeld = false;
		}
		if (input.isPressed(96) && zeroHeld == false){
			zeroHeld = true;
			for (Tank t: field.tanks){
				if (t.control == command.KEYPAD && t.getShots() > 0){
					ammo.addAll(t.fire());
				}
			}
		}
		
		if (! input.isPressed(96)){
			zeroHeld = false;
		}
		
		if (input.isPressed(KeyEvent.VK_O) && !oHeld){
			oHeld = true;
			for (Tank t: field.tanks){
				if (t.control == command.ARROW_P){
					t.showAim = !t.showAim;
				}
			}
		}
		
		if (!input.isPressed(KeyEvent.VK_O)){
			oHeld = false;
		}
		
		if (input.isPressed(KeyEvent.VK_E) && !eHeld){
			eHeld = true;
			for (Tank t: field.tanks){
				if (t.control == command.WASD_Q){
					t.showAim = !t.showAim;
				}
			}
		}
		
		if (!input.isPressed(KeyEvent.VK_E)){
			eHeld = false;
		}
		
		if (input.isPressed(97) && !oneHeld){
			oneHeld = true;
			for (Tank t: field.tanks){
				if (t.control == command.KEYPAD){
					t.showAim = !t.showAim;
				}
			}
		}
		
		if (!input.isPressed(97)){
			oneHeld = false;
		}
		
		

	}
	
	private class MainPanel extends JPanel{

		
		public MainPanel(){
			this.setPreferredSize(new Dimension(selection_width, selection_height));
			this.setBackground(Color.GRAY);
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.setVisible(true);
			singleWallButton = new JButton("Play Ruins");
			singleWallButton.addActionListener(new singleWallListener());
			singleWallButton.setVisible(true);
			maze2Button = new JButton("Play Maze");
			maze2Button.addActionListener(new Maze2Listener());
			maze2Button.setVisible(true);
			caveButton = new JButton("Play Cave");
			caveButton.addActionListener(new CaveListener());
			caveButton.setVisible(true);
			addPlayerButton = new JButton("Add a player");
			addPlayerButton.addActionListener(new addPlayerListener());
			addPlayerButton.setVisible(true);
			removePlayerButton = new JButton("Remove a Player");
			removePlayerButton.addActionListener(new RemovePlayerListener());
			removePlayerButton.setVisible(true);
			resultsButton = new JButton("Play Again");
			resultsButton.addActionListener(new ResultsButtonListener());
			resultsButton.setVisible(true);
		}
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);

//			if (state == State.PLAYING){
//				this.setBackground(Color.LIGHT_GRAY);
//				
//				try{
//					
//					
//					for (Bush b: field.bushes){
//						g.setColor(Bush.getColor());
//						g.fillRect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
//					}
//					for (Tank t: field.tanks){
//						
//						if (t.visible){
//							
//							
//							g.setColor(Color.WHITE);
//							
//							if (t.type == Tank.ROVER && t.showAim){
//								int span = Shell.getLifespan();
//								int frames = span/loopPause;
//								int distance = frames * Shell.getSpeed();
//								int x = t.location.x + Ammo.OFFSET;
//								int y = t.location.y + Ammo.OFFSET;
//								int[] xLocs = {x, (int) (x + (Math.cos(Math.toRadians(t.getMathmaticalAngle())) * distance)), 
//										(int) (x + Math.cos(Math.toRadians(t.getMathmaticalAngle() + 14)) * distance)};
//								int[] yLocs = {y, (int) (y - (Math.sin(Math.toRadians(t.getMathmaticalAngle())) * distance)), 
//										(int) (y - Math.sin(Math.toRadians(t.getMathmaticalAngle() + 14)) * distance)};
//								g.fillPolygon(new Polygon(xLocs, yLocs, 3));
//								
//								int[] secondXLocs = {x, (int) (x + (Math.cos(Math.toRadians(t.getMathmaticalAngle())) * distance)), 
//										(int) (x + Math.cos(Math.toRadians(t.getMathmaticalAngle() - 14)) * distance)};
//								int[] secondYLocs = {y, (int) (y - (Math.sin(Math.toRadians(t.getMathmaticalAngle())) * distance)), 
//										(int) (y - Math.sin(Math.toRadians(t.getMathmaticalAngle() - 14)) * distance)};
//								g.fillPolygon(new Polygon(secondXLocs, secondYLocs, 3));
//							
////
////								g.drawLine(x, y, (int) (x + (Math.cos(Math.toRadians(t.getMathmaticalAngle())) * distance)), (int) (y - (Math.sin(Math.toRadians(t.getMathmaticalAngle())) * distance)));
////								g.drawLine(x, y, (int) (x + Math.cos(Math.toRadians(t.getMathmaticalAngle() + 14)) * distance), (int) (y - Math.sin(Math.toRadians(t.getMathmaticalAngle() + 14)) * distance));
////								g.drawLine(x, y, (int) (x + Math.cos(Math.toRadians(t.getMathmaticalAngle() - 14)) * distance), (int) (y - Math.sin(Math.toRadians(t.getMathmaticalAngle() - 14)) * distance));	
//							}
//							
//							else if (t.type == Tank.SNIPER && t.showAim){
//								int span = Bullet.getLifespan();
//								int frames = span/loopPause;
//								int distance = frames * Bullet.getSpeed();
//								int x = t.location.x + Ammo.OFFSET;
//								int y = t.location.y + Ammo.OFFSET;
//								
//								g.drawLine(x, y, (int) (x + Math.cos(Math.toRadians(t.getMathmaticalAngle()))*distance), (int)(y - Math.sin(Math.toRadians(t.getMathmaticalAngle())) * distance));
//							
//							}
//							
//							else if (t.type == Tank.GRENADIER && t.showAim){
//								int span = Grenade.getLifespan();
//								int frames = span/loopPause;
//								int distance = frames * Grenade.getSpeed();
//								int x = t.location.x + Ammo.OFFSET;
//								int y = t.location.y + Ammo.OFFSET;
//								
//								g.drawLine(x, y, (int) (x + Math.cos(Math.toRadians(t.getMathmaticalAngle()))*distance), (int)(y - Math.sin(Math.toRadians(t.getMathmaticalAngle())) * distance));
//							
//							}
//							
//							
//							g.setColor(Color.RED);
//							
////							int centerX = t.location.x + t.width/2;
////							int centerY = t.location.y + t.height/2;
//	//
////							double modifier = 1.7;
////							double centerMod = 2.35;
////							//System.out.println("x modification " + (int)Math.cos(Math.toRadians(t.getMathmaticalAngle())) * t.getImage().getWidth()/2);
////							g.drawOval((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()+45)) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()+45)) * t.height/modifier), 4, 4);
////							g.drawOval((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()+135)) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()+135)) * t.height/modifier), 4, 4);
////							g.drawOval((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()-45)) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()-45)) * t.height/modifier), 4, 4);
////							g.drawOval((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()-135)) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()-135)) * t.height/modifier), 4, 4);
//	//
////							g.drawOval((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()+270)) * t.width/centerMod), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()+270)) * t.height/centerMod),4,4);
////							
////							g.drawOval(centerX - (t.width/2), centerY - (t.height/2), t.width, t.height);
//
//							for (int i= 0; i < t.getShots(); i ++){
//								g.drawRect(t.location.x + i * 20, t.location.y + t.getImage().getHeight(), 15, 10);
//							}
//							g.drawString(String.valueOf(t.health), t.location.x, t.location.y - 10);
//							
//							g.setColor(Color.BLUE);
//				
//							g.drawString(String.valueOf(t.level), t.location.x + t.width, t.location.y - 10);
//						}
//						
//						
//					}
//					
//					for (Wall w : field.walls){
//						g.setColor(Wall.getColor());
//						g.fillRect(w.getX(), w.getY(), w.getWidth(), w.getHeight());
//					}
//					
//					
//					for (Tank t : field.tanks){
//						
//						if (t.visible){
//							g.drawImage(t.getImage(), t.location.x, t.location.y, this);
//						}
//
			
//						
//					}
//					for (Ammo a: weapons){
//						if (a.type == Ammo.SHELL){
//							g.setColor(Color.BLACK);
//							g.fillOval(a.location.x, a.location.y, 5, 5);
//						}
//						else if (a.type == Ammo.BULLET){
//							g.setColor(Color.RED);
//							g.fillRect(a.location.x, a.location.y, 6, 6);
//						}
//						else if (a.type == Ammo.GRENADE){
//							g.setColor(Color.black);
//							g.fillRect(a.location.x, a.location.y, 7, 7);
//							g.setColor(Color.RED);
//							g.fillRect(a.location.x+3, a.location.y+3, 2, 2);
//						}
//						else if (a.type == Ammo.SHRAPNEL){
//							g.setColor(Color.BLACK);
//							g.fillOval(a.location.x, a.location.y, 6,6);
//							g.setColor(Color.RED);
//							g.fillOval(a.location.x + 2, a.location.y + 2, 2, 2);
//						}
//					}
//					for (Explosion e: explosions){
//						g.setColor(Color.RED);
//						g.fillOval(e.getLocation().x, e.getLocation().y, 10, 10);
//						g.setColor(Color.ORANGE);
//						g.fillOval(e.getLocation().x +2, e.getLocation().y + 2, 6, 6);
//					}
//					for (Element e: elements){
//						if (e.getClass() == HealthPack.class){
//							g.setColor(Color.WHITE);
//							g.fillRect(e.getLocation().x, e.getLocation().y, HealthPack.WIDTH, HealthPack.WIDTH);
//							g.setColor(Color.RED);
//							g.fillRect(e.getLocation().x + HealthPack.WIDTH/3, e.getLocation().y, HealthPack.WIDTH/3, HealthPack.WIDTH);
//							g.fillRect(e.getLocation().x, e.getLocation().y + HealthPack.WIDTH/3, HealthPack.WIDTH, HealthPack.WIDTH/3);
//						}
//						else if (e.getClass() == LevelUpper.class){
//							g.setColor(Color.YELLOW);
//							int[] xPoints = {e.getLocation().x , e.getLocation().x + e.getWidth()/2, e.getLocation().x + e.getWidth()};
//							int[] yPoints = {e.getLocation().y + e.getWidth(), e.getLocation().y, e.getLocation().y + e.getWidth()};
//							g.fillPolygon(new Polygon(xPoints, yPoints, 3));
//							g.setColor(Color.MAGENTA);
//							g.drawLine(e.getLocation().x, e.getLocation().y + e.getWidth(), e.getLocation().x + e.getWidth()/2, e.getLocation().y);
//							g.drawLine(e.getLocation().x + e.getWidth()/2, e.getLocation().y, e.getLocation().x + e.getWidth(), e.getLocation().y + e.getWidth());
//							g.drawLine(e.getLocation().x, e.getLocation().y + e.getWidth(), e.getLocation().x + e.getWidth(), e.getLocation().y + e.getWidth());
//						}
//					}
//					
//					
//				}
//			catch (NullPointerException n){
//				System.out.println("not set up yet!");
//				}
//			}
		
		}
	}
	
	private class ButtonPanel extends JPanel{
		
		public ButtonPanel(){
			this.setBackground(Color.GRAY);
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.setVisible(true);
		}
	}
	
	private class TankViewPanel extends JPanel{
		
		private Tank tank;
		
		public TankViewPanel(Tank t){
			this.setPreferredSize(new Dimension(viewWidth,viewHeight));
			this.setBackground(Color.LIGHT_GRAY);
			this.setVisible(true);
			this.tank = t;
		}
		
		@Override 
		public void paintComponent(Graphics g){
			
			super.paintComponent(g);
			
			this.setBackground(Color.LIGHT_GRAY);
			
			if (state == State.PLAYING){
			
				int xcenter = viewWidth/2 - (tank.width/2);
				int ycenter = viewHeight/2 - (tank.height/2);
				
				int xOffset = tank.location.x - xcenter;
				int yOffset = tank.location.y - ycenter;
				
				g.setColor(Color.ORANGE);
				g.fillRect(0 - xOffset, 0-yOffset - 10, field.width, 10);
				g.fillRect(0 - xOffset - 10, 0 - yOffset, 10, field.height);
				g.fillRect(0 - xOffset, field.height - yOffset, field.width, 10);
				g.fillRect(field.width - xOffset, 0 - yOffset, 10, field.height);
				
				
				
				
				
				for (Tank t: field.tanks){
					
					if ( t == tank || (t.side == tank.side && tank.side != Tank.team.SOLO)){
						
						g.setColor(Color.WHITE);
						
						if (t.type == Tank.ROVER && t.showAim){
							int span = Shell.getLifespan();
							int frames = span/loopPause;
							int distance = frames * Shell.getSpeed();
							int x = t.location.x + Ammo.OFFSET - xOffset;
							int y = t.location.y + Ammo.OFFSET - yOffset;
							int[] xLocs = {x, (int) (x + (Math.cos(Math.toRadians(t.getMathmaticalAngle())) * distance)), 
									(int) (x + Math.cos(Math.toRadians(t.getMathmaticalAngle() + 14)) * distance)};
							int[] yLocs = {y, (int) (y - (Math.sin(Math.toRadians(t.getMathmaticalAngle())) * distance)), 
									(int) (y - Math.sin(Math.toRadians(t.getMathmaticalAngle() + 14)) * distance)};
							g.fillPolygon(new Polygon(xLocs, yLocs, 3));
							
							int[] secondXLocs = {x, (int) (x + (Math.cos(Math.toRadians(t.getMathmaticalAngle())) * distance)), 
									(int) (x + Math.cos(Math.toRadians(t.getMathmaticalAngle() - 14)) * distance)};
							int[] secondYLocs = {y, (int) (y - (Math.sin(Math.toRadians(t.getMathmaticalAngle())) * distance)), 
									(int) (y - Math.sin(Math.toRadians(t.getMathmaticalAngle() - 14)) * distance)};
							g.fillPolygon(new Polygon(secondXLocs, secondYLocs, 3));
						
						}
						
						else if (t.type == Tank.SNIPER && t.showAim){
							int span = Bullet.getLifespan();
							int frames = span/loopPause;
							int distance = frames * Bullet.getSpeed();
							int x = t.location.x + Ammo.OFFSET - xOffset;
							int y = t.location.y + Ammo.OFFSET - yOffset;
							
							g.drawLine(x, y, (int) (x + Math.cos(Math.toRadians(t.getMathmaticalAngle()))*distance), (int)(y - Math.sin(Math.toRadians(t.getMathmaticalAngle())) * distance));
						
						}
						
						else if (t.type == Tank.GRENADIER && t.showAim){
							int span = Grenade.getLifespan();
							int frames = span/loopPause;
							int distance = frames * Grenade.getSpeed();
							int x = t.location.x + Ammo.OFFSET - xOffset;
							int y = t.location.y + Ammo.OFFSET - yOffset;
							
							g.drawLine(x, y, (int) (x + Math.cos(Math.toRadians(t.getMathmaticalAngle()))*distance), (int)(y - Math.sin(Math.toRadians(t.getMathmaticalAngle())) * distance));
						
						}
	
					}
					
				}
				
				for (Bush b: field.bushes){
					int centerx = tank.location.x + tank.width/2;
					int centery = tank.location.y + tank.height/2;
					if (centerx < (b.getX() + b.getWidth() + BUFFER) && centerx > b.getX() - BUFFER && centery > b.getY() - BUFFER && centery < (b.getY() + b.getHeight() + BUFFER)){
						b.setColor(new Color (25,125,60));
						
					}

					else{
						b.setColor(Bush.DEFAULT_COLOR);
					}
					g.setColor(b.getColor());
					g.fillRect(b.getX() - xOffset, b.getY() - yOffset, b.getWidth(), b.getHeight());
					
				}
				
				for (Wall w : field.walls){
					g.setColor(w.getColor());
					g.fillRect(w.getX() - xOffset, w.getY() - yOffset, w.getWidth(), w.getHeight());
				}
				
				for (Tank t: field.tanks){
					
					
					
					if (t.visible || t == tank || (t.side == tank.side && tank.side != Tank.team.SOLO) || distance(t, tank) < Main.CLOSE){
	
						
						if (t.side == Tank.team.BLUE){
							g.setColor(Color.BLUE);
						}
						else if (t.side == Tank.team.RED){
							g.setColor(Color.RED);
						}
						else if (t.side == Tank.team.SOLO){
							g.setColor(Color.BLACK);
						}
						
						
						for (int i= 0; i < t.getShots(); i ++){
							g.drawRect(t.location.x - xOffset + i * 20, t.location.y - yOffset + t.getImage().getHeight(), 15, 10);
						}
					
						g.drawString(String.valueOf(t.health), t.location.x - xOffset, t.location.y - yOffset - 10);
						g.drawString(String.valueOf(t.level), t.location.x + t.width - xOffset, t.location.y - 10 - yOffset);
						
						g.drawImage(t.getImage(), t.location.x - xOffset, t.location.y - yOffset, this);
					}
				}
				
				for (Ammo a: ammo){
					int x = a.location.x - xOffset;
					int y = a.location.y - yOffset;
					if (a.type == Ammo.SHELL){
						g.setColor(Color.BLACK);
						g.fillOval(x, y, 5, 5);
					}
					else if (a.type == Ammo.BULLET){
						g.setColor(Color.RED);
						g.fillRect(x, y, 6, 6);
					}
					else if (a.type == Ammo.GRENADE){
						g.setColor(Color.black);
						g.fillRect(x, y, 7, 7);
						g.setColor(Color.RED);
						g.fillRect(x+3, y+3, 2, 2);
					}
					else if (a.type == Ammo.SHRAPNEL){
						g.setColor(Color.BLACK);
						g.fillOval(x, y, 6,6);
						g.setColor(Color.RED);
						g.fillOval(x + 2, y + 2, 2, 2);
					}
				}
				
				for (Explosion e: explosions){
					int x = e.getLocation().x - xOffset;
					int y = e.getLocation().y - yOffset;
					g.setColor(Color.RED);
					g.fillOval(x, y, 10, 10);
					g.setColor(Color.ORANGE);
					g.fillOval(x +2, y + 2, 6, 6);
				}
				
				for (Element e: elements){
					int x = e.getLocation().x - xOffset;
					int y = e.getLocation().y - yOffset;
					if (e.getClass() == HealthPack.class){
						g.setColor(Color.WHITE);
						g.fillRect(x, y, HealthPack.WIDTH, HealthPack.WIDTH);
						g.setColor(Color.RED);
						g.fillRect(x + HealthPack.WIDTH/3, y, HealthPack.WIDTH/3, HealthPack.WIDTH);
						g.fillRect(x, y + HealthPack.WIDTH/3, HealthPack.WIDTH, HealthPack.WIDTH/3);
					}
					else if (e.getClass() == LevelUpper.class){
						g.setColor(Color.YELLOW);
						int[] xPoints = {x , x + e.getWidth()/2, x + e.getWidth()};
						int[] yPoints = {y + e.getWidth(), y, y + e.getWidth()};
						g.fillPolygon(new Polygon(xPoints, yPoints, 3));
						g.setColor(Color.MAGENTA);
						g.drawLine(x, y + e.getWidth(), x + e.getWidth()/2, y);
						g.drawLine(x + e.getWidth()/2, y, x + e.getWidth(), y + e.getWidth());
						g.drawLine(x, y + e.getWidth(), x + e.getWidth(), y + e.getWidth());
					}
					else if (e.getClass() == Radar.class){
						g.setColor(Color.BLUE);
						g.drawArc(x, y, e.getWidth(), e.getWidth(), 0, 180);
						g.drawArc((int)(x + e.getWidth()/3.3),(int)( y + e.getWidth()/3.3), e.getWidth()/2, e.getWidth()/2, 0, 180);
						g.drawArc((int)(x - e.getWidth()/3.8),(int)( y - e.getWidth()/3.8), (int)(e.getWidth()*1.5), (int)(e.getWidth()*1.5), 0, 180);
						g.fillOval(x - 2 + e.getWidth()/2, y + e.getWidth() - 3, 5, 5);
					}
				}

				if (tank.radar){
					double xScale = (double)(viewWidth/4)/(double)field.width;
					double yScale = (double)(viewHeight/8)/(double)field.height;
					System.out.println("xScale is " + xScale);
					System.out.println("Y scale is " + yScale);
					g.setColor(Color.lightGray);
					g.fillRect(20,0, (int)(field.width * xScale), (int)(field.height * yScale));

					
					for (Bush b : field.bushes){
						g.setColor(Bush.DEFAULT_COLOR);
						g.fillRect((int)((b.getX() * xScale) + 20),(int)( b.getY() * yScale), 
								(int)(b.getWidth() * xScale), (int)(b.getHeight() * yScale));
					}
					for (Wall w : field.walls){
						g.setColor(Color.DARK_GRAY);
						g.fillRect((int)((w.getX() * xScale) + 20),(int)( w.getY() * yScale), 
								(int)(w.getWidth() * xScale), (int)(w.getHeight() * yScale));
					}
					for (Tank t : field.tanks){
						if (t.side == Tank.team.BLUE){
							g.setColor(Color.BLUE);
						}
						else if (t.side == Tank.team.RED){
							g.setColor(Color.RED);
						}
						else if (t.side == Tank.team.SOLO){
							g.setColor(Color.BLACK);
						}
						g.fillRect((int)(t.location.x * xScale) + 20, (int)(t.location.y * yScale), 
								(int)(t.width * xScale), (int)(t.height * yScale));
					}
					g.setColor(Color.ORANGE);
					g.fillRect(20, 0, (int) (xScale * field.width), 2);
					g.fillRect(20, 0, 2, (int)(field.height * yScale));
					g.fillRect(20, (int)(yScale * field.height), (int)(xScale * field.width), 2);
					g.fillRect((int)(xScale * field.width) + 20, 0, 2, (int)(yScale * field.height));
				}
				
				
				
				
			}
			
			else if (state == State.ENDGAME){
				boolean won = false;
				for (Tank t: field.tanks){
					if (tank.side == t.side || tank == t){
						won = true;
					}
				}
				if (won){
					g.setColor(Color.RED);
					g.drawString("Congratulations - Your Team Won!", 100,100);
				}
				else{
					g.setColor(Color.BLUE);
					g.drawString("Sorry - Better Luck Next Time", 100,100);
				}
				g.drawImage(tank.getImage(), 150, 200, this);
				
			}

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 20, viewHeight);
			
		}
	}
	

	
	private class SelectionPanel extends JPanel{
		
		JLabel nameLabel;
		JLabel blankLabel;
		JLabel healthLabel;
		JLabel reloadLabel;
		JLabel rangeLabel;
		JLabel damageLabel;
		JLabel colorLabel;
		
		JList<String> menu;
		String[] choices = {"Rover", "Sniper", "Grenadier"};
		
		JList<String> teamMenu;
		String[] teams = {"Red", "Blue", "Solo"};
		
		JCheckBox box;
		
		
		public SelectionPanel(){

			this.setBackground(Color.GRAY);
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.setVisible(true);
		
			
			nameLabel = new JLabel("Tank : " + Rover.NAME);
			this.add(nameLabel);
			
			blankLabel = new JLabel("           ");
			this.add(blankLabel);
			
			healthLabel = new JLabel("Health : " + Rover.MAX_HEALTH);
			this.add(healthLabel);
			
			reloadLabel = new JLabel("Reload Time : " + (double) Rover.RELOAD_TIME/1000 + " seconds");
			this.add(reloadLabel);
			
			rangeLabel = new JLabel("Range : " + Rover.RANGE);
			this.add(rangeLabel);
			
			damageLabel = new JLabel("Damage : " + Rover.DAMAGE_DESCRIPTION);
			this.add(damageLabel);
			
			colorLabel = new JLabel("Color : " + Rover.COLOR);
			this.add(colorLabel);
			
			
			menu = new JList<String> (choices);
			menu.setSelectedIndex(0);
			this.add(menu);
			
			this.add(new JLabel("Team :"));
			
			teamMenu = new JList<String> (teams);
			teamMenu.setSelectedIndex(0);
			this.add(teamMenu);
			
			box = new JCheckBox("AI");
			this.add(box);
			
			System.out.println("made new selection panel");
			
		}
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			
			if (menu.getSelectedValue() == "Rover"){
				selection = Tank.ROVER;
				nameLabel.setText("Tank : " + Rover.NAME);
				blankLabel.setText("           ");
				healthLabel.setText("Health : " + Rover.MAX_HEALTH);
				reloadLabel.setText("Reload Time : " + (double) Rover.RELOAD_TIME/1000 + " seconds");
				rangeLabel.setText("Range : " + Rover.RANGE);
				damageLabel.setText("Damage : " + Rover.DAMAGE_DESCRIPTION);
				colorLabel.setText("Color : " + Rover.COLOR);
				
			}
			else if (menu.getSelectedValue() == "Sniper"){
				selection = Tank.SNIPER;
				nameLabel.setText("Tank : " + Sniper.NAME);
				blankLabel.setText("           ");
				healthLabel.setText("Health : " + Sniper.MAX_HEALTH);
				reloadLabel.setText("Reload Time : " + (double) Sniper.RELOAD_TIME/1000 + " seconds");
				rangeLabel.setText("Range : " + Sniper.RANGE);
				damageLabel.setText("Damage : " + Sniper.DAMAGE_DESCRIPTION);
				colorLabel.setText("Color : " + Sniper.COLOR);
			}
			
			else if (menu.getSelectedValue() == "Grenadier"){
				selection = Tank.GRENADIER;
				nameLabel.setText("Tank : " + Grenadier.NAME);
				blankLabel.setText("           ");
				healthLabel.setText("Health : " + Grenadier.MAX_HEALTH);
				reloadLabel.setText("Reload Time : " + (double) Grenadier.RELOAD_TIME/1000 + " seconds");
				rangeLabel.setText("Range : " + Grenadier.RANGE);
				damageLabel.setText("Damage : " + Grenadier.DAMAGE_DESCRIPTION);
				colorLabel.setText("Color : " + Grenadier.COLOR);
			}
			
			
		}
		
		int selection = Tank.ROVER;
		
		int getSelectedType(){
			return selection;
		}
		
		boolean isAI(){
			return box.isSelected();
		}
		
		Tank.team getTeam(){
			if (teamMenu.getSelectedValue() == "Red"){
				return Tank.team.RED;
			}
			else if (teamMenu.getSelectedValue() == "Blue"){
				return Tank.team.BLUE;
			}
			else {
				return Tank.team.SOLO;
			}
		}
		
	}
	
	private class ResultsButtonListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e){
			state = State.SELECTION;
			stateSetUp = false;
		}
	}
	
	private class singleWallListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {	

			
			field = new Maze1();
			fieldUtilitySetup(Maze1.getLocations());
		}
		
	}
	
	private class Maze2Listener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {	

			
			field = new Maze2();
			fieldUtilitySetup(Maze2.getLocations());
		}
		
	}
	
	private class CaveListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {	

			field = new Maze3();
			fieldUtilitySetup(Maze3.getLocations());
		}
		
	}
	
	private class addPlayerListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (selectionPanels.size() < 3){
				SelectionPanel tempPanel = new SelectionPanel();
				selectionPanels.add(selectionPanels.size(), tempPanel);
				//mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
				mainPanel.add(tempPanel, BorderLayout.EAST);
				
				mainPanel.revalidate();
				mainPanel.repaint();
			}
			
			
		}
		
	}
	
	private class RemovePlayerListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e){
			if (selectionPanels.size() > 2){
				SelectionPanel lastPanel = selectionPanels.get(selectionPanels.size() -1);
				mainPanel.remove(lastPanel);
				selectionPanels.remove(lastPanel);
				
				mainPanel.revalidate();
				mainPanel.repaint();
				
			}
		}
	}
	private Wall wallBetween(Point a, Point b){
		double deltax = b.x - a.x;
		double deltay = b.y - a.y;
		double slope;
		
		try{
			slope = deltay/deltax;
		}
		catch(ArithmeticException ae){
			slope = -999;
		}
		
		double yIntercept = a.y - (slope * a.x);
		
		for (Wall w: field.walls){
			
			Integer[] xList = {w.getX(), w.getX() + w.getWidth()};
			for (int i = 0; i < 2; i ++){
				if ((xList[i] < a.x && xList[i] > b.x) || (xList[i] > a.x && xList[i] < b.x)){
					int y = (int)(slope*xList[i] + yIntercept);
					if (y > w.getY() && y < w.getY() + w.getHeight()){
						return w;
					}
					//System.out.println(a.x + "," + a.y + " : " + centerx + "," + y + " : " + b.x + "," + b.y );
				}
			}
			
			Integer[] yList = {w.getY(), w.getY() + w.getHeight()};
			for (int i = 0; i < 2; i ++){
				if ((yList[i] < a.y && yList[i] > b.y) || (yList[i] > a.y && yList[i] < b.y)){
					int x = (int)((yList[i] - yIntercept)/slope);
					if (x > w.getX() && x < w.getX() + w.getWidth()){
						return w;
					}
				}
			}
			
			
		}

		return null;
	}
	
	private Point moveAround(Tank tank, Wall w, Element e){
		
		double centerx = w.getX() + (w.getWidth()/2);
		double centery = w.getY() + w.getHeight()/2;
		
		double buffer = 1.5;
		
		Point topLeft = new Point(w.getX() - (int)(buffer*tank.width), w.getY() - (int)(buffer *tank.height));
		Point bottomRight = new Point(w.getX() + w.getWidth() + (int)(buffer *tank.width), w.getY() + w.getHeight() + (int)(buffer * tank.height));
		Point topRight = new Point (w.getX() + w.getWidth() + (int) (buffer * tank.width), w.getY() - (int) (buffer * tank.height));
		Point bottomLeft = new Point (w.getX() - (int) (buffer * tank.width), w.getY() + w.getHeight() + (int) (buffer * tank.height));
		
		double topLeftCorner = distance(tank.getCenter(), topLeft);
		double topRightCorner = distance(tank.getCenter(), topRight);
		double bottomLeftCorner = distance(tank.getCenter(), bottomLeft);
		double bottomRightCorner = distance(tank.getCenter(), bottomRight);
		Double[] pointList = {topLeftCorner, topRightCorner, bottomLeftCorner, bottomRightCorner};
		
		int cornerDistance = 10;
		
//		if (topLeftCorner < cornerDistance || topRightCorner < cornerDistance || bottomLeftCorner < cornerDistance || bottomRightCorner < cornerDistance){
			
			double lowest = topLeftCorner;
			Point finalGoal = topLeft;
			for (double d : pointList){
				if (d < lowest){
					lowest = d;
					if (d == topLeftCorner){
						finalGoal = topLeft;
					}
					else if (d == bottomRightCorner){
						finalGoal = bottomRight;
					}
					else if (d == topRightCorner){
						finalGoal = topRight;
					}
					else{
						finalGoal = bottomLeft;
					}
				}
			}
			
			if (finalGoal == topLeft || finalGoal == bottomRight){
				if (wallBetween(tank.getCenter(), bottomLeft) != null){
					return topRight;
				}
				else{
					return bottomLeft;
				}
				
			}
			else{
				if (wallBetween(tank.getCenter(), bottomRight) != null){
					return topLeft;
				}
				else{
					return bottomRight;
				}
			}
			
		
//		}
//		else{
//			double low = topLeftCorner;
//			
//			for (double d : pointList){
//				if (d < low){
//					low = d;
//				}
//			}
//			if (low == topLeftCorner){
//				return topLeft;
//			}
//			else if (low == topRightCorner){
//				return topRight;
//			}
//			else if (low == bottomLeftCorner){
//				return bottomLeft;
//			}
//			else{
//				return bottomRight;
//			}
//		}
		
			

	}
	
	private boolean inBounds(Point p){
		for (Wall w : field.walls){
			if (p.x > w.getX() && p.x < (w.getX() + w.getWidth()) &&
					p.y > w.getY() && p.y < (w.getY() + w.getHeight())){
				return false;
			}
		}
		if (p.x < 0 || p.y < 0 || p.x > field.width || p.y > field.height){
			return false;
		}
		else{
			return true;
		}
	}
	
	private boolean validElementLocation(Point p){
		if (!inBounds(p)){
			return false;
		}
		for (Bush b : field.bushes){
			if (p.x > b.getX() && p.x < (b.getX() + b.getWidth()) &&
					p.y > b.getY() && p.y < (b.getY() + b.getHeight())){
				return false;
			}
		}
		for (Tank t : field.tanks){
			if (p.x > t.location.x && p.x < (t.location.x + t.width) &&
					p.y > t.location.y && p.y < (t.location.y + t.height)){
				return false;
			}
		}
		for (Element e : elements){
			if (p.x > e.getLocation().x && p.x < (e.getLocation().x + e.getWidth()) &&
					p.y > e.getLocation().y && p.y < (e.getLocation().y + e.getWidth())){
				return false;
			}
		}
	
		return true;
	}
	
	private boolean inBounds(Tank t){
		int centerX = t.location.x + t.width/2;
		int centerY = t.location.y + t.height/2;
		double modifier = 1.7;
		
		Point topRight = new Point((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()+45)) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()+45)) * t.height/modifier));
		Point topLeft = new Point((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()+135)) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()+135)) * t.height/modifier));
		Point bottomLeft = new Point((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()-135)) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()-135)) * t.height/modifier));
		Point bottomRight = new Point((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()-45)) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()-45)) * t.height/modifier));
		
		modifier = 2.35;
		
		Point front = new Point((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle())) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle())) * t.height/modifier));
		Point left = new Point((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()+90)) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()+90)) * t.height/modifier));
		Point back = new Point((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()+180)) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()+180)) * t.height/modifier));
		Point right = new Point((int)(centerX + Math.cos(Math.toRadians(t.getMathmaticalAngle()+270)) * t.width/modifier), (int)(centerY - Math.sin(Math.toRadians(t.getMathmaticalAngle()+270)) * t.height/modifier));
		
		if (!inBounds(topLeft) || !inBounds(topRight) || !inBounds(bottomLeft) || !inBounds(bottomRight) || !inBounds(front) || !inBounds(left) || !inBounds(back) || !inBounds(right)){
			return false;
		}
		else{
			return true;
		}
	}
	
	private boolean oneTeam(){
		
		boolean blue = false;
		boolean red = false;
		int solo = 0;
		for (Tank t : field.tanks){
			if (t.side == Tank.team.SOLO){
				solo ++;
			}
			else if (t.side == Tank.team.BLUE){
				blue = true;
			}
			else if (t.side == Tank.team.RED){
				red = true;
			}
		}
		if (solo > 1){
			return false;
		}
		else if ((blue && red) || (red && solo > 0) || (blue && solo > 0)){
			return false;
		}
		else{
			return true;
		}
	}
	
	private int distance (Tank a, Tank b){
		//find centers;
		int xDif = (int) Math.pow(Math.abs(a.location.x + a.width/2 - (b.location.x + b.width/2)),2);
		int yDif = (int) Math.pow(Math.abs(a.location.y + a.height/2 - (b.location.y + b.height/2)), 2);
		int distance = (int) Math.sqrt(xDif+yDif);
		
		return distance;
	}
	
	private int distance (Point a, Point b){
		int xDif = (int) Math.pow(Math.abs(a.x - b.x),2);
		int yDif = (int) Math.pow(Math.abs(a.y - b.y),2);
		int distance = (int) Math.sqrt(xDif+yDif);
		
		return distance;
	}
	
	
	private void fieldUtilitySetup(ArrayList<Point> locations){
		for (int i = 0; i < selectionPanels.size(); i ++){
			System.out.println("Added tank number " + i);
			SelectionPanel p = selectionPanels.get(i);
			if (i == 0){
				command c = Input.command.WASD_Q;
				if (p.isAI()){
					c = Input.command.AI;
				}
				if (p.getSelectedType() == Tank.ROVER){
					field.addTank(Tank.ROVER, p.getTeam(), c, locations.get(i));
				}
				else if (p.getSelectedType() == Tank.SNIPER){
					field.addTank(Tank.SNIPER, p.getTeam(), c, locations.get(i));
				}
				else if (p.getSelectedType() == Tank.GRENADIER){
					field.addTank(Tank.GRENADIER, p.getTeam(), c, locations.get(i));
				}
			}
			else if (i == 1){
				command c = Input.command.ARROW_P;
				if (p.isAI()){
					c = Input.command.AI;
				}
				if (p.getSelectedType() == Tank.ROVER){
					field.addTank(Tank.ROVER, p.getTeam(), c, locations.get(i));
				}
				else if (p.getSelectedType() == Tank.SNIPER){
					field.addTank(Tank.SNIPER, p.getTeam(), c, locations.get(i));
				}
				else if (p.getSelectedType() == Tank.GRENADIER){
					field.addTank(Tank.GRENADIER, p.getTeam(), c, locations.get(i));
				}
			}
			else if (i == 2){
				command c = Input.command.KEYPAD;
				if (p.isAI()){
					c = Input.command.AI;
				}
				if (p.getSelectedType() == Tank.ROVER){
					field.addTank(Tank.ROVER, p.getTeam(), c, locations.get(i));
				}
				else if (p.getSelectedType() == Tank.SNIPER){
					field.addTank(Tank.SNIPER, p.getTeam(), c, locations.get(i));
				}
				else if (p.getSelectedType() == Tank.GRENADIER){
					field.addTank(Tank.GRENADIER, p.getTeam(), c, locations.get(i));
				}
			}
		}
		state = State.PLAYING;
		stateSetUp = false;
		System.out.println("ready to play");
	}
	
	private void addToWeapons(Grenade g){
		tempWeapons.addAll(g.explode());
	}
	
	
}
