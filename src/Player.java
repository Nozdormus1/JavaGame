import java.awt.*;

public class Player {
	private int x;
	private int y;
	private int r;
	
	private int dx;
	private int dy;
	private int speed;
	private int health;
	private int multiDetector;
	
	private boolean firing;
	private boolean teleporting;
	private boolean dead;
	private boolean cdDetector;
	private boolean multiShot;
	private long firingTimer;
	private long firingDelay;
	private long multiShotTimer;
	private long multiShotDelay;
	private long teleportingTimer;
	private long teleportingDelay;
	private long elapsedTele;
	
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	
	private Color color1;
	private Color color2;
	
	private double angleFromMouse;
	
	public Player(){
		x = (int)(Math.random() * (FirstFrame.WIDTH-10)) + 5; //FirstFrame.WIDTH / 2; 
		y = (int)(Math.random() * (FirstFrame.HEIGHT-30)) + 26; //FirstFrame.HEIGHT / 2;
		r = 10;
		
		dx =0;
		dy =0;
		speed = 5;
		multiDetector = 0;
		
		color1 = Color.BLUE;
		color2 = Color.YELLOW;
		firing = false;
		teleporting = false;
		multiShot = false;
		firingDelay = 200;
		teleportingDelay = 10000;
		multiShotDelay = 5000;
		
		health = 1;
		dead = false;
		
	}
	
	public void setLeft(boolean b){left = b;}
	public void setRight(boolean b){right = b;}
	public void setUp(boolean b){up = b;}
	public void setDown(boolean b){down = b;}
	
	public void setFiring(boolean b){ firing = b;}
	public void setTeleporting(boolean b){ teleporting = b;}
	public void setMultiShot(boolean b){multiShot = b;}
	
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
				FirstFrame.myFireballs.add(new Fireball(angleFromMouse, x, y, Color.BLUE));
				firingTimer = System.nanoTime();
			}
		}
		if(multiShot) {
			long elapsedMultiShot = (System.nanoTime() - multiShotTimer) / 1000000;
			if(elapsedMultiShot > multiShotDelay){
				for(int i = 0; i < 20; i++){
					FirstFrame.myFireballs.add(new Fireball(0+i*18, x, y, Color.BLUE));
					multiShotTimer = System.nanoTime();
				}
			}
		}
		if(teleporting){
			elapsedTele = (System.nanoTime() - teleportingTimer) / 1000000;
			if(elapsedTele > teleportingDelay){
				teleport(FirstFrame.mouseX, FirstFrame.mouseY);
				teleportingTimer = System.nanoTime();
				cdDetector = true;
				multiDetector = 1;
			}
		}
		elapsedTele = (System.nanoTime() - teleportingTimer) / 1000000;
		if(elapsedTele > teleportingDelay){
			cdDetector = false;
			multiDetector = 0;
		}
	}
	
	public boolean isDead(){
		return dead;
	}
	
	public void teleport(int x, int y){
		this.x = x;
		this.y = y;
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
	
	public void setX(int x){
		this.x = x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public int getR(){
		return r;
	}
	
	public int getMultiDetector(){
		return multiDetector;
	}
	
	public void Draw(Graphics2D g, String nickname){
		if(!cdDetector){
			g.setColor(color1);
		} else{
			g.setColor(color2);
		}
		g.fillOval(x - r, y - r, 2*r, 2*r);
		
		g.setStroke(new BasicStroke(3));
		g.setColor(color1.darker());
		g.drawOval(x - r, y - r, 2*r, 2*r);
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.BLACK);
		g.setFont(new Font("default", Font.BOLD, 16));
		g.drawString(nickname, x - (int ) (nickname.length()*7 / 1.5 ), y-2*r);
		g.setFont(new Font("default", Font.PLAIN, 16));
		
	}
}
