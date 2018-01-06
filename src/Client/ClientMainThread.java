package Client;

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
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;

import BingoToolkit.ObjectStringCoder;
import Hauptmenu.BingoFrame;
import Hauptmenu.JoinServerPanel;
import Main.BingoMain;
import Server.ServerAttributes;

public class ClientMainThread {

	// SearchForServers
	private static DatagramSocket c;
	public static ArrayList<ServerAttributes> serverlist;
	public static boolean shouldSearchForServer = true;

	// connect to server
	public static ServerAttributes connect_Server;

	// others
	static Socket client;
	static PrintWriter writer;
	static BufferedReader reader;
	public static String name;
	public static final String nameAlreadyTakenError = "NAME_ALREADY_TAKEN!",
			nameTakenQuestion = "IS_NAME_ALREADY_TAKEN?", BINGO_PATTERN = "BINGO_PATTERN",
			NO_CHAT_MESSAGE = "rHBvyWvqbR0JVs6x6g24", BINGO_SENTENCES = "BINGO_SENTENCES_FOLLOWING",
			BINGO_SENTENCE_ACTIVE = "BINGO_SENTENCE_ACTIVE", PATTERN_REACHED = "PATTERN_REACHED";
	public static ArrayList<String> bingoSentences = new ArrayList<String>();

	public ClientMainThread() {

		serverlist = new ArrayList<ServerAttributes>();

		Thread serverSearcher = new Thread(ServerSearcherThread.getServerSearcherThreadInstance());
		serverSearcher.start();
	}

	public static boolean connectToServer() { // if true wait for server-response
		try {

			ClientChat.clear();
			client = new Socket(connect_Server.getIP(), 5555);
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new PrintWriter(client.getOutputStream());

			Thread t = new Thread(MessagesFromServerListener.getMessagesFromServerListenerInstance());
			t.start();

			ClientMainThread.sendMessageToServer(NO_CHAT_MESSAGE + "JOINING" + name);

			return true;
		} catch (Exception e) {
			appendTextMessages("Netzwerkverbindung konnte nicht hergestellt werden", ClientChat.LEVEL_ERROR);
			e.printStackTrace();

			return false;
		}
	}

