package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import GUI.ShadowLabel;
import Main.BingoMain;

/*
 * ClientLiveGamePanel
 * 
 * show Chat
 * show 3x3 Button Raster for requestActivating sentences
 */
public class ClientLiveGamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static ArrayList<JButton> bingoButtons = new ArrayList<JButton>();
	private static JPanel contentPane = new JPanel();
	private JPanel buttonPane = new JPanel();
	private static HashMap<String, Long> serverTimestamps = new HashMap<String, Long>();
	private static HashMap<String, Long> buttonTimestamps = new HashMap<String, Long>();
	private JPanel bingoPatternPanel;
	private static ArrayList<JCheckBox> bingoPatternCheckBoxes;
	private ShadowLabel bingoPatternTitle;
	private static boolean hasAlreadyWon = false;

	public ClientLiveGamePanel(Dimension size) {
		contentPane.setLayout(new BorderLayout());
		buttonPane.setLayout(new GridLayout(3, 3, 1, 1));
		buttonPane.setPreferredSize(new Dimension(454, 454));

		for (int i = 0; i < 9; i++) {

			JButton exampleButton = new JButton("");
			exampleButton.setPreferredSize(new Dimension(150, 150));
			exampleButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					buttonTimestamps.put(exampleButton.getText(), System.currentTimeMillis());
				}
			});

			bingoButtons.add(exampleButton);
			buttonPane.add(exampleButton);
		}

		bingoPatternCheckBoxes = new ArrayList<JCheckBox>();

		bingoPatternPanel = new JPanel();
		bingoPatternPanel.setBounds(882, 100, 243, 110);

		bingoPatternTitle = new ShadowLabel("Gefordertes Bingo-Muster", 18, 245, 31);
		bingoPatternTitle.setPreferredSize(new Dimension(243, 38));

		JPanel bingoPatternCheckBoxesPanel = new JPanel();
		bingoPatternCheckBoxesPanel.setLayout(new GridLayout(3, 3, 1, 1));
		for (int i = 0; i < (3 * 3); i++) {

			JCheckBox check = new JCheckBox("" + (i + 1));
			if (i % 2 == 0)
				check.setSelected(true);
			check.setEnabled(false);
			bingoPatternCheckBoxesPanel.add(check);
			bingoPatternCheckBoxes.add(i, check);
		}

		bingoPatternPanel.add(bingoPatternTitle);
		bingoPatternPanel.add(bingoPatternCheckBoxesPanel);
		bingoPatternPanel.setOpaque(false);
		contentPane.add(bingoPatternPanel, BorderLayout.PAGE_START);

		buttonPane.setOpaque(false);
		contentPane.setOpaque(false);
		this.setOpaque(false);
		contentPane.add(buttonPane, BorderLayout.CENTER);
		this.add(contentPane);
	}

	public static void setChat(boolean visible) {
		if (visible) {
			contentPane.add(BingoMain.clientChat, BorderLayout.PAGE_END);
		} else {
			contentPane.remove(BingoMain.clientChat);
		}
	}

	private static void checkIfBingoSentenceActive(String keyMsg) {
		long buttonTimestamp;
		long serverTimestamp;

		try {
			buttonTimestamp = buttonTimestamps.get(keyMsg);
			serverTimestamp = serverTimestamps.get(keyMsg);
		} catch (Exception e) {
			return;
		}
		long timeOutPressed = 5000; // Millisekunden
		// Zeitraum 5 Sekunden vor- sowie bis 5 Sekunde nachdem der Admin gedrückt hat
		// gültig
		long timedifferenz = Math.abs(buttonTimestamp - serverTimestamp); // MAth.abs: returns absolutive value.
																			// Example: Math.abs(-1) returns 1
		if (timedifferenz < timeOutPressed) { // 5 Sekunden vorher und nachher

			for (JButton button : bingoButtons) {

				if (button.getText().equals(keyMsg)) {

					button.setEnabled(false);
					button.setBackground(Color.green);
					button.setForeground(Color.BLACK);
				}
			}
		}
	}

	public static void setBingoSentencesRandomly(ArrayList<String> sentences) {

		ArrayList<Integer> usedButtonIDs = new ArrayList<Integer>();
		for (String sentence : sentences) {
			int random; // random number between 0 - 9
			do {
				random = (int) Math.round(Math.random() * 8);

			} while (usedButtonIDs.contains(random));

			sentence = "<html>" + sentence + "</html>";

			serverTimestamps.put(sentence, 0l);
			bingoButtons.get(random).setText(sentence);
			usedButtonIDs.add(random);
		}
	}

	public static void reload() {

		for (JButton button : bingoButtons) {
			button.setEnabled(true);
			button.setBackground(new JButton().getBackground()); // Standard Background-Color
			button.setForeground(new JButton().getForeground());
			buttonTimestamps.clear();
			serverTimestamps.clear();
			hasAlreadyWon = false;
		}
	}

	public static void setSentenceActive(String sentence, long timestamp) {

		serverTimestamps.put(sentence, timestamp);
		System.out.println("ST: \"" + sentence + "\"=" + serverTimestamps.get(sentence));
		checkIfBingoSentenceActive(sentence);
		if (isPatternReached() && !hasAlreadyWon) {
			hasAlreadyWon = true;
			ClientMainThread.sendMessageToServer(
					ClientMainThread.NO_CHAT_MESSAGE + ClientMainThread.PATTERN_REACHED + ClientMainThread.name);
		}

	}

	private static boolean isPatternReached() {

		for (int i = 0; i < 9; i++) {

			if (!(bingoPatternCheckBoxes.get(i).isSelected() == bingoButtons.get(i).getBackground()
					.equals(Color.green))) {
				return false;
			}
		}
		return (!hasAlreadyWon);
	}

	public static void setBingoPattern(String message) {

		int startPos = message.indexOf("="), endPos = message.lastIndexOf("}") + 1;
		message = message.substring(startPos, endPos);
		// for: get all checkBox values
		for (int i = 0; i < 9; i++) {
			startPos = message.indexOf(":") + 1;
			endPos = message.indexOf("}");
			String value = message.substring(startPos, endPos);
			System.out.println("[" + i + "] MSG: \"" + message + "\" VALUE: " + value);
			bingoPatternCheckBoxes.get(i).setSelected(Boolean.parseBoolean(value));
			message = message.substring(endPos + 1);
		}
	}
}