package Server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import Main.BingoMain;

/*
 * Something like a chat
 * 
 * jtextarea + jtextfield + sendbutton
 * 
 * Should display all player-chat-messages, messages like "player joined/leaved the game" and Server-Errors
 * 
 * Should be able to process Commands
 * 
 */
public class ServerConsole extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public static JTextArea textAreaMessages;
	public static JTextField textFieldMessage;
	private JButton button_SendMessage;
	private JScrollPane scrollPaneMessages;
	private JCheckBox networkTrafficCheckBox;

	final static int LEVEL_INFO = 2;
	final static int LEVEL_ERROR = 1;
	final static int LEVEL_NORMAL = 0;

	public ServerConsole() {
		this.setSize(800, 600);
		this.setMinimumSize(new Dimension(870, 300));
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		ServerConsole.textAreaMessages = new JTextArea();
		ServerConsole.textAreaMessages.setEditable(false);

		textFieldMessage = new JTextField(65); // 38
		textFieldMessage.addKeyListener(new SendPressEnterListener());

		button_SendMessage = new JButton("Senden");
		button_SendMessage.addActionListener(new SendButtonListener());

		networkTrafficCheckBox = new JCheckBox("Hintergrund Nachrichten des Netzwerks anzeigen");
		networkTrafficCheckBox.addActionListener(new JCheckBoxListener());

		contentPane.add(networkTrafficCheckBox, BorderLayout.PAGE_START);

		// Scrollbalken zur textArea hinzufügen
		scrollPaneMessages = new JScrollPane(textAreaMessages);
		scrollPaneMessages.setPreferredSize(new Dimension(870, 300));
		scrollPaneMessages.setMinimumSize(new Dimension(870, 300));
		scrollPaneMessages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneMessages.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		contentPane.add(scrollPaneMessages, BorderLayout.CENTER);
		JPanel p = new JPanel();
		p.add(textFieldMessage);
		p.add(button_SendMessage);
		contentPane.add(p, BorderLayout.PAGE_END);
		this.add(BorderLayout.CENTER, contentPane);
	}

	public static void appendTextToConsole(String message, int level) {

		if (level == LEVEL_ERROR) {
			ServerConsole.textAreaMessages.append("[Error]: " + message + "\n");

		} else if (level == LEVEL_INFO) {
			ServerConsole.textAreaMessages.append("[Info]: " + message + "\n");

		} else {

			ServerConsole.textAreaMessages.append("[Message]: " + message + "\n");
		}
		ServerConsole.textAreaMessages.setCaretPosition(ServerConsole.textAreaMessages.getText().length());
	}

	// Listener
	public class SendPressEnterListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				ServerMainThread.executeCommand(textFieldMessage.getText());
				textFieldMessage.setText("");
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}
	}

	public class SendButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ServerMainThread.executeCommand(textFieldMessage.getText());
			textFieldMessage.setText("");
		}
	}

	public class JCheckBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			BingoMain.showServerNetworkInformation = networkTrafficCheckBox.isSelected();
		}
	}
}