	public static boolean disconnectFromServer(boolean WasOnServerChat) {

		try {
			if (WasOnServerChat)
				ClientMainThread.sendMessageToServer(NO_CHAT_MESSAGE + "LEAVING" + name);

			client.close();
			reader.close();
			writer.close();

			connect_Server = null;
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	public static void sendMessageToServer(String message) {
		if (writer != null) {
			if (!message.contains(NO_CHAT_MESSAGE)) {

				message = name + ":" + message;
			}
			writer.println(message);
			writer.flush();
		}
		ClientChat.last_timestamp = System.currentTimeMillis();
	}

	public static void appendTextMessages(String message, int level) {
		ClientChat.appendTextToChat(message, level);
	}

	public static void executeCommand(String msg) {
		if (msg.substring(0, 1).equals("/")) { // Command

		} else { // Message
			sendMessageToServer(msg);
		}
	}

	public static class MessagesFromServerListener implements Runnable {

		@Override
		public void run() {

			String message;
			try {

				while ((message = reader.readLine()) != null) {
					if (message.contains(ClientMainThread.NO_CHAT_MESSAGE)) {
						if (message.contains(nameTakenQuestion)) {
							if (message.contains(nameAlreadyTakenError)) {
								disconnectFromServer(false);
								JoinServerPanel.nameAccepted(false);
							} else {
								JoinServerPanel.nameAccepted(true);
							}
						} else if (message.contains(BINGO_SENTENCES)) {
							int startIndex = message.indexOf("ANZAHL=") + 7;
							int endIndex = message.indexOf("}", message.indexOf("ANZAHL=") + 6);
							int anzahlBingoSentences = Integer.valueOf(message.substring(startIndex, endIndex));

							message = message.substring(endIndex + 1);
							startIndex = 1;

							for (int i = 0; i < anzahlBingoSentences; i++) {

								endIndex = message.indexOf("}");
								String objectString = message.substring(startIndex, endIndex);

								String translatedObjectString = (String) ObjectStringCoder.stringToObject(objectString);

								ClientMainThread.bingoSentences.add(translatedObjectString);

								message = message.substring(endIndex + 1);
							}
							ClientLiveGamePanel.setBingoSentencesRandomly(ClientMainThread.bingoSentences);
							BingoFrame.showPane(BingoFrame.CLIENT_LIVE_GAME_PANEL);
						} else if (message.contains(BINGO_SENTENCE_ACTIVE)) {
							// rHBvyWvqbR0JVs6x6g24BINGO_SENTENCE_ACTIVE{MSG=<html>Satz 5</html>}
							int startPos = message.indexOf("=") + 1, endPos = message.indexOf("}");
							String nachricht = message.substring(startPos, endPos);
							long timeStamp = System.currentTimeMillis();
							ClientLiveGamePanel.setSentenceActive(nachricht, timeStamp);
						} else if (message.contains(BINGO_PATTERN)) {

							ClientLiveGamePanel.setBingoPattern(message);
						}
					} else {
						appendTextMessages(message, ClientChat.LEVEL_INFO);
					}
				}
			} catch (Exception e) {
				appendTextMessages("Verbindung zum Server verloren. Vielleicht ist dieser Offline?",
						ClientChat.LEVEL_ERROR);
				e.printStackTrace();
			}
		}

		public static MessagesFromServerListener getMessagesFromServerListenerInstance() {

			return MessagesFromServerListenerThreadHolder.INSTANCE;
		}

		private static class MessagesFromServerListenerThreadHolder {

			private static final MessagesFromServerListener INSTANCE = new MessagesFromServerListener();
		}
	}

	public static class ServerSearcherThread implements Runnable {

		@Override
		public void run() {

			while (true) {

				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {

					e1.printStackTrace();
				}

				if (shouldSearchForServer) {

					// Search for Servers

					// Find the server using UDP broadcast

					try {

						// Open a random port to send the package

						c = new DatagramSocket();

						c.setBroadcast(true);

						byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();

						// Try the 255.255.255.255 first

						try {

							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
									InetAddress.getByName("255.255.255.255"), 8888);

							c.send(sendPacket);
							if (BingoMain.showClientNetworkInformation)
								ClientMainThread.appendTextMessages(
										name + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)",
										ClientChat.LEVEL_INFO);

						} catch (Exception e) {
						}

						// Broadcast the message over all the network interfaces

						@SuppressWarnings("rawtypes")
						Enumeration interfaces = NetworkInterface.getNetworkInterfaces();

						while (interfaces.hasMoreElements()) {

							NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

							if (networkInterface.isLoopback() || !networkInterface.isUp()) {

								// continue; // Don't want to broadcast to the loopback interface

							}

							for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {

								InetAddress broadcast = interfaceAddress.getBroadcast(); // Broadcast-Adresse des Netzes
																							// (z.B. 192.168.2.255)

								if (broadcast == null) {

									continue;

								}
								// Send the broadcast package!

								try {

									DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast,
											8888);

									c.send(sendPacket);

								} catch (Exception e) {
								}

								if (BingoMain.showClientNetworkInformation)
									ClientMainThread.appendTextMessages(
											name + ">>> Request packet sent to: " + broadcast.getHostAddress()
													+ "; Interface: " + networkInterface.getDisplayName(),
											ClientChat.LEVEL_INFO);
							}
						}

						if (BingoMain.showClientNetworkInformation)
							ClientMainThread.appendTextMessages(
									name + ">>> Done looping over all network interfaces. Now waiting for a reply!",
									ClientChat.LEVEL_INFO);

						// START-SCHLEIFE;

						int timeout_waiting_response = 1000; // ms
						serverlist.clear();
						c.setSoTimeout(timeout_waiting_response);
						long nowtime = System.currentTimeMillis(); // Timestamp from now in MS

						while ((System.currentTimeMillis() - nowtime) < timeout_waiting_response - 100) {

							// Wait for a response
							byte[] recvBuf = new byte[15000];
							DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);

							c.receive(receivePacket);

							// We have a response
							String IP = receivePacket.getAddress().getHostAddress();
							int lastByteIndex = IP.lastIndexOf(".");
							String lastByte = IP.substring(lastByteIndex + 1, IP.length());

							if (lastByte.equals("1"))
								continue; // Skip normal Gateway-Responses

							if (BingoMain.showClientNetworkInformation)
								ClientMainThread.appendTextMessages(name + ">>> Broadcast response from server: "
										+ receivePacket.getAddress().getHostAddress(), ClientChat.LEVEL_INFO);

							// Informationen des Servers auslesen
							String text = new String(receivePacket.getData(), "UTF-8");
							String server_name = "";
							String server_user_count = "";
							int index;

							// Geschweifte Klammer-auf finden
							while ((index = text.indexOf("{")) != -1) {// Geschweifte Klammer-auf gefunden

								text = text.substring(index); // Inhalt nach der Klammer
								int index2;

								// Gleichaltszeichen finden
								if ((index2 = text.indexOf("=")) != -1) {// Gleichhaltszeichen gefunden
									index = text.indexOf("{");

									String dataType = text.substring(index + 1, index2);
									if (dataType.equals("NAME")) {// If the dataType is the server-name

										int index3 = text.indexOf("}");
										String data = text.substring(index2 + 1, index3);
										server_name = data;

									} else if (dataType.equals("USER_COUNT")) {// If the dataType is the
																				// server-user-count
										int index3 = text.indexOf("}");
										String data = text.substring(index2 + 1, index3);
										server_user_count = data;
									}
									text = text.substring(text.indexOf("}") + 1);
								}
							}

							ServerAttributes found_actual__server = new ServerAttributes(
									receivePacket.getAddress().getHostAddress(), server_name, server_user_count);

							boolean new_server = true;
							for (int i = 0; i < serverlist.size(); i++) {
								if (found_actual__server.getIP().equals(serverlist.get(i).getIP())) {
									if (found_actual__server.getName().equals(serverlist.get(i).getName())) {
										new_server = false;
									}
								}
							}
							if (new_server) {
								serverlist.add(found_actual__server);
							}
						} // ENDE-SCHLEIFE

						c.close();

					} catch (SocketTimeoutException e) {

						if (BingoMain.showClientNetworkInformation)
							ClientMainThread.appendTextMessages(
									getClass().getName() + ">>> Got no reply - No Server found!",
									ClientChat.LEVEL_INFO);

					} catch (IOException ex) {

						ClientMainThread.appendTextMessages("exception: \n" + ex.getMessage(), ClientChat.LEVEL_ERROR);

					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						ClientMainThread.appendTextMessages(e.getMessage(), ClientChat.LEVEL_ERROR);
					}
				}
			}
		}

		public static ServerSearcherThread getServerSearcherThreadInstance() {

			return ServerSearcherThreadHolder.INSTANCE;
		}

		private static class ServerSearcherThreadHolder {

			private static final ServerSearcherThread INSTANCE = new ServerSearcherThread();
		}
	}
}