package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import Client.ClientAttributes;
import Hauptmenu.CreateServerPanel;
import Main.BingoMain;

public class ServerMainThread implements Runnable {

	ServerSocket server;
	static ArrayList<PrintWriter> listClientWriter;
	static ArrayList<ClientAttributes> listClients;
	private static boolean shouldRun = false;
	public static String name, maxUser;
	public static final String nameTakenQuestion = "IS_NAME_ALREADY_TAKEN?",
			nameAlreadyTakenError = "NAME_ALREADY_TAKEN!", NO_CHAT_MESSAGE = "rHBvyWvqbR0JVs6x6g24",
			BINGO_SENTENCES = "BINGO_SENTENCES_FOLLOWING", BINGO_SENTENCE_ACTIVE = "BINGO_SENTENCE_ACTIVE",
			BINGO_PATTERN = "BINGO_PATTERN", PATTERN_REACHED = "PATTERN_REACHED";
	private static ArrayList<String> leavedPlayerNames = new ArrayList<String>();

	@Override
	public void run() {
		System.out.println(getClass().getName() + ":");
		while (true) {

			if (shouldRun) {
				Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
				discoveryThread.start();

				if (runServer()) {
					listenToClients();
				}
			}
		}
	}

	public ServerMainThread(String name, Long long1) {

		ServerMainThread.name = name;
		ServerMainThread.maxUser = Long.toString(long1);
		ServerMainThread.shouldRun = true;
	}

