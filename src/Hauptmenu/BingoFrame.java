package Hauptmenu;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Client.ClientControlPanel;
import Client.ClientLiveGamePanel;
import Client.ClientMainThread;
import Main.BingoMain;
import Server.ServerControlPanel;
import Server.ServerLiveGamePanel;

public class BingoFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private int width = 1265, height = 1000;// width: 1165
	private static JPanel contentPane;
	private JPanel midButtonPanel;
	private JLabel title;
	private static JButton createServer;
	private JButton joinServer;
	private JButton exit;
	private AllAction actionLis;
	private static CreateServerPanel createServerPanel;
	private static JoinServerPanel joinServerPanel;
	private static ServerControlPanel serverControlPanel;
	private ServerLiveGamePanel serverLiveGamePanel;
	private ClientControlPanel clientControlPanel;
	private ClientLiveGamePanel clientLiveGamePanel;
	private ImagePanel background = null;
	private static CardLayout cl = new CardLayout();
	public static final int MID_BUTTON_PANEL = 0;
	public static final int CREATE_SERVER_PANEL = 1;
	public static final int JOIN_SERVER_PANEL = 2;
	public static final int SERVER_CONTROL_PANEL = 3;
	public static final int SERVER_LIVE_GAME_PANEL = 4;
	public static final int CLIENT_CONTROL_PANEL = 5;
	public static final int CLIENT_LIVE_GAME_PANEL = 6;

	public BingoFrame() {
		super("Bingo");
		setSize(width, height);
		setMinimumSize(new Dimension(width, height));
		contentPane = new JPanel();
		contentPane.setLayout(cl);
		contentPane.setSize(this.getSize());

		// Buttons with Action
		actionLis = new AllAction();
		midButtonPanel = new JPanel();

		midButtonPanel.add(createServer = new JButton("Server erstellen"));
		createServer.addActionListener(actionLis);

		midButtonPanel.add(joinServer = new JButton("Server beitreten"));
		joinServer.addActionListener(actionLis);

		midButtonPanel.add(exit = new JButton("Spiel beenden"));
		exit.addActionListener(actionLis);

		midButtonPanel.setAlignmentX(SwingConstants.CENTER);
		midButtonPanel.setAlignmentY(SwingConstants.CENTER);

		try {
			background = new ImagePanel(ImageIO.read(ClassLoader.getSystemResource("img/Bims_komp.png")));
			background.setAlignmentX(SwingConstants.CENTER);
			background.setAlignmentY(SwingConstants.CENTER);
		} catch (IOException e) {

			e.printStackTrace();
		}
		title = new JLabel("<html>B  I  N  G  O  -  E  A  I  T  6</html>");
		title.setPreferredSize(new Dimension(400, 50));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Dialog", 1, 50));
		background.add(title, BorderLayout.PAGE_START);

		createServerPanel = new CreateServerPanel(this.getSize());
		createServerPanel.setAlignmentX(SwingConstants.CENTER);
		createServerPanel.setAlignmentY(SwingConstants.CENTER);

		joinServerPanel = new JoinServerPanel(this.getSize());
		joinServerPanel.setAlignmentX(SwingConstants.CENTER);
		joinServerPanel.setAlignmentY(SwingConstants.CENTER);

		serverControlPanel = new ServerControlPanel(this.getSize());
		serverControlPanel.setAlignmentX(SwingConstants.CENTER);
		serverControlPanel.setAlignmentY(SwingConstants.CENTER);

		serverLiveGamePanel = new ServerLiveGamePanel(this.getSize());
		serverLiveGamePanel.setAlignmentX(SwingConstants.CENTER);
		serverLiveGamePanel.setAlignmentY(SwingConstants.CENTER);

		clientControlPanel = new ClientControlPanel(this.getSize());
		clientControlPanel.setAlignmentX(SwingConstants.CENTER);
		clientControlPanel.setAlignmentY(SwingConstants.CENTER);

		clientLiveGamePanel = new ClientLiveGamePanel(this.getSize());
		clientLiveGamePanel.setAlignmentX(SwingConstants.CENTER);
		clientLiveGamePanel.setAlignmentY(SwingConstants.CENTER);

		contentPane.setOpaque(false);
		midButtonPanel.setOpaque(false);
		joinServerPanel.setOpaque(false);
		createServerPanel.setOpaque(false);
		serverControlPanel.setOpaque(false);
		serverLiveGamePanel.setOpaque(false);
		clientControlPanel.setOpaque(false);
		clientLiveGamePanel.setOpaque(false);

		/*
		 * CARDLAYOUT: - Card1: createServerPanel - Card2: joinServerPanel - Card3:
		 * midButtonPanel - Card4: serverControlPanel - Card5: serverLiveGamePanel -
		 * Card6: clientControlPanel - Card7: clientLiveGamePanel
		 */

		background.setToolTipText("Picture: ©BIMS BY FLEMG");
		getContentPane().add(background, BorderLayout.CENTER);
		background.add(contentPane, BorderLayout.CENTER);

		contentPane.add(createServerPanel, "createServerPanel");
		contentPane.add(joinServerPanel, "joinServerPanel");
		contentPane.add(midButtonPanel, "midButtonPanel");
		contentPane.add(serverControlPanel, "serverControlPanel");
		contentPane.add(serverLiveGamePanel, "serverLiveGamePanel");
		contentPane.add(clientControlPanel, "clientControlPanel");
		contentPane.add(clientLiveGamePanel, "clientLiveGamePanel");

		cl.show(contentPane, "midButtonPanel");

		setLocationRelativeTo(null);
		setDefaultCloseOperation(3);
		setVisible(true);
	}

	static boolean firtstTime = true;

	public static void showPane(int ID) {

		switch (ID) {
		case MID_BUTTON_PANEL:
			if (CreateServerPanel.serverIsRunning) {
				createServer.setText("Zum Server");
			} else {
				createServer.setText("Server erstellen");
			}
			cl.show(contentPane, "midButtonPanel");
			break;
		case CREATE_SERVER_PANEL:
			createServerPanel.reload();
			cl.show(contentPane, "createServerPanel");
			break;
		case JOIN_SERVER_PANEL:
			if (firtstTime) {
				BingoMain.clientThread = new ClientMainThread();
				joinServerPanel.startAutoReloadServerTableList();
			}
			ClientMainThread.shouldSearchForServer = true;
			cl.show(contentPane, "joinServerPanel");
			break;
		case SERVER_CONTROL_PANEL:
			setConsoleToPanel(SERVER_CONTROL_PANEL);
			serverControlPanel.reload();
			cl.show(contentPane, "serverControlPanel");
			break;
		case SERVER_LIVE_GAME_PANEL:
			setConsoleToPanel(SERVER_LIVE_GAME_PANEL);
			cl.show(contentPane, "serverLiveGamePanel");
			break;
		case CLIENT_CONTROL_PANEL:
			setChatToPanel(CLIENT_CONTROL_PANEL);
			cl.show(contentPane, "clientControlPanel");
			break;
		case CLIENT_LIVE_GAME_PANEL:
			setChatToPanel(CLIENT_LIVE_GAME_PANEL);
			ClientLiveGamePanel.reload();
			cl.show(contentPane, "clientLiveGamePanel");
			break;
		default:
			break;
		}
	}

	private static void setChatToPanel(int ID) {

		switch (ID) {

		case CLIENT_CONTROL_PANEL:

			ClientLiveGamePanel.setChat(false);
			ClientControlPanel.setChat(true);
			break;
		case CLIENT_LIVE_GAME_PANEL:

			ClientControlPanel.setChat(false);
			ClientLiveGamePanel.setChat(true);
			break;
		default:
			break;
		}
	}

	private static void setConsoleToPanel(int ID) {

		switch (ID) {

		case SERVER_CONTROL_PANEL:

			ServerLiveGamePanel.setConsole(false);
			ServerControlPanel.setConsole(true);
			break;
		case SERVER_LIVE_GAME_PANEL:

			ServerControlPanel.setConsole(false);
			ServerLiveGamePanel.setConsole(true);
			break;
		default:
			break;
		}
	}

	private class AllAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();

			if (action.equals(createServer.getText())) {

				if (CreateServerPanel.serverIsRunning) {

					showPane(SERVER_CONTROL_PANEL);
				} else {
					showPane(CREATE_SERVER_PANEL);
				}
			} else if (action.equals(joinServer.getText())) {

				showPane(JOIN_SERVER_PANEL);

			} else if (action.equals(exit.getText())) {

				System.exit(0);
			}
		}
	}

	private class ImagePanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private Image img;
		private Dimension size;

		public ImagePanel(Image img) {
			this.img = img;
			size = new Dimension(img.getWidth(null), img.getHeight(null));
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			setSize(size);

			setLayout(new BorderLayout());
		}

		public void paintComponent(Graphics g) {
			super.paintComponents(g);

			g.drawImage(img, 0, 0, (int) getSize().getWidth(), (int) getSize().getHeight(), null);
		}
	}
}