// Package muss natürlich angepasst werden
//package tutcubede.chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.synth.SynthSpinnerUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

//import Client.Server;

public class Client implements ActionListener, WindowListener{
	//Variables
	private String name;
	
	//chat-window
	private JFrame chatFrame;
	private JPanel chatPanel;
	private JTextArea textArea_Messages;
	private JTextField textField_ClientMessage;
	private JButton button_SendMessage;
//	private JTextField textField_Username;
	private JScrollPane scrollPane_Messages;
	
	//client-window
	private JFrame clientFrame;
	private JPanel clientPanel;
	private JPanel contentPane;
	private JTextField textField;
	private JTable table;
	private ArrayList<Client.Server> serverlist;
	//SearchForServers
	private DatagramSocket c;
	
	
	
	//connect to server
	private JButton[] connectButtons;
	
	//actual timestamp to wait 0.5sec
	long last_timestamp = System.currentTimeMillis()-500;
	private Server connect_Server;
	//others
	Socket client;
	PrintWriter writer;
	BufferedReader reader;
	
	public Client() {

//		Thread t = new Thread(new MessagesFromServerListener());
//		t.start();

		
//		if(!connectToServer()) {
			// Connect-Label anzeigen ob verbunden oder nicht...
//		}
		serverlist = new ArrayList<Client.Server>();
	}
	
	public static void main(String[] args) {
		Client c = new Client();	
//		c.createChat();
		c.createGUI();
		c.searchForServers();
	}
	
	public void createChat() {
		chatFrame = new JFrame("Client[Chat]");
		chatFrame.setSize(800, 600);
		
		// Panel erzeugen, welches alle anderen Inhalte enthält
		chatPanel = new JPanel();
		
		textArea_Messages = new JTextArea();
		textArea_Messages.setEditable(false);
		
		textField_ClientMessage = new JTextField(38);
		textField_ClientMessage.addKeyListener(new SendPressEnterListener());
		
		button_SendMessage = new JButton("Senden");
		button_SendMessage.addActionListener(new SendButtonListener());
		
		name = textField.getText();
//		textField_Username = new JTextField(10);
		
		// Scrollbalken zur textArea hinzufügen
		scrollPane_Messages = new JScrollPane(textArea_Messages);
		scrollPane_Messages.setPreferredSize(new Dimension(700, 500));
		scrollPane_Messages.setMinimumSize(new Dimension(700, 500));
		scrollPane_Messages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_Messages.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);		


		
		if(!connectToServer()) {
			// Connect-Label anzeigen ob verbunden oder nicht...
		}
		
		Thread t = new Thread(new MessagesFromServerListener());
		t.start();
		chatPanel.add(scrollPane_Messages);
//		chatPanel.add(textField_Username);
		chatPanel.add(textField_ClientMessage);
		chatPanel.add(button_SendMessage);
		
		// Panel zum ContentPane (Inhaltsbereich) hinzufügen
		chatFrame.getContentPane().add(BorderLayout.CENTER, chatPanel);
		
		chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chatFrame.setResizable(false);
		chatFrame.setVisible(true);
		chatFrame.addWindowListener(this);
		clientFrame.setVisible(false);
	}
	
	public void createGUI() {
		
		clientFrame = new JFrame();
		
		clientFrame.setTitle("BINGO - Client");
		clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientFrame.setBounds(100, 100, 800, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		clientFrame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("BINGO - Client");
		lblNewLabel.setFont(new Font("Source Sans Pro Semibold", Font.BOLD, 40));
		lblNewLabel.setBounds(275, 0, 250, 49);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Name");
		lblNewLabel_1.setFont(new Font("Source Sans Pro Semibold", Font.BOLD, 25));
		lblNewLabel_1.setBounds(44, 65, 213, 31);
		contentPane.add(lblNewLabel_1);
		
		textField = new JTextField("Anonymous");
		textField.setBounds(44, 107, 213, 25);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblServerliste = new JLabel("Server-Liste");
		lblServerliste.setFont(new Font("Source Sans Pro Semibold", Font.BOLD, 25));
		lblServerliste.setBounds(335, 40, 150, 30);
		contentPane.add(lblServerliste);
		
//	       String[] columnNames = {"Server-Name",
//                   "IP-Address",
//                   "User-Count"};
		
		
//		table = new JTable(new Object[][] {}, columnNames);
//		table = new JTable(10, 3);
//		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		table.setBounds(275, 107, 238, 200);
		
		connectButtons = new JButton[12];
		//X: 
		int x = 525, y = 120, width = 80, height = 16;
		for(int i = 0; i < 12; i++) {
			JButton b = new JButton("connect");
			b.setBounds(x, y, width, height);
			b.addActionListener(this);
			b.setActionCommand("CONNECT_"+i);
			y += height;
			contentPane.add(b);
		}
		
		String column_names[]= {"Server-Name","IP-Address","User-Count"};
		table = new JTable(new DefaultTableModel(column_names,12));

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setBounds(275, 120, 250, 193);
		table.setEnabled(false);
	
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		TableColumnModel colModel=table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(80);
		colModel.getColumn(1).setPreferredWidth(100);    
		colModel.getColumn(2).setPreferredWidth(70);
		
		JTableHeader th = table.getTableHeader();
		th.setBounds(275, 100, 250, 20);
		contentPane.add(th);
		contentPane.add(table);
		
		JButton btnRefresh = new JButton("refresh");
		btnRefresh.setBounds(354, 75, 101, 25);
		btnRefresh.addActionListener(this);
		btnRefresh.setActionCommand("RELOAD_SERVER_LIST");
		
//		JButton connect = new JButton("connect");
//		connect.setBounds(400, 70, 100, 30);
//		connect.addActionListener(this);
//		connect.setActionCommand("CONNECT");
		
//		contentPane.add(connect);
		contentPane.add(btnRefresh);
		clientFrame.setResizable(false);
		clientFrame.setVisible(true);
	}
	
	
	public void reloadServerTableList() {
		//items
		
	  	//Abfragen ob ein Server fehlt
		
		//jetzige ServerListe vom JTable herausfinden herausfinden
		
//		ArrayList<Server> server_list_from_table = new ArrayList<Server>();
//		//Nachgucken ob einzelne einträge gelöscht werden müssen
//		
//		Dimension d = table.getSize();
//		for(int x = 0; x < d.getWidth(); x++) {
//			String IP, name, user_count;
//			
//			IP = (String) table.getModel().getValueAt(x, 0);
//			name = (String) table.getModel().getValueAt(x, 0);
//			user_count = (String) table.getModel().getValueAt(x, 0);
//			
//			Server newServer = new Server(IP, name, user_count);
//			server_list_from_table.add(newServer);
//			
//			System.out.println("Server_From_Table: "+newServer);
//		}

		
		
		
		
//	  	for(Client.Server server : serverlist) {
//	  		boolean found = false;
//	  		for(Client.Server server2 : serverlist) {
//
//	  			if(server.equals(server2)) {
//	  				found = true;
//	  			}
//	  			if(!found) {
//	  				serverlist.remove(server);
//	  			}
//	  		}
//	  	}
		
		
		for(int r = 0; r < table.getRowCount(); r++) {
			
			table.getModel().setValueAt("", r, 0);
			table.getModel().setValueAt("", r, 1);
			table.getModel().setValueAt("", r, 2);
		}
		
		for(int i = 0; i < serverlist.size(); i++) {
			//row = i; col = self-count
			Server temp = serverlist.get(i);

			table.getModel().setValueAt(temp.name, i, 0);
			table.getModel().setValueAt(temp.IP, i, 1);
			table.getModel().setValueAt(temp.user_count, i, 2);
		}
	}
	
	
	public void searchForServers() {
		
		
		
		// Find the server using UDP broadcast
		
		try {
		
		  //Open a random port to send the package
		
		  c = new DatagramSocket();
		
		  c.setBroadcast(true);
		
		 
		
		  byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();
		
		 
		
		  //Try the 255.255.255.255 first
		
		  try {
		
		    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
		
		    c.send(sendPacket);
		
		    System.out.println(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
		
		  } catch (Exception e) {
		
		  }
		
		 
		
		  // Broadcast the message over all the network interfaces
		
		  Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
		
		  while (interfaces.hasMoreElements()) {
		
		    NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
		
		 
		
		    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
		
		      //continue; // Don't want to broadcast to the loopback interface
		
		    }
		
		 
		
		    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
		
		      InetAddress broadcast = interfaceAddress.getBroadcast(); //broadcast des Netztes(z.B. 192.168.2.255)
		
		      if (broadcast == null) {
		
		        continue;
		
		      }
		
		 
		
		      // Send the broadcast package!
		
		      try {
		
		        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
		
		        c.send(sendPacket);
		
		      } catch (Exception e) {
		
		    }
		
		 
		
		      System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
		
		    }
		
		  }
		
		 
		
		  System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");
		
		
		  //START-SCHLEIFE; 
		  int timeout_waiting_response = 1000;//ms
//		  ArrayList<Client.Server> found_server = new ArrayList<Client.Server>(); 
		  serverlist.clear();
		  c.setSoTimeout(timeout_waiting_response);
		  long nowtime = System.currentTimeMillis(); //now timestamp in MS
		  int i2 = 0;
		  	while((System.currentTimeMillis()-nowtime)<timeout_waiting_response) {
//		  		System.out.println("TEEEEEEEEEEEEEEEEST: "+(System.currentTimeMillis()-nowtime)+" waiting: "+timeout_waiting_response);
//		  		System.out.println("DURCHLAUF_NR: "+i2);
		  		i2++;
				  //Wait for a response
				
				  byte[] recvBuf = new byte[15000];
				  DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
				  c.receive(receivePacket);
				
				  
				  //We have a response
				  String IP = receivePacket.getAddress().getHostAddress();
				  int lastByteIndex = IP.lastIndexOf(".");
				  String lastByte = IP.substring(lastByteIndex+1, IP.length());

//				  System.out.println("IP-ADRESS: "+IP+" lastByteIndex: "+lastByteIndex+" lastByte: "+lastByte);
				  
				  
				  if(lastByte.equals("1")) { //GATEWAY RESPONSE ABFANGEN
					  continue;
					  
				  }
				  
				  
				  System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
				
				  String s = new String(receivePacket.getData(),"UTF-8");
				  
				  String server_name = "";
				  String server_user_count = "";
				  
				  //Informationen des Servers auslesen
				  String text = s;
				  int index;
		
				  //Geschweifte Klammer auf finden
				  
				  while((index = text.indexOf("{"))!=-1) {//Geschweifte Klammer gefunden
		
					  text = text.substring(index); //Inhalt nach der Klammer
					  int index2;
					  
					  //Gleichaltszeichen finden
					  if((index2 = text.indexOf("="))!=-1) {//Gleichhaltszeichen gefunden
						index = text.indexOf("{");
		
						  String dataType = text.substring(index+1, index2);
						  if(dataType.equals("NAME")) {//If The dataType is the server-name
		
							  int index3 = text.indexOf("}");
							  String data = text.substring(index2+1, index3);
							  server_name = data;
		
							  
						  }else if(dataType.equals("USER_COUNT")) {//If the dataType is the server-user-count
		
							  int index3 = text.indexOf("}");
							  String data = text.substring(index2+1, index3);
							  server_user_count = data;
		
							  
						  }		
						  
						  text = text.substring(text.indexOf("}")+1);
						  
						  
					  }			  
				  }
				  
				  Server found_actual__server = new Server(receivePacket.getAddress().getHostAddress(), server_name, server_user_count);
				  
				  boolean new_server = true;
				  for(int i = 0; i < serverlist.size(); i++) {
					  if(found_actual__server.IP.equals(serverlist.get(i).IP)) {
						  if(found_actual__server.name.equals(serverlist.get(i).name)) {
								  new_server = false;
							  
						  }
					  }
				  }
				  
				  if(new_server) {
				  		serverlist.add(found_actual__server);
//				  		found_server.add(found_actual__server);
				  }
		
				  reloadServerTableList();
				  
//				  reloadServerTableList();
				  
				
				  //Check if the message is correct
				
				  String message = new String(receivePacket.getData()).trim();
				
				  if (message.equals("DISCOVER_FUIFSERVER_RESPONSE")) {
				
				    //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
				
		//		    Controller_Base.setServerIp(receivePacket.getAddress());
				
				  }
		  
		  	}//ENDE-SCHLEIFE
		  	
		  	


//		  	reloadServerTableList();
		  
		  	 c.close();
		  	
		} catch (IOException ex) {
		System.out.println("exception: \n"+ex.getMessage());
//		  Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
		
		}
	}
	
	
	public boolean connectToServer() {
		try {
//			client = new Socket("127.0.0.1", 5555);
//			client = new Socket("172.16.144.187", 5555);
//			client = new Socket("172.16.144.194", 5555);
			client = new Socket(connect_Server.getIP(), 5555);
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new PrintWriter(client.getOutputStream());
			appendTextMessages("Netzwerkverbindung hergestellt");
			
			return true;
		} catch(Exception e) {
			appendTextMessages("Netzwerkverbindung konnte nicht hergestellt werden");
			e.printStackTrace();
			
			return false;
		}
	}
	
	public void sendMessageToServer() {
		String msg = this.name + ":" + textField_ClientMessage.getText();
		if(checkMessage(last_timestamp)) {
			
			writer.println(msg);
			writer.flush();
//			System.out.println("MSG_CLIENT: "+msg);
			textField_ClientMessage.setText("");
			textField_ClientMessage.requestFocus();
			
		}
		

		
		last_timestamp = System.currentTimeMillis();
	}
	
	public void appendTextMessages(String message) {
		textArea_Messages.append(message + "\n"); //TODO:
	}
	
	public boolean checkMessage(long timestamp) {
		boolean isOkay = true;
		
//			if(textField_Username.getText().length()<2||textField_Username.getText().length()>20) {
//				textArea_Messages.append("!!!USERNAME_RULES: 2-20 CHARACTER!!!\n");
//				isOkay = false;
//			}
				if(textField_ClientMessage.getText().length()<2||textField_ClientMessage.getText().length()>100) {
					textArea_Messages.append("MESSAGE_RULES: 2-100 CHARACTER\n");
				isOkay = false;
			}
		
			
		if(timestamp>System.currentTimeMillis()-500) {
			textArea_Messages.append("!!!DO NOT SPAM!!!\n");
			
			isOkay = false;
		}else {
			last_timestamp = System.currentTimeMillis();
		}
		
		return isOkay;
	}
	
	// Listener
	public class SendPressEnterListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {
			if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				sendMessageToServer();
			}	
		}

		@Override
		public void keyReleased(KeyEvent arg0) {}

		@Override
		public void keyTyped(KeyEvent arg0) {}
		
	}
	
	public class SendButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			sendMessageToServer();			
		}
		
	}
	
	public class MessagesFromServerListener implements Runnable {

		@Override
		public void run() {
			
//			if(client.isClosed()) {//Ob die verbindung zum server weg ist
//				
//				serverlist.remove(connect_Server);
//				reloadServerTableList();
//			}else {
				
				String message;
				if(reader!=null) {
					try {
						while((message = reader.readLine()) != null) {
							appendTextMessages(message);
							textArea_Messages.setCaretPosition(textArea_Messages.getText().length());
						}
					} catch (IOException e) {
						appendTextMessages("Nachricht konnte nicht empfangen werden!");
						e.printStackTrace();
					}
				}
//			}
			

		}
	}
	private class Server {
		String IP = "0.0.0.0", name = "", user_count = "0/100";
	
		public Server(String IP, String name, String user_count) {
			
			this.IP = IP;
			this.name = name;
			this.user_count = user_count;
		}

		public String getIP() {
			return IP;
		}

		public void setIP(String iP) {
			IP = iP;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUser_count() {
			return user_count;
		}

		public void setUser_count(String user_count) {
			this.user_count = user_count;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand().equals("CONNECT_0")) {
			
			if(serverlist.size()>0 && serverlist.get(0)!=null) {
				connect_Server = serverlist.get(0);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("CONNECT_1")) {
			
			if(serverlist.size()>1 && serverlist.get(1)!=null) {
				connect_Server = serverlist.get(1);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("CONNECT_2")) {
			
			if(serverlist.size()>2 && serverlist.get(2)!=null) {
				connect_Server = serverlist.get(2);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("CONNECT_3")) {
			
			if(serverlist.size()>3 && serverlist.get(3)!=null) {
				connect_Server = serverlist.get(3);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("CONNECT_4")) {
			
			if(serverlist.size()>4 && serverlist.get(4)!=null) {
				connect_Server = serverlist.get(4);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("CONNECT_5")) {
			
			if(serverlist.size()>5 && serverlist.get(5)!=null) {
				connect_Server = serverlist.get(5);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("CONNECT_6")) {
			
			if(serverlist.size()>6 && serverlist.get(6)!=null) {
				connect_Server = serverlist.get(6);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("CONNECT_7")) {
			
			if(serverlist.size()>7 && serverlist.get(7)!=null) {
				connect_Server = serverlist.get(7);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("CONNECT_8")) {
			
			if(serverlist.size()>8 && serverlist.get(8)!=null) {
				connect_Server = serverlist.get(8);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("CONNECT_9")) {
			
			if(serverlist.size()>9 && serverlist.get(9)!=null) {
				connect_Server = serverlist.get(9);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("CONNECT_10")) {
			
			if(serverlist.size()>10 && serverlist.get(10)!=null) {
				connect_Server = serverlist.get(10);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("CONNECT_11")) {
			
			if(serverlist.size()>11 && serverlist.get(11)!=null) {
				connect_Server = serverlist.get(11);
				createChat();
			}
			
		}else if(e.getActionCommand().equals("RELOAD_SERVER_LIST")) {
			
			searchForServers();
			
			reloadServerTableList();
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {

		clientFrame.setVisible(true);
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}