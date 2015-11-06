import java.io.IOException;
import java.net.Socket;

public class ServerAccept implements Runnable {
	
	Socket socket;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				socket = FirstFrame.serverSocket.accept();
				FirstFrame.sockets.add(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
