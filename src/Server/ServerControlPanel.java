package Server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import BingoToolkit.ObjectStringCoder;
import GUI.ShadowLabel;
import Hauptmenu.BingoFrame;
import Hauptmenu.CreateServerPanel;
import Main.BingoMain;

//import Main.BingoMain;
/*
 * ServerControlPanel:
 * 
 * 	*Set 9 Sentences for Bingo
 *  *Players can join
 *  *Players can give suggestions for Bingo-sentences
 */
public class ServerControlPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton stopServer, startGame, skipBack;
	private static JPanel contentPane;
	private JTextField userNameTextField, maxUserTextField;
	private ShadowLabel maxUserLabel, title, labelServerName, consoleTitle;

	// Components for Setting up 9 Bingo-Sentences
	private JPanel bingoSentencesPanel;
	private ArrayList<JButton> deleteSentenceButtons, editSentenceButtons;
	private ArrayList<JTextField> bingoSentenceTextFields;
	private ArrayList<String> bingoSentences;
	private ImageIcon deleteIcon, editIcon;
	private ShadowLabel bingoSentenceTitle;

	// Components for Bingo-template (Muster zum Bingo-Sieg)
	private JPanel bingoPatternPanel;
	private static ArrayList<JCheckBox> bingoPatternCheckBoxes;
	private ShadowLabel bingoPatternTitle;

	public ServerControlPanel(Dimension size) {

		this.setLayout(new BorderLayout());
		contentPane = new JPanel();
		contentPane.setOpaque(false);
		contentPane.setBackground(Color.black);
		this.setSize(size);
		contentPane.setPreferredSize(new Dimension((int) (size.getWidth() * 0.5), (int) (size.getHeight() * 0.5)));
		contentPane.setMaximumSize(new Dimension((int) (size.getWidth() * 0.5), (int) (size.getHeight() * 0.5)));
		contentPane.setLayout(null);

		title = new ShadowLabel("Spiel konfigurieren", 30, 300, 49);
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

		stopServer = new JButton("Server Stoppen"); // Nachricht "Server geschlossen" an alle Clients senden und
													// Interrupt in allen ServerThreads auslösen
		stopServer.setBounds(25, 200, 150, 50);
		stopServer.addActionListener(this);
		contentPane.add(stopServer);

		startGame = new JButton("Spiel Starten"); // Gewählte 9 Bingo-Sätze an alle Clients senden(Clients sollen im 3x3
													// Raster ein zufälliges Layout anfertigen) & zum
													// ServerLiveGamePanel springen.
		startGame.setBounds(180, 200, 150, 50);
		startGame.addActionListener(this);
		contentPane.add(startGame);

		skipBack = new JButton("Zurück zum Hauptmenü");
		skipBack.setBounds(25, 255, 305, 50);
		skipBack.addActionListener(this);
		contentPane.add(skipBack);

		bingoSentencesPanel = new JPanel();
		bingoSentencesPanel.setBounds(355, 100, 502, 299);
		bingoSentencesPanel.setLayout(null);

		deleteSentenceButtons = new ArrayList<JButton>();
		editSentenceButtons = new ArrayList<JButton>();
		bingoSentenceTextFields = new ArrayList<JTextField>();
		bingoSentences = new ArrayList<String>();

		bingoSentenceTitle = new ShadowLabel("Bingo-Sätze", 25, 300, 31);
		bingoSentenceTitle.setBounds(355, 60, 400, 38);
		contentPane.add(bingoSentenceTitle);

		editIcon = BingoToolkit.ImageLoader.loadIcon("edit_icon.png", 25, 25);
		deleteIcon = BingoToolkit.ImageLoader.loadIcon("delete_icon.png", 25, 25);

		for (int i = 0; i < 9; i++) { // 9 Bingo Sentences with edit and delete buttons

			JButton editSentenceButton = new JButton();
			editSentenceButton.setBounds(401, 1 + i * 33, 50, 33);
			editSentenceButton.addActionListener(this);
			editSentenceButton.setActionCommand("EDIT_SENTENCE" + i);
			editSentenceButton.setIcon(editIcon);
			editSentenceButton.setToolTipText("Satz bearbeiten");
			editSentenceButtons.add(editSentenceButton);
			bingoSentencesPanel.add(editSentenceButton);

			JButton deleteSentenceButton = new JButton();
			deleteSentenceButton.setBounds(451, 1 + i * 33, 50, 33);
			deleteSentenceButton.addActionListener(this);
			deleteSentenceButton.setActionCommand("DELETE_SENTENCE" + i);
			deleteSentenceButton.setIcon(deleteIcon);
			deleteSentenceButton.setToolTipText("Satz entfernen");
			deleteSentenceButtons.add(deleteSentenceButton);
			bingoSentencesPanel.add(deleteSentenceButton);

			JTextField bingoSentenceTextField = new JTextField("Satz " + (i + 1));
			bingoSentenceTextField.setBounds(1, 1 + i * 33, 400, 33);
			bingoSentenceTextField.setEditable(false);
			bingoSentenceTextField.addKeyListener(new SendPressEnterListener());
			bingoSentenceTextFields.add(bingoSentenceTextField);
			bingoSentencesPanel.add(bingoSentenceTextField);

			bingoSentences.add(bingoSentenceTextField.getText());

		}
		contentPane.add(bingoSentencesPanel);

		bingoPatternCheckBoxes = new ArrayList<JCheckBox>();

		bingoPatternPanel = new JPanel();
		bingoPatternPanel.setBounds(882, 100, 243, 110);

		bingoPatternTitle = new ShadowLabel("Gefordertes Bingo-Muster", 18, 245, 31);
		bingoPatternTitle.setBounds(882, 60, 243, 38);

		JPanel bingoPatternCheckBoxesPanel = new JPanel();
		bingoPatternCheckBoxesPanel.setLayout(new GridLayout(3, 3, 1, 1));

		for (int i = 0; i < (3 * 3); i++) {

			JCheckBox check = new JCheckBox("" + (i + 1));
			if (i % 2 == 0)
				check.setSelected(true);
			bingoPatternCheckBoxesPanel.add(check);
			bingoPatternCheckBoxes.add(i, check);
		}

		contentPane.add(bingoPatternTitle);
		bingoPatternPanel.add(bingoPatternCheckBoxesPanel);
		bingoPatternPanel.setOpaque(false);
		contentPane.add(bingoPatternPanel);

		consoleTitle = new ShadowLabel("Server-Konsole", 25, 200, 35);
		consoleTitle.setBounds(25, 380, 200, 39);
		contentPane.add(consoleTitle);
		BingoMain.hostServerConsole.setBounds(25, 420, 877, 370);// 877 340
		contentPane.add(BingoMain.hostServerConsole);

		this.add(contentPane, BorderLayout.CENTER);
	}

	public void reload() {

		userNameTextField.setText(ServerMainThread.name);
		maxUserTextField.setText(ServerMainThread.maxUser);
		stopServer.setEnabled(CreateServerPanel.serverIsRunning);
	}

	public static void setConsole(boolean visible) {
		if (visible) {
			contentPane.add(BingoMain.hostServerConsole, BorderLayout.PAGE_END);
		} else {
			contentPane.remove(BingoMain.hostServerConsole);
		}
	}

	public static String getBingoPattern() {// Muster der 3x3 angeordneten Bingo Buttons, welches soweit alle Buttons
											// aktiviert sind, zum Sieg führt
		// BINGO_PATTERN={0:true}{1:false}{2:true}{3:true}..

		String patternString = ServerMainThread.BINGO_PATTERN + "=";
		// for: set all checkBox values
		for (int i = 0; i < 9; i++) {

			patternString += "{" + i + ":" + bingoPatternCheckBoxes.get(i).isSelected() + "}";
		}

		return patternString;
	}

	private String getBingoSentencesAsString() {
		String message = ServerMainThread.BINGO_SENTENCES + "{ANZAHL=" + bingoSentences.size() + "}";

		for (String bingoSentence : bingoSentences) {

			try {
				message += ("{" + ObjectStringCoder.objectToString(bingoSentence) + "}");
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		}
		return message;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();

		if (actionCommand.equals(stopServer.getText())) {
			// Nachricht "Server geschlossen" an alle Clients senden und Interrupt in allen
			// ServerThreads auslösen
			stopServer.setEnabled(false);
			ServerMainThread.sendToAllClients("SERVER_CLOSED");
			ServerMainThread.stop();

		} else if (actionCommand.equals(startGame.getText())) {
			// Gewählte 9 Bingo-Sätze an alle Clients senden(Clients sollen im 3x3 Raster
			// ein zufälliges Layout anfertigen) & zum LiveGamePanel springen.

			/*
			 * String-Syntax:
			 * 
			 * String message =
			 * NO_CHAT_MESSAGE+BINGO-SENTENCES"{ANZAHL="+bingoSentences.length+"}";
			 *
			 * for(String bingoSentence : bingoSentences) {
			 *
			 * message += ("{"+ObjectStringCoder.ObjectToString(bingoSentence)+"}")
			 *
			 * }
			 *
			 */

			ServerLiveGamePanel.setBingoSentences(bingoSentences);

			ServerMainThread.sendToAllClients(ServerMainThread.NO_CHAT_MESSAGE + getBingoSentencesAsString());
			ServerMainThread.sendToAllClients(ServerMainThread.NO_CHAT_MESSAGE + "" + getBingoPattern());
			ServerLiveGamePanel.setBingoPattern(getBingoPattern());

			BingoFrame.showPane(BingoFrame.SERVER_LIVE_GAME_PANEL);

		} else if (actionCommand.equals(skipBack.getText())) {
			BingoFrame.showPane(BingoFrame.MID_BUTTON_PANEL);

		} else if ("EDIT_SENTENCE".equals(new String(actionCommand.substring(0, e.getActionCommand().length() - 1)))) {

			int buttonID = Integer.valueOf(actionCommand.substring(actionCommand.length() - 1));
			JTextField activatedTextField = bingoSentenceTextFields.get(buttonID);

			if (!activatedTextField.isEditable()) {
				bingoSentences.remove(activatedTextField.getText());
				startGame.setEnabled(false);

				activatedTextField.setEditable(true);
				activatedTextField.requestFocus();
			} else {

				if (bingoSentences.indexOf(activatedTextField.getText()) == -1) { // ob der text nicht schon im array
																					// vorhanden ist
					if (!activatedTextField.getText().isEmpty()) { // ob text im textField steht
						if (activatedTextField.isEnabled()) {// ob es gerade bearbeitet wurde
							bingoSentences.add(activatedTextField.getText());
						}
					}
				}
				if (bingoSentences.size() == 9)
					startGame.setEnabled(true);
				activatedTextField.setEditable(false);
				requestFocus();
			}
		} else if ("DELETE_SENTENCE"
				.equals(new String(actionCommand.substring(0, e.getActionCommand().length() - 1)))) {

			int buttonID = Integer.valueOf(actionCommand.substring(actionCommand.length() - 1));
			JTextField deleteTextField = bingoSentenceTextFields.get(buttonID);
			bingoSentences.remove(deleteTextField.getText());
			deleteTextField.setText("");
		}
	}

	public class SendPressEnterListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				JTextField comp = (JTextField) e.getComponent();
				bingoSentences.add(comp.getText());
				comp.setEditable(false);
				requestFocus();
				if (bingoSentences.size() == 9)
					startGame.setEnabled(true);
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