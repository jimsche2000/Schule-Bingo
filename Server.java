
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements ActionListener{

	ServerSocket server;
	static ArrayList<PrintWriter> list_clientWriter;
	static ArrayList<Socket> list_clients;
	//Server-window
	private JFrame serverFrame;
	private JPanel serverPanel;
	private JPanel contentPane;
	private JTextField textField;
	private JLabel maxUserLabel;
	private JTextField maxUserTextField;
	private JButton hostServer;
	private static boolean shouldRun = false;
	private static String name;
	private static String maxUser;
	private static int nowUser;
	final int LEVEL_ERROR = 1;
	final int LEVEL_NORMAL = 0;
	
	//Broadcast-Answer-System
	
//	private String name = System.getProperty("user.name");
	
	public static void main(String[] args) {
//		Window w = new Window();
//		w.setVisible(true);
		Server s = new Server();
			
			
			while(true) {
				System.out.println();
				if(shouldRun) {
//					System.out.println("shouldRun!");
					
					Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
					discoveryThread.start();
					
					if (s.runServer()) {
//						System.out.println("gcg");
						s.listenToClients();
					}
					
				}	
			}
	}
	
	
	public Server() {
		
		createGUI();
		
		
	}

	public void createGUI() {
		serverFrame = new JFrame();
		serverFrame.setTitle("BINGO - Server");
		serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		serverFrame.setBounds(100, 100, 310, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		serverFrame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("BINGO - Server");
		lblNewLabel.setFont(new Font("Source Sans Pro Semibold", Font.BOLD, 40));
		lblNewLabel.setBounds(25, 0, 300, 49);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Server-Name");
		lblNewLabel_1.setFont(new Font("Source Sans Pro Semibold", Font.BOLD, 25));
		lblNewLabel_1.setBounds(44, 70, 213, 31);
		contentPane.add(lblNewLabel_1);
		
		textField = new JTextField(System.getProperty("user.name"));
		textField.setBounds(44, 107, 213, 25);
		contentPane.add(textField);
		textField.setColumns(10);
		
		maxUserLabel = new JLabel("Maximale Anzahl der User");
		maxUserLabel.setBounds(44, 135, 213, 31);
		contentPane.add(maxUserLabel);
		maxUserTextField = new JTextField("100");
		maxUserTextField.setBounds(44, 165, 213, 25);
		contentPane.add(maxUserTextField);
		
		hostServer = new JButton("Server erstellen");
		hostServer.setBounds(44, 215, 213, 31);
		hostServer.addActionListener(this);
		hostServer.setActionCommand("HOST_SERVER");
		contentPane.add(hostServer);
		
		serverFrame.setResizable(false);
		serverFrame.setVisible(true);
	}
	
	public class ClientHandler implements Runnable {

		Socket client;
		BufferedReader reader;

		public ClientHandler(Socket client) {
			try {
				

				this.client = client;
				reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			if(!client.isClosed()) {
				String nachricht;

				try {
					while ((nachricht = reader.readLine()) != null) {
						// System.out.println("Nachricht: \""+nachricht+"\" test");
						int pos = nachricht.indexOf(":");
						String name = nachricht.substring(0, pos);
						nachricht = nachricht.substring(pos + 1, nachricht.length());
						// System.out.println("NAME: \""+name+"\" MSG: \""+nachricht+"\"");
						String sendMSG = name + ": " + nachricht;
						appendTextToConsole(sendMSG, LEVEL_NORMAL);
						sendToAllClients(sendMSG);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				
				
				//Client geschlossen
			}

		}
	}
	
	
//	public class HeartBeat implements Runnable {
//			/*
//			 * "Ansonsten kannst du einen Thread erstellen,
//			 * der in bestimmten Abständen isClosed() vom Socket aufruft."
//			 * 
//			 * 
//			 * 
//			 */
//		public HeartBeat() {
//			
//		}		
//		
//		@Override
//		public void run() {
//			
////			lis
//		}	
//	}

	public void listenToClients() {
		while (true) {
			try {
				nowUser = list_clientWriter.size();
				Socket client = server.accept();

				PrintWriter writer = new PrintWriter(client.getOutputStream());
				list_clientWriter.add(writer);
				
				Thread clientThread = new Thread(new ClientHandler(client));
				clientThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean runServer() {
		try {
			// InetAddress addr = InetAddress.getByName("172.16.144.194");
			// server = new ServerSocket(5556, 0, addr);
			server = new ServerSocket(5555);
			appendTextToConsole("Server wurde gestartet!", LEVEL_ERROR);

			list_clientWriter = new ArrayList<PrintWriter>();
			list_clients = new ArrayList<Socket>();
			return true;
		} catch (IOException e) {
			appendTextToConsole("Server konnte nicht gestartet werden!", LEVEL_ERROR);
			e.printStackTrace();
			return false;
		}
	}

	public void appendTextToConsole(String message, int level) {
		if (level == LEVEL_ERROR) {
			System.err.println(message + "\n");
		} else {
			System.out.println(message + "\n");
		}
	}

	public void sendToAllClients(String message) {
		Iterator it = list_clientWriter.iterator();

		while (it.hasNext()) {
			PrintWriter writer = (PrintWriter) it.next();
			writer.println(message);
//			System.out.println("SERVER_WRITE_MSG: \""+message+"\"");
			writer.flush();
		}
	}

	public static class DiscoveryThread implements Runnable {
		DatagramSocket socket;

		@Override

		public void run() {

			try {

				// Keep a socket open to listen to all the UDP trafic that is destined for this
				// port

				socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));

				socket.setBroadcast(true);

				while (true) {

					System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

					// Receive a packet

					byte[] recvBuf = new byte[15000];

					DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

					socket.receive(packet);

					// Packet received

					System.out.println(getClass().getName() + ">>>Discovery packet received from: "
							+ packet.getAddress().getHostAddress());

					System.out.println(
							getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

					// See if the packet holds the right command (message)

					String message = new String(packet.getData()).trim();

					if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {

						String text = "DISCOVER_FUIFSERVER_RESPONSE{NAME="+name+"}{USER_COUNT="+nowUser+"/"+maxUser+"}";
						byte[] sendData = text.getBytes("UTF-8");

						// Send a response

						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(),
								packet.getPort());

						socket.send(sendPacket);

						System.out.println(getClass().getName() + ">>>Sent packet to: "
								+ sendPacket.getAddress().getHostAddress());

					}

				}

			} catch (IOException ex) {

				Logger.getLogger(DiscoveryThread.class.getName()).log(Level.SEVERE, null, ex);

			}

		}

		public static DiscoveryThread getInstance() {

			return DiscoveryThreadHolder.INSTANCE;

		}

		private static class DiscoveryThreadHolder {

			private static final DiscoveryThread INSTANCE = new DiscoveryThread();

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("HOST_SERVER")) {
			name = textField.getText();
			maxUser = maxUserTextField.getText();
			shouldRun = true;
			hostServer.setEnabled(false);
		}
	}
}