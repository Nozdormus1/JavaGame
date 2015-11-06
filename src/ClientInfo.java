import java.io.Serializable;

public class ClientInfo implements Serializable {
	private double x = 0;
	private double y = 0;
	private double angle = 0;
	private double checkShot = 0;
	private double teleportDetector = 0;
	private String nickname;
	private int multiShotCheck;
	private boolean dead;
	private int kills;
	private int deaths;
	
	public double getTeleportDetector() {
		return teleportDetector;
	}
	public void setTeleportDetector(double teleportDetector) {
		this.teleportDetector = teleportDetector;
	}
	public double getCheckShot() {
		return checkShot;
	}
	public void setCheckShot(double checkShot) {
		this.checkShot = checkShot;
	}
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getMultiShotCheck() {
		return multiShotCheck;
	}
	public void setMultiShotCheck(int multiShotCheck) {
		this.multiShotCheck = multiShotCheck;
	}
	public boolean isDead() {
		return dead;
	}
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	public int getKills() {
		return kills;
	}
	public void setKills(int kills) {
		this.kills = kills;
	}
	public int getDeaths() {
		return deaths;
	}
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	
}
