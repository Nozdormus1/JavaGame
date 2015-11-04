import java.awt.*;

public class Player {
	private int x;
	private int y;
	private int r;
	
	private int dx;
	private int dy;
	private int speed;
	private int health;
	
	private boolean firing;
	private boolean dead;
	private long firingTimer;
	private long firingDelay;
	
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	
	private Color color1;
	private Color color2;
	
	private double angleFromMouse;
	
	public Player(){
		x = FirstFrame.WIDTH / 2;
		y = FirstFrame.HEIGHT / 2;
		r = 10;
		
		dx =0;
		dy =0;
		speed = 5;
		
		color1 = Color.BLUE;
		color2 = Color.RED;
		
		firing = false;
		firingTimer = System.nanoTime();
		firingDelay = 200;
		
		health = 1;
		dead = false;
		
	}
	
	public void setLeft(boolean b){left = b;}
	public void setRight(boolean b){right = b;}
	public void setUp(boolean b){up = b;}
	public void setDown(boolean b){down = b;}
	
	public void setFiring(boolean b){ firing = b;}
	
	public void setAngleFromMouse(double a){ angleFromMouse = a;}
	
	public void update(){
		if(left){
			dx = -speed;
		}
		if(right){
			dx = speed;
		}
		if(up){
			dy = -speed;
		}
		if(down){
			dy = speed;
		}
		x += dx;
		y += dy;
		if(x < r+5) x = r+5;
		if(y < r+26) y = r+26;
		if(x > FirstFrame.WIDTH - (r+5)) x = FirstFrame.WIDTH - (r+5);
		if(y > FirstFrame.HEIGHT - (r+5)) y = FirstFrame.HEIGHT - (r+5);
		
		dx = 0;
		dy = 0;
		
		if(firing) {
			long elapsed = (System.nanoTime() - firingTimer) / 1000000;
			if(elapsed > firingDelay){
				FirstFrame.fireballs.add(new Fireball(angleFromMouse, x, y));
				firingTimer = System.nanoTime();
			}
		}
	}
	
	public boolean isDead(){
		return dead;
	}
	
	public void hit(){
		health--;
		if(health <= 0 ){
			dead = true;
		}
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getR(){
		return r;
	}
	
	public void Draw(Graphics2D g){
		g.setColor(color1);
		g.fillOval(x - r, y - r, 2*r, 2*r);
		
		g.setStroke(new BasicStroke(3));
		g.setColor(color1.darker());
		g.drawOval(x - r, y - r, 2*r, 2*r);
		g.setStroke(new BasicStroke(1));
	}
}
