package Hauptmenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import GUI.ShadowLabel;
import Main.BingoMain;
import Server.ServerMainThread;

public class CreateServerPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JTextField userNameTextField, maxUserTextField;
	private ShadowLabel maxUserLabel, title, labelServerName;
	private JButton hostServer, skipBack;
	private JPanel properties;
	public static boolean serverIsRunning = false;

	public CreateServerPanel(Dimension size) {

		this.setLayout(new BorderLayout());
		properties = new JPanel();
		properties.setOpaque(false);
		properties.setBackground(Color.black);
		this.setSize(size);
		properties.setPreferredSize(new Dimension((int) (size.getWidth() * 0.5), (int) (size.getHeight() * 0.5)));
		properties.setMaximumSize(new Dimension((int) (size.getWidth() * 0.5), (int) (size.getHeight() * 0.5)));
		properties.setLayout(null);

		title = new ShadowLabel("BINGO - Server", 40, 300, 49);
		title.setBounds(25, 0, 300, 49);
		properties.add(title);

		labelServerName = new ShadowLabel("Server-Name", 24, 300, 31);
		labelServerName.setBounds(44, 65, 300, 31);
		properties.add(labelServerName);

		userNameTextField = new JTextField(System.getProperty("user.name"));
		userNameTextField.setBounds(44, 100, 300, 25);
		properties.add(userNameTextField);
		userNameTextField.setColumns(10);

		maxUserLabel = new ShadowLabel("Maximale Anzahl der User", 24, 300, 31);
		maxUserLabel.setBounds(44, 130, 300, 31);
		properties.add(maxUserLabel);

		maxUserTextField = new JTextField("100");
		maxUserTextField.setBounds(44, 165, 300, 25);
		properties.add(maxUserTextField);

		hostServer = new JButton("Server hosten");
		hostServer.setBounds(44, 215, 300, 31);
		hostServer.addActionListener(this);
		hostServer.setActionCommand("HOST_SERVER");
		properties.add(hostServer);

		skipBack = new JButton("Zurück");
		skipBack.setBounds(25, 300, 100, 50);
		skipBack.addActionListener(this);
		skipBack.setActionCommand("SKIP_BACK");
		properties.add(skipBack);

		properties.setAlignmentX(SwingConstants.CENTER);
		properties.setAlignmentY(SwingConstants.CENTER);
		this.setAlignmentX(SwingConstants.CENTER);
		this.setAlignmentY(SwingConstants.CENTER);

		this.add(properties, BorderLayout.CENTER);
	}

	public void reload() {
		hostServer.setEnabled(!serverIsRunning);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("HOST_SERVER")) {
			serverIsRunning = true;

			BingoMain.hostServer = new ServerMainThread(userNameTextField.getText(),
					Long.valueOf(maxUserTextField.getText()));
			Thread serverThread = new Thread(BingoMain.getInstance());
			serverThread.start();
			BingoFrame.showPane(BingoFrame.SERVER_CONTROL_PANEL);

		} else if (e.getActionCommand().equals("SKIP_BACK")) {
			BingoFrame.showPane(BingoFrame.MID_BUTTON_PANEL);
		}
	}
}