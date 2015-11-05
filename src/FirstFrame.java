import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.image.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.awt.event.*;

public class FirstFrame extends JFrame implements Runnable, KeyListener, MouseListener {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private Thread thread = new Thread(this, "FirstFrame");
	
	private String ip = "localhost";
	private int port = 22222;
	
	private Socket socket;
	private boolean server = false;
	
	private BufferedImage image;
	private Graphics2D g;
	
	public static ServerSocket serverSocket;
	
	public static int WIDTH = 536;
	public static int HEIGHT = 418;
	public static int mouseX;
	public static int mouseY;
	
	public static Player player;
	public static ArrayList<Fireball> fireballs;
	public static ArrayList<Socket> sockets;
	
	private int FPS = 30;
	private double averageFPS;
	
	private double[] enemyarray = new double[6];
	private double[] myarray = new double[6];
	private int error = 0;
	private double deg;
	private double check;
	private long firingTimer = System.nanoTime();
	private long firingDelay = 200;
	private int score;
	private int escore;
	private int radius;


	/**
	 * Create the frame.
	 */
	public FirstFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, WIDTH, HEIGHT);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setFont(new Font("Tahoma", Font.BOLD, 30));
		textField.setBounds(81, 69, 357, 60);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setHorizontalAlignment(SwingConstants.CENTER);
		textField_1.setFont(new Font("Tahoma", Font.BOLD, 30));
		textField_1.setBounds(81, 183, 357, 60);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("PLAY!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ip = textField.getText();
				try{ 
					port = Integer.parseInt(textField_1.getText());
				} catch(Exception e){
					JOptionPane.showMessageDialog(null, "enter a number beetween 1 and 65535"); 
					port = 0;
					}
				if (port < 1 || port > 65535) {
					JOptionPane.showMessageDialog(null, "enter a number beetween 1 and 65535");
				} else {
					if (!connect()) initializeServer();
					thread.start();
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 33));
		btnNewButton.setBounds(81, 283, 357, 70);
		contentPane.add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Enter IP adress");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 36));
		lblNewLabel.setBounds(81, 11, 357, 48);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Enter Port number");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 29));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(91, 140, 348, 32);
		contentPane.add(lblNewLabel_1);
		
		this.setFocusable(true);
		this.requestFocus();
		addMouseListener(this);
		addKeyListener(this);
	}
	
	public void run() {
		contentPane.removeAll();
		JLabel lblNewLabel_3 = new JLabel("Waiting for Player 2 ...");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 29));
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(91, 140, 348, 32);
		contentPane.add(lblNewLabel_3);
		contentPane.repaint();

		long startTime;
		long URDTimeMillis;
		long waitTime;
		long totalTime = 0;
		
		int frameCount = 0;
		int maxFrameCount = 30;
		
		long targetTime = 1000 / FPS;
		
		player = new Player();
		fireballs = new ArrayList<Fireball>();
		sockets = new ArrayList<Socket>();
		
		radius = player.getR();
		
			
		if(server == true){
			try {
				socket = serverSocket.accept();
				sockets.add(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			(new Thread(new ServerAccept())).start();
		}
		
		contentPane.removeAll();
		contentPane.repaint();
		
		image = new BufferedImage(535, 417, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		while(true){
			//for(int j = 0; j < sockets.size(); j++){
			startTime = System.nanoTime();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, WIDTH, HEIGHT);
			if(server == true){
			for(int j = 0; j < sockets.size(); j++){
				gameUpdate(sockets.get(j));
				gameRender();
			}
			} else{
					gameUpdate(socket);
					gameRender();
			}
			for(int i = 0; i < fireballs.size(); i++ ){
				fireballs.get(i).draw(g);
				}
			gameDrow();
			if(error >= 20){
				JOptionPane.showMessageDialog(null, "Your opponent has left!");
				break;
			}
			
			URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
			
			waitTime = targetTime - URDTimeMillis;
			
			try {
				Thread.sleep(waitTime);
				} catch (Exception e){
			}
			
			totalTime += System.nanoTime() - startTime;
			frameCount++;
			if(frameCount == maxFrameCount){
				averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000 );
				frameCount = 0;
				totalTime = 0;
			}
		}
		//}
	}
	
	private void gameUpdate(Socket s){
		player.update();
		myarray[0] = player.getX();
		myarray[1] = player.getY();
		myarray[2] = deg;
		myarray[3] = check;
		myarray[4] = player.getMultiDetector();
		try {
			sendMessage(s, myarray);
			} catch (IOException e){
				System.out.println("null2");
				error++;
			}
		try {
			enemyarray = getMessage(s);
			} catch (IOException e){ 
				System.out.println("null"); 
			} catch (ClassNotFoundException e) {
				System.out.println("noclass");
			}
		if((int) enemyarray[3] == 1){
			long elapsed = (System.nanoTime() - firingTimer) / 1000000;
			if(elapsed > firingDelay){
				fireballs.add(new Fireball(enemyarray[2], (int) enemyarray[0], (int) enemyarray[1], Color.RED));
				firingTimer = System.nanoTime();
			}
		}
		for(int i = 0; i < fireballs.size(); i++ ){
			boolean remove = fireballs.get(i).update();
			if(remove){
				fireballs.remove(i);
				i--;
			}
		}
		for(int i = 0; i < fireballs.size(); i++ ){
			Fireball f = fireballs.get(i);
			double fx = f.getx();
			double fy = f.gety();
			double fr = f.getr();
			
			double ex = enemyarray[0];
			double ey = enemyarray[1];
			double er = radius;
			
			double mx = myarray[0];
			double my = myarray[1];
			double mr = radius;
			
			double dx = fx - ex;
			double dy = fy - ey;
			double dist = Math.sqrt(dx*dx + dy*dy);
			
			double dx2 = fx - mx;
			double dy2 = fy - my;
			double dist2 = Math.sqrt(dx2*dx2 + dy2*dy2);
			
			if(dist < fr + er){
				fireballs.remove(i);
				score++;
			}
			if(dist2 < fr + mr){
				fireballs.remove(i);
				escore++;
			}
		}
		}
	
	private void gameRender(){
		//g.setColor(Color.WHITE);
		//g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("FPS: " + averageFPS, 100, 100);
		g.drawString("Your score: " + score, 100, 120);
		g.drawString("Enemy score: " + escore, 100, 140);
		
		player.Draw(g);
		try {
			if((int) enemyarray[4] == 0){
				g.setColor(Color.RED);
			} else{
				g.setColor(Color.YELLOW);
			}
			g.fillOval((int) enemyarray[0] - radius, (int) enemyarray[1] - radius, 2*radius, 2*radius);
		
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.RED.darker());
			g.drawOval((int) enemyarray[0] - radius, (int) enemyarray[1] - radius, 2*radius, 2*radius);
			g.setStroke(new BasicStroke(1));
			} catch (Exception e){
				System.out.println("null2"); 
			}
		for(int i = 0; i < fireballs.size(); i++ ){
			fireballs.get(i).draw(g);
		}
	}
	
	private void gameDrow(){
		
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		
		
	}
	public void mousePressed(MouseEvent key){
		int keyCode = key.getButton();
		if(keyCode == MouseEvent.BUTTON1){
			int x = (int) key.getPoint().getX();
			int y = (int) key.getPoint().getY();
			int dx = x - player.getX();
			int dy = y - player.getY();
			double rad = Math.atan2(dy, dx);
			deg = Math.toDegrees(rad);
			if (deg < 0){ deg += 360; }
			player.setAngleFromMouse(deg);
			check = 1;
			player.setFiring(true);
		}
		if(keyCode == MouseEvent.BUTTON3){
			mouseX = (int) key.getPoint().getX();
			mouseY = (int) key.getPoint().getY();
			player.setTeleporting(true);
		}
	}
	public void mouseReleased(MouseEvent key){
		int keyCode = key.getButton();
		if(keyCode == MouseEvent.BUTTON1){
			check = 0;
			player.setFiring(false);
		}
		if(keyCode == MouseEvent.BUTTON3){
			player.setTeleporting(false);
		}
	}
	public void keyTyped(KeyEvent key){}
	public void keyPressed(KeyEvent key){
		int keyCode = key.getKeyCode();
		if(keyCode == KeyEvent.VK_A){
			player.setLeft(true);
		}
		if(keyCode == KeyEvent.VK_D){
			player.setRight(true);
		}
		if(keyCode == KeyEvent.VK_W){
			player.setUp(true);
		}
		if(keyCode == KeyEvent.VK_S){
			player.setDown(true);
		}
	}
	public void keyReleased(KeyEvent key){
		int keyCode = key.getKeyCode();
		if(keyCode == KeyEvent.VK_A){
			player.setLeft(false);
		}
		if(keyCode == KeyEvent.VK_D){
			player.setRight(false);
		}
		if(keyCode == KeyEvent.VK_W){
			player.setUp(false);
		}
		if(keyCode == KeyEvent.VK_S){
			player.setDown(false);
		}
	}
	
	public static void sendMessage(Socket s, double[] myMessageArray) throws IOException{
		OutputStream os = s.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os));
		oos.writeObject(myMessageArray);
		oos.flush();
	}
	
	public static double[] getMessage(Socket s) throws IOException, ClassNotFoundException{
		InputStream is = s.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
		double[] myMessageArray = (double[]) ois.readObject();
		return myMessageArray;
	}
	
	
	private boolean connect() {
		try {
			socket = new Socket(ip, port);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to connect to the address: " + ip + ":" + port + " | Starting a server");
			return false;
		}
		JOptionPane.showMessageDialog(null, "Successfully connected to the server.");
		return true;
	}
	
	private void initializeServer() {
		try {
			serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
		} catch (Exception e) {
			e.printStackTrace();
		}
		server = true;
	}

	@Override
	public void mouseClicked(MouseEvent key) {}

	@Override
	public void mouseEntered(MouseEvent key) {}

	@Override
	public void mouseExited(MouseEvent key) {}
}