	public static void stop() {
		shouldRun = false;
		CreateServerPanel.serverIsRunning = false;
		try {

			BingoMain.hostServer.server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DiscoveryThread.socket.close();
		ServerMainThread.appendTextToConsole("Server wurde gestoppt!", ServerConsole.LEVEL_ERROR);
	}

	public void listenToClients() {
		while (shouldRun) {
			try {
				if (!server.isClosed()) {

					Socket client = null;
					try {

						client = server.accept();
					} catch (Exception e) {
						continue;
					}
					PrintWriter writer = new PrintWriter(client.getOutputStream());
					listClientWriter.add(writer);

					Thread clientThread = new Thread(new ClientHandler(client));
					clientThread.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean runServer() {
		try {

			BingoMain.hostServer.server = new ServerSocket(5555);
			appendTextToConsole("Server wurde gestartet!", ServerConsole.LEVEL_INFO);

			listClientWriter = new ArrayList<PrintWriter>();
			listClients = new ArrayList<ClientAttributes>();
			return true;
		} catch (IOException e) {
			appendTextToConsole("Server konnte nicht gestartet werden! Läuft bereits ein Server an diesem PC?",
					ServerConsole.LEVEL_ERROR);
			e.printStackTrace();
			return false;
		}
	}

	public static void executeCommand(String cmd) {
		if (cmd.substring(0, 1).equals("/")) { // Command

		} else { // Message
			appendTextToConsole("Admin: " + cmd, ServerConsole.LEVEL_NORMAL);
			sendToAllClients("Admin: " + cmd);
		}
	}

	public static void appendTextToConsole(String message, int level) {
		ServerConsole.appendTextToConsole(message, level);

	}

	public static void sendToAllClients(String message) {
		if (listClientWriter != null) {

			@SuppressWarnings("rawtypes")
			Iterator it = listClientWriter.iterator();

			while (it.hasNext()) {
				PrintWriter writer = (PrintWriter) it.next();

				writer.println(message);
				writer.flush();
			}
		}
	}

	public static void sendToClient(Socket client, String msg) {

		PrintWriter writer;
		try {
			writer = new PrintWriter(client.getOutputStream());
			writer.println(msg);
			writer.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void removeClientFromList(String ip, String name) {

		lala: for (int i = 0; i < listClients.size(); i++) {

			String ipAddr = listClients.get(i).getIP();
			String ClName = listClients.get(i).getName();

			if (ipAddr.equals(ip) && ClName.equals(name)) {
				listClients.remove(i);
				break lala;
			}
		}
	}

	public static boolean nameAlreadyTaken(String name) {
		if (listClients.size() > 0) {
			for (ClientAttributes client : listClients) {
				if (client.getName().equals(name))
					return true;
			}
			return false;
		}
		return false;
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

		@SuppressWarnings("unlikely-arg-type")
		@Override
		public void run() {
			if (!client.isClosed() && shouldRun) {
				String nachricht;
				String IP = client.getInetAddress().getHostAddress();
				String name = "";
				try {
					// if connection reset exception -> user leaved game
					while ((nachricht = reader.readLine()) != null) {
						if (nachricht.contains(NO_CHAT_MESSAGE)) {

							if (nachricht.contains("JOINING")) {// "JOINING".equals(nachricht.substring(0,
																// nachricht.indexOf("G", 0)+1))

								IP = client.getInetAddress().getHostAddress();
								name = nachricht.substring(nachricht.indexOf("JOINING", 0) + 7);
								if (!nameAlreadyTaken(name)) {
									if (leavedPlayerNames.contains(name))
										leavedPlayerNames.remove(name);

									listClients.add(new ClientAttributes(IP, name));
									ServerMainThread.appendTextToConsole(name + " ist dem Server beigetreten",
											ServerConsole.LEVEL_INFO);
									sendToClient(client, ServerMainThread.NO_CHAT_MESSAGE + nameTakenQuestion);
									ServerMainThread.sendToAllClients(name + " ist dem Server beigetreten");
								} else {

									sendToClient(client, ServerMainThread.NO_CHAT_MESSAGE + nameTakenQuestion
											+ nameAlreadyTakenError);
								}
							}
							// LEAVINGname
							else if (nachricht.contains("LEAVING")) {// "LEAVING".equals(nachricht.substring(0,
																		// nachricht.indexOf("G", 0)+1))

								IP = client.getInetAddress().getHostAddress();
								name = nachricht.substring(nachricht.indexOf("LEAVING", 0) + 7);
								leavedPlayerNames.add(name);
								removeClientFromList(IP, name);

								ServerMainThread.appendTextToConsole(name + " hat den Server verlassen",
										ServerConsole.LEVEL_INFO);
								ServerMainThread.sendToAllClients(name + " hat den Server verlassen");

							} else if (nachricht.contains("VORSCHLAG")) {
								String message = nachricht.substring(nachricht.indexOf("VORSCHLAG") + 10);

								ServerMainThread.appendTextToConsole(message, ServerConsole.LEVEL_INFO);
							} else if (nachricht.contains(PATTERN_REACHED)) {
								int startPos = nachricht.indexOf(PATTERN_REACHED) + PATTERN_REACHED.length();

								String message = nachricht.substring(startPos) + " hat diese Bingo-Runde gewonnen!";
								for (int i = 0; i < 10; i++) {
									appendTextToConsole(message, ServerConsole.LEVEL_INFO);
									ServerMainThread.sendToAllClients(message);
								}
							}
						} else {
							int pos = nachricht.indexOf(":");
							String name2 = nachricht.substring(0, pos);
							nachricht = nachricht.substring(pos + 1, nachricht.length());
							String sendMSG = name2 + ": " + nachricht;
							appendTextToConsole(sendMSG, ServerConsole.LEVEL_NORMAL);
							sendToAllClients(sendMSG);
						}
					}
				} catch (IOException e) {
					if (!leavedPlayerNames.contains(name)) {
						removeClientFromList(IP, name);

						ServerMainThread.appendTextToConsole(name + " hat den Server verlassen",
								ServerConsole.LEVEL_INFO);
						ServerMainThread.sendToAllClients(name + " hat den Server verlassen");

						e.printStackTrace();
					}
				}
			} else if (client.isClosed() && listClients.contains(client)) {// Client geschlossen
				try {
					int index = listClientWriter.indexOf(client);
					listClientWriter.remove(client);

					String name = ((ClientAttributes) listClients.get(index)).getName();

					ServerMainThread.appendTextToConsole(name + " hat den Server verlassen", ServerConsole.LEVEL_INFO);
					ServerMainThread.sendToAllClients(name + " hat den Server verlassen");
				} catch (Exception e2) {

					ServerMainThread.appendTextToConsole("Unbekannt hat den Server verlassen",
							ServerConsole.LEVEL_INFO);
					ServerMainThread.sendToAllClients("Unbekannt hat den Server verlassen");
				}
			}
		}
	}

	public static class DiscoveryThread implements Runnable {
		static DatagramSocket socket;

		@Override
		public void run() {
			try {
				// Keep a socket open to listen to all the UDP trafic that is destined for this
				// port
				socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));

				socket.setBroadcast(true);

				while (shouldRun) {
					if (!socket.isClosed()) {
						if (BingoMain.showServerNetworkInformation)
							ServerMainThread.appendTextToConsole(
									getClass().getName() + ">>>Ready to receive broadcast packets!",
									ServerConsole.LEVEL_INFO);
						// Receive a packet

						byte[] recvBuf = new byte[15000];

						DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

						try {
							socket.receive(packet);
						} catch (Exception e) {
							continue;
						}
						// Packet received
						if (BingoMain.showServerNetworkInformation)
							ServerMainThread.appendTextToConsole(getClass().getName()
									+ ">>>Discovery packet received from: " + packet.getAddress().getHostAddress(),
									ServerConsole.LEVEL_INFO);

						if (BingoMain.showServerNetworkInformation)
							ServerMainThread.appendTextToConsole(
									getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()),
									ServerConsole.LEVEL_INFO);
						// See if the packet holds the right command (message)

						String message = new String(packet.getData()).trim();

						if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {
							String text = "DISCOVER_FUIFSERVER_RESPONSE{NAME=" + name + "}{USER_COUNT="
									+ listClients.size() + "/" + maxUser + "}";
							byte[] sendData = text.getBytes("UTF-8");

							// Send a response

							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
									packet.getAddress(), packet.getPort());

							socket.send(sendPacket);
							if (BingoMain.showServerNetworkInformation)
								ServerMainThread.appendTextToConsole(getClass().getName() + ">>>Sent packet to: "
										+ sendPacket.getAddress().getHostAddress(), ServerConsole.LEVEL_INFO);
						}
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
}