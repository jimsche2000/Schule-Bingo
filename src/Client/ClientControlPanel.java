package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import GUI.ShadowLabel;
import Hauptmenu.CreateServerPanel;
import Hauptmenu.BingoFrame;
import Main.BingoMain;
import Server.ServerMainThread;

/*
 * ClientControlPanel:
 * 
 * 	*Give suggestions for Bingo-sentences
 *  *Chat
 *  *Load and Save Suggestion-Packs(less or more as 9 Sentences) from data saved on HardDrive
 */
public class ClientControlPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton stopServer, skipBack;
	private static JPanel contentPane;
	private JTextField userNameTextField, maxUserTextField;
	private ShadowLabel maxUserLabel, title, labelServerName, chatTitle;

	// Components for Suggesting Bingo-Sentences
	private JPanel bingoSentencesPanel;
	private JTextField bingoSentenceTextField;
	private JButton sendBingoSentence;
	private ShadowLabel bingoSentenceTitle, SuggestionSendWarningLabel;
	private long lastSuggestionTimestamp = 0;

	public ClientControlPanel(Dimension size) {

		this.setLayout(new BorderLayout());
		contentPane = new JPanel();
		contentPane.setOpaque(false);
		contentPane.setBackground(Color.black);
		this.setSize(size);
		contentPane.setPreferredSize(new Dimension((int) (size.getWidth() * 0.5), (int) (size.getHeight() * 0.5)));
		contentPane.setMaximumSize(new Dimension((int) (size.getWidth() * 0.5), (int) (size.getHeight() * 0.5)));
		contentPane.setLayout(null);

		title = new ShadowLabel("Spiel wird konfiguriert", 30, 300, 49);
		title.setBounds(25, 0, 400, 60);
		contentPane.add(title);

		contentPane.setAlignmentX(SwingConstants.CENTER);
		contentPane.setAlignmentY(SwingConstants.CENTER);
		this.setAlignmentX(SwingConstants.CENTER);
		this.setAlignmentY(SwingConstants.CENTER);

		labelServerName = new ShadowLabel("Server-Name", 24, 300, 31);
		labelServerName.setBounds(25, 60, 300, 35);
		contentPane.add(labelServerName);

		userNameTextField = new JTextField(ServerMainThread.name);
		userNameTextField.setBounds(25, 100, 300, 25);
		userNameTextField.setColumns(10);
		userNameTextField.setEditable(false);
		contentPane.add(userNameTextField);

		maxUserLabel = new ShadowLabel("Maximale Anzahl der User", 24, 300, 31);
		maxUserLabel.setBounds(25, 130, 300, 31);
		contentPane.add(maxUserLabel);

		maxUserTextField = new JTextField(ServerMainThread.maxUser);
		maxUserTextField.setBounds(25, 165, 300, 25);
		maxUserTextField.setEditable(false);
		contentPane.add(maxUserTextField);

		skipBack = new JButton("Server verlassen");
		skipBack.setBounds(25, 255, 305, 50);
		skipBack.addActionListener(this);
		contentPane.add(skipBack);

		bingoSentencesPanel = new JPanel();
		bingoSentencesPanel.setBounds(400, 100, 502, 52);
		bingoSentencesPanel.setLayout(null);

		bingoSentenceTitle = new ShadowLabel("Dem Admin einen Bingo-Satz vorschlagen", 20, 300, 31);
		bingoSentenceTitle.setBounds(401, 60, 400, 38);
		contentPane.add(bingoSentenceTitle);

		SuggestionSendWarningLabel = new ShadowLabel("", 15, 300, 31);
		SuggestionSendWarningLabel.setBounds(401, 135, 400, 50);
		contentPane.add(SuggestionSendWarningLabel);

		bingoSentenceTextField = new JTextField();
		bingoSentenceTextField.setBounds(1, 1, 400, 50);
		sendBingoSentence = new JButton("Senden");
		sendBingoSentence.setBounds(401, 1, 100, 50);
		sendBingoSentence.addActionListener(this);
		sendBingoSentence.setActionCommand("sendSentence");

		bingoSentencesPanel.add(bingoSentenceTextField);
		bingoSentencesPanel.add(sendBingoSentence);
		contentPane.add(bingoSentencesPanel);

		chatTitle = new ShadowLabel("Chat", 25, 200, 35);
		chatTitle.setBounds(25, 400, 200, 39);
		contentPane.add(chatTitle);
		BingoMain.clientChat.setBounds(25, 450, 877, 340);// 877 340
		contentPane.add(BingoMain.clientChat);
		this.add(contentPane, BorderLayout.CENTER);
	}

	public static void setChat(boolean visible) {
		if (visible) {
			contentPane.add(BingoMain.clientChat);
		} else {
			contentPane.remove(BingoMain.clientChat);
		}
	}

	public void reload() {

		userNameTextField.setText(ServerMainThread.name);
		maxUserTextField.setText(ServerMainThread.maxUser);
		stopServer.setEnabled(CreateServerPanel.serverIsRunning);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		if (actionCommand.equals(skipBack.getText())) {

			ClientMainThread.disconnectFromServer(true);

			BingoFrame.showPane(BingoFrame.JOIN_SERVER_PANEL);
		} else if (actionCommand.equals("sendSentence")) {
			// Satz-Vorschlag an den Server schicken
			// System.out.println(lastSuggestionTimestamp-System.currentTimeMillis());
			if (lastSuggestionTimestamp == 0 || (lastSuggestionTimestamp - System.currentTimeMillis()) < -10000) {
				ClientMainThread.sendMessageToServer(ClientMainThread.NO_CHAT_MESSAGE + "VORSCHLAG:"
						+ ClientMainThread.name + " schlägt vor: \"" + bingoSentenceTextField.getText() + "\"");
				lastSuggestionTimestamp = System.currentTimeMillis();
				bingoSentenceTextField.setText("");
				SuggestionSendWarningLabel.setText("Vorschlag wurde abgeschickt.");
			} else {
				SuggestionSendWarningLabel.setText(
						"Warte noch " + (((lastSuggestionTimestamp - System.currentTimeMillis()) / 1000) + 10) + "s");
			}
		}
	}

	public class SendPressEnterListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				JTextField comp = (JTextField) e.getComponent();
				ClientMainThread.executeCommand(comp.getText());
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}
	}
}