package Client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/*
 * Something like a chat
 * 
 * jtextarea + jtextfield + sendbutton
 * 
 * Should display all player-chat-messages, messages like "player joined/leaved the game" and Messages like "Don't Spam!"
 * 
 * Should be able to process Commands
 * 
 */
public class ClientChat extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public static JTextArea textAreaMessages;
	public static JTextField textFieldMessage;
	private JButton button_SendMessage;
	private JScrollPane scrollPaneMessages;

	// actual timestamp to wait 0.5sec
	static long last_timestamp = System.currentTimeMillis() - 500;

	final public static int LEVEL_INFO = 2;
	final public static int LEVEL_ERROR = 1;
	final public static int LEVEL_NORMAL = 0;

	public ClientChat() {
		this.setSize(800, 600);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		ClientChat.textAreaMessages = new JTextArea();
		ClientChat.textAreaMessages.setEditable(false);

		textFieldMessage = new JTextField(65); // 38
		textFieldMessage.addKeyListener(new SendPressEnterListener());

		button_SendMessage = new JButton("Senden");
		button_SendMessage.addActionListener(new SendButtonListener());

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

	public static void appendTextToChat(String message, int level) {

		if (level == LEVEL_ERROR) {
			ClientChat.textAreaMessages.append("[Error]: " + message + "\n");

		} else if (level == LEVEL_INFO) {
			ClientChat.textAreaMessages.append(message + "\n");

		} else { // LEVEL_NORMAL
			try {

				ClientChat.textAreaMessages.append(message + "\n");
				textFieldMessage.setText("");

			} catch (Exception e) {
				ClientChat.appendTextToChat(e.getMessage(), ClientChat.LEVEL_ERROR);
			}
		}

		ClientChat.textAreaMessages.setCaretPosition(ClientChat.textAreaMessages.getText().length());
	}

	static boolean checkMessage(long timestamp, String message) {
		boolean isOkay = true;

		if (message.length() < 2 || message.length() > 100) {
			ClientChat.appendTextToChat("MESSAGE_RULES: 2-100 CHARACTER", ClientChat.LEVEL_ERROR);
			isOkay = false;
		}
		if (timestamp > System.currentTimeMillis() - 500) {

			ClientChat.appendTextToChat("!!!DO NOT SPAM!!!", ClientChat.LEVEL_ERROR);
			isOkay = false;
		} else {
			last_timestamp = System.currentTimeMillis();
		}
		return isOkay;
	}

	// Listener
	public class SendPressEnterListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				String message = textFieldMessage.getText();
				if (ClientChat.checkMessage(last_timestamp, message)) {
					ClientMainThread.executeCommand(message);
					textFieldMessage.setText("");
				}
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
			ClientMainThread.executeCommand(textFieldMessage.getText());
			textFieldMessage.setText("");
		}
	}

	public static void clear() {
		textAreaMessages.setText("");

	}
}