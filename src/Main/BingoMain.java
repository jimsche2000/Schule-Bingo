package Main;

import Client.ClientChat;
import Client.ClientMainThread;
import Hauptmenu.BingoFrame;
import Server.ServerConsole;
import Server.ServerMainThread;

public class BingoMain {

	public static ServerMainThread hostServer;
	public static ServerConsole hostServerConsole = new ServerConsole();
	public static ClientMainThread clientThread;
	public static ClientChat clientChat = new ClientChat();
	public static BingoFrame menu;
	public static boolean showClientNetworkInformation = false;
	public static boolean showServerNetworkInformation = false;

	public static void main(String[] args) {

		menu = new BingoFrame();
	}

	public static ServerMainThread getInstance() {

		return ServerThreadHolder.INSTANCE;
	}

	private static class ServerThreadHolder {

		private static final ServerMainThread INSTANCE = BingoMain.hostServer;
	}
}
