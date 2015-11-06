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
	private JTextField textField_2;
	private Thread thread = new Thread(this, "FirstFrame");
	
	private String ip = "localhost";
	private int port = 22222;
	
	private Socket socket;
	private boolean server = false;
	
	private BufferedImage image;
	private Graphics2D g;
	
	public static ServerSocket serverSocket;
	
	public static int WIDTH = 736;
	public static int HEIGHT = 618;
	public static int mouseX;
	public static int mouseY;
	
	public static Player player;
	public static ArrayList<Fireball> fireballs;
	public static ArrayList<Socket> sockets;
	private ClientInfo clientInfoSend;
	private ClientInfo clientInfoGet;
	private ArrayList<ClientInfo> clientInfosSend;
	private ArrayList<ClientInfo> clientInfosGet;
	private ArrayList<ClientInfo> clientInfosMail;
	
	private int FPS = 30;
	private double averageFPS;
	
	private int error = 0;
	private double deg;
	private double check;
	private int multiShotCheck;
	private long firingTimer = System.nanoTime();
	private long firingDelay = 200;
	private long multiShotTimer = System.nanoTime();
	private long multiShotDelay = 5000;
	private long deathTimer;
	private long deathDelay = 4000;
	private int score;
	private int escore;
	private int radius;
	private String nickname;


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
		textField.setBounds(199, 100, 357, 60);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setHorizontalAlignment(SwingConstants.CENTER);
		textField_1.setFont(new Font("Tahoma", Font.BOLD, 30));
		textField_1.setBounds(199, 214, 357, 60);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("PLAY!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ip = textField.getText();
				nickname = textField_2.getText();
				if(nickname == null){
					JOptionPane.showMessageDialog(null, "enter a nickname"); 
				}
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
		btnNewButton.setBounds(199, 430, 357, 70);
		contentPane.add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Enter IP adress");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 36));
		lblNewLabel.setBounds(199, 41, 357, 48);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Enter Port number");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 29));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(209, 171, 348, 32);
		contentPane.add(lblNewLabel_1);
		
		textField_2 = new JTextField();
		textField_2.setHorizontalAlignment(SwingConstants.CENTER);
		textField_2.setFont(new Font("Tahoma", Font.BOLD, 30));
		textField_2.setBounds(199, 332, 357, 60);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Enter your nickname");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 29));
		lblNewLabel_2.setBounds(199, 285, 357, 36);
		contentPane.add(lblNewLabel_2);
		
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
		lblNewLabel_3.setBounds(199, 214, 357, 60);
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
		clientInfoGet = new ClientInfo();
		clientInfoSend = new ClientInfo();
		
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
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		
		while(true){
			startTime = System.nanoTime();
			
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, WIDTH, HEIGHT);
			
			if(server == true){
				gamePreUpdateServer();
				for(int j = 0; j < sockets.size(); j++){
					gameCollectingInfoServer(sockets.get(j));
				}
				clientInfosSend.add(clientInfoSend);
				for(int j = 0; j < clientInfosSend.size()-1; j++){
					clientInfosMail = new ArrayList<>();
					for(int i = 0; i < clientInfosSend.size(); i++){
						if(i!=j){
							clientInfosMail.add(clientInfosSend.get(i));
						}
					}
					try {
						sendMessageServer(sockets.get(j), clientInfosMail);
						} catch (IOException e){
							e.printStackTrace();
							error++;
							if(error >= 20){
								try {
									sockets.get(j).close();
									sockets.remove(j);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
				}
			} else{
				gamePreUpdateClient(socket);
			}
			gameUpdatePart1();
			for(int i = 0; i < fireballs.size(); i++ ){
				boolean remove = fireballs.get(i).update();
				if(remove){
					fireballs.remove(i);
					i--;
				}
			}
			gameUpdatePart2();
			gameRender();
			
			if(clientInfoSend.isDead()) {
				long elapsedDeath = (System.nanoTime() - deathTimer) / 1000000;
				g.setColor(Color.RED);
				g.setFont(new Font("default", Font.PLAIN, 11));
				g.drawString("Reviving in:" + elapsedDeath, 100, 140);
				if(elapsedDeath > deathDelay){
					player.setX((int)(Math.random() * (FirstFrame.WIDTH-10)) + 5);
					player.setY((int)(Math.random() * (FirstFrame.HEIGHT-30)) + 26);
					clientInfoSend.setDead(false);
					deathTimer = System.nanoTime();
				}
			}
			
			gameDrow();
			
			//if(error >= 20){
			//	JOptionPane.showMessageDialog(null, "Your opponent has left!");
			//	break;
			//}
			
			
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
	}
	
	private void gamePreUpdateServer(){
		clientInfosSend = new ArrayList<ClientInfo>();
		clientInfosGet = new ArrayList<ClientInfo>();
		player.update();
		clientInfoSend.setX(player.getX());
		clientInfoSend.setY(player.getY());
		clientInfoSend.setAngle(deg);
		clientInfoSend.setCheckShot(check);
		clientInfoSend.setTeleportDetector(player.getMultiDetector());
		clientInfoSend.setNickname(nickname);
		clientInfoSend.setMultiShotCheck(multiShotCheck);
	}
	private void gameCollectingInfoServer(Socket s){
		try {
			clientInfoGet = getMessage(s);
			} catch (IOException e){ 
				System.out.println("null"); 
			} catch (ClassNotFoundException e) {
				System.out.println("noclass");
			}
		
		clientInfosSend.add(clientInfoGet);
		clientInfosGet.add(clientInfoGet);
	}
	
	private void gamePreUpdateClient(Socket s){
		clientInfosGet = new ArrayList<ClientInfo>();
		player.update();
		clientInfoSend.setX(player.getX());
		clientInfoSend.setY(player.getY());
		clientInfoSend.setAngle(deg);
		clientInfoSend.setCheckShot(check);
		clientInfoSend.setTeleportDetector(player.getMultiDetector());
		clientInfoSend.setNickname(nickname);
		clientInfoSend.setMultiShotCheck(multiShotCheck);
		try {
			sendMessage(s, clientInfoSend);
			} catch (IOException e){
				e.printStackTrace();
				error++;
				if(error >= 20){
					JOptionPane.showMessageDialog(null, "Disconnected from server!");
					System.exit(0);
				}
			}
		try {
			clientInfosGet = getMessageClient(s);
			} catch (IOException e){ 
				System.out.println("null"); 
			} catch (ClassNotFoundException e) {
				System.out.println("noclass");
			}
		}
	
	private void gameUpdatePart1(){
		for(int j = 0; j < clientInfosGet.size(); j++ ){
			if((int) clientInfosGet.get(j).getCheckShot() == 1){
				long elapsed = (System.nanoTime() - firingTimer) / 1000000;
				if(elapsed > firingDelay){
					fireballs.add(new Fireball(clientInfosGet.get(j).getAngle(), (int) clientInfosGet.get(j).getX(), (int) clientInfosGet.get(j).getY(), Color.RED));
					firingTimer = System.nanoTime();
				}
			}
			if(clientInfosGet.get(j).getMultiShotCheck() == 1){
				long elapsedMultiShot = (System.nanoTime() - multiShotTimer) / 1000000;
				if(elapsedMultiShot > multiShotDelay){
					for(int i = 0; i < 20; i++){
						FirstFrame.fireballs.add(new Fireball(0+i*18, (int) clientInfosGet.get(j).getX(), (int) clientInfosGet.get(j).getY(), Color.RED));
						multiShotTimer = System.nanoTime();
					}
				}
			}
		}
	}
	private void gameUpdatePart2(){
		
		for(int j = 0; j < clientInfosGet.size(); j++ ){
			if(clientInfosGet.get(j).isDead() == false){
				for(int i = 0; i < fireballs.size(); i++ ){
					Fireball f = fireballs.get(i);
					double fx = f.getx();
					double fy = f.gety();
					double fr = f.getr();
			
					double ex = clientInfosGet.get(j).getX();
					double ey = clientInfosGet.get(j).getY();
					double er = radius;
			
					double mx = clientInfoSend.getX();
					double my = clientInfoSend.getY();
					double mr = radius;
			
					double dx = fx - ex;
					double dy = fy - ey;
					double dist = Math.sqrt(dx*dx + dy*dy);
			
					double dx2 = fx - mx;
					double dy2 = fy - my;
					double dist2 = Math.sqrt(dx2*dx2 + dy2*dy2);
			
					if(dist < fr + er){
						try{
							fireballs.remove(i);
						} catch(Exception e){System.out.println("already removed");}
						score++;
						clientInfosGet.get(j).setDead(true);
					}
					if(clientInfoSend.isDead() == false){
						if(dist2 < fr + mr){
							try{
								fireballs.remove(i);
							} catch(Exception e){System.out.println("already removed");}
							escore++;
							clientInfoSend.setDead(true);
							deathTimer = System.nanoTime();
						}
					}
				}
			}
		}
	}
	
	private void gameRender(){
		//g.setColor(Color.WHITE);
		//g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.setFont(new Font("default", Font.PLAIN, 11));
		g.drawString("FPS: " + averageFPS, 100, 100);
		g.drawString("Your score: kills:" +score + " / deaths: "+escore, 100, 120);
		
		if(clientInfoSend.isDead() == false){
			player.Draw(g, clientInfoSend.getNickname());
		}
		
		for(int j = 0; j < clientInfosGet.size(); j++ ){
			try {
				if(clientInfosGet.get(j).isDead() == false){
					if((int) clientInfosGet.get(j).getTeleportDetector() == 0){
						g.setColor(Color.RED);
					} else{
						g.setColor(Color.YELLOW);
					}
					g.fillOval((int) clientInfosGet.get(j).getX() - radius, (int) clientInfosGet.get(j).getY() - radius, 2*radius, 2*radius);
		
					g.setStroke(new BasicStroke(3));
					g.setColor(Color.RED.darker());
					g.drawOval((int) clientInfosGet.get(j).getX() - radius, (int) clientInfosGet.get(j).getY() - radius, 2*radius, 2*radius);
					g.setStroke(new BasicStroke(1));
					g.setColor(Color.BLACK);
					g.setFont(new Font("default", Font.BOLD, 16));
					g.drawString(clientInfosGet.get(j).getNickname(), (int) clientInfosGet.get(j).getX() - (int ) (clientInfosGet.get(j).getNickname().length()*7 / 1.5 ), (int) clientInfosGet.get(j).getY()-2*radius);
					g.setFont(new Font("default", Font.PLAIN, 11));
				}
			} catch (Exception e){
				System.out.println("null2"); 
			}
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
		if(clientInfoSend.isDead() == false){
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
		if(clientInfoSend.isDead() == false){
			if(keyCode == KeyEvent.VK_E){
				multiShotCheck = 1;
				player.setMultiShot(true);
			}
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
		if(keyCode == KeyEvent.VK_E){
			multiShotCheck = 0;
			player.setMultiShot(false);
		}
	}
	
	public static void sendMessage(Socket s, ClientInfo myMessageArray) throws IOException{
		OutputStream os = s.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os));
		oos.writeObject(myMessageArray);
		oos.flush();
	}
	
	public static void sendMessageServer(Socket s, ArrayList<ClientInfo> myMessageArray) throws IOException{
		OutputStream os = s.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os));
		oos.writeObject(myMessageArray);
		oos.flush();
	}
	
	public static ClientInfo getMessage(Socket s) throws IOException, ClassNotFoundException{
		InputStream is = s.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
		ClientInfo myMessageArray = (ClientInfo) ois.readObject();
		return myMessageArray;
	}
	
	public static ArrayList<ClientInfo> getMessageClient(Socket s) throws IOException, ClassNotFoundException{
		InputStream is = s.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(is));
		ArrayList<ClientInfo> myMessageArray = (ArrayList<ClientInfo>) ois.readObject();
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
