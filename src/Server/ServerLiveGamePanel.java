package Server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import GUI.ShadowLabel;
import Main.BingoMain;

/*
 * ServerLiveGamePanel
 * 
 * 	*Tell all Clients if a Bingo Sentence is ACTIVE
 *  *Show Server Thread
 *  *Show 3x3 Button-Raster for activating sentences
 */
public class ServerLiveGamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static ArrayList<JButton> bingoButtons = new ArrayList<JButton>();
	private static JPanel contentPane = new JPanel();
	private JPanel buttonPane = new JPanel();
	private JPanel bingoPatternPanel;
	private static ArrayList<JCheckBox> bingoPatternCheckBoxes;
	private ShadowLabel bingoPatternTitle;

	public ServerLiveGamePanel(Dimension size) {
		contentPane.setLayout(new BorderLayout());
		buttonPane.setLayout(new GridLayout(3, 3, 1, 1));
		buttonPane.setPreferredSize(new Dimension(304, 304));

		for (int i = 0; i < 9; i++) {

			JButton exampleButton = new JButton("");
			exampleButton.setPreferredSize(new Dimension(100, 100));
			exampleButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Nachricht an Clients mit dem jeweiligen BingoText Senden
					// Client sollen den timestamp abspeichern, und falls der
					// User im Zeitraum(30 sekunden vor oder nach dem timestamp)
					// auf genau den Button geklickt hat, soll bei diesem Client
					// der jeweilige Button grün gefärbt werden.
					String nachricht = ServerMainThread.NO_CHAT_MESSAGE + ServerMainThread.BINGO_SENTENCE_ACTIVE
							+ "{MSG=" + ((JButton) e.getSource()).getText() + "}";
					ServerMainThread.sendToAllClients(nachricht);
					// System.out.println(nachricht);
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

	public static void setConsole(boolean visible) {
		if (visible) {
			contentPane.add(BingoMain.hostServerConsole, BorderLayout.PAGE_END);
		} else {
			contentPane.remove(BingoMain.hostServerConsole);
		}
	}

	public static void setBingoSentences(ArrayList<String> sentences) {

		for (String sentence : sentences) {

			JButton temp = bingoButtons.get(sentences.indexOf(sentence));

			sentence = "<html>" + sentence + "</html>";
			temp.setText(sentence);
		}
	}

	public static void setBingoPattern(String message) {

		int startPos = message.indexOf("="), endPos = message.lastIndexOf("}") + 1;
		message = message.substring(startPos, endPos);
		// for: get all checkBox values
		for (int i = 0; i < 9; i++) {
			startPos = message.indexOf(":") + 1;
			endPos = message.indexOf("}");
			String value = message.substring(startPos, endPos);
			bingoPatternCheckBoxes.get(i).setSelected(Boolean.parseBoolean(value));
			message = message.substring(endPos + 1);
		}
	}
}