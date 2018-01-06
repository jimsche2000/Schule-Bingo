package Hauptmenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import BingoToolkit.ImageLoader;
import Client.ClientChat;
import Client.ClientMainThread;
import GUI.ShadowLabel;
import Server.ServerAttributes;

public class JoinServerPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static JPanel contentPane;
	private static JTextField textField;
	private static JTable table;
	private static JTableHeader th;
	private static ArrayList<JButton> connectButtons;
	private JButton skipBack;
	private ShadowLabel lblNewLabel, lblNewLabel_1, lblServerliste;
	private static ShadowLabel nameAlreadyTaken;
	private static ImageIcon lanIcon = ImageLoader.loadIcon("lan_icon.png", 40, 40);
	private ActionListener action = new Action();

	public JoinServerPanel(Dimension size) {

		this.setSize(size);
		this.setLayout(new BorderLayout());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);

		lblNewLabel = new ShadowLabel("BINGO - Client", 36, 250, 40);
		lblNewLabel.setFont(new Font("Source Sans Pro Semibold", Font.BOLD, 40));
		lblNewLabel.setBounds(275, 0, 250, 49);
		contentPane.add(lblNewLabel);

		lblNewLabel_1 = new ShadowLabel("Name", 25, 213, 31);
		lblNewLabel_1.setFont(new Font("Source Sans Pro Semibold", Font.BOLD, 25));
		lblNewLabel_1.setBounds(44, 65, 213, 31);
		contentPane.add(lblNewLabel_1);

		textField = new JTextField("Anonymous");
		textField.setBounds(44, 100, 213, 25);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				clearNameAlreadyTakenLabel();
			}

			public void removeUpdate(DocumentEvent e) {
				clearNameAlreadyTakenLabel();
			}

			public void insertUpdate(DocumentEvent e) {
				clearNameAlreadyTakenLabel();
			}

			public void clearNameAlreadyTakenLabel() {
				nameAlreadyTaken.setText("");
			}
		});

		lblServerliste = new ShadowLabel("Server-Liste", 25, 213, 31);
		lblServerliste.setBounds(275, 65, 213, 31);
		contentPane.add(lblServerliste);

		nameAlreadyTaken = new ShadowLabel("", 13, 213, 31);
		nameAlreadyTaken.setBounds(44, 108, 213, 40);
		nameAlreadyTaken.setForeground(Color.red);
		contentPane.add(nameAlreadyTaken);

		connectButtons = new ArrayList<JButton>();

		try {
			String column_names[] = { "Server-Name", "IP-Address", "User-Count" };
			table = new JTable(new DefaultTableModel(column_names, 1));
			th = table.getTableHeader();

			th.setBounds(275, 100, 400, 20);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setBounds(275, 120, 400, 300);
			table.setEnabled(false);

			table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
			TableColumnModel colModel = table.getColumnModel();
			colModel.getColumn(0).setPreferredWidth(150);
			colModel.getColumn(1).setPreferredWidth(60);
			colModel.getColumn(2).setPreferredWidth(30);

			table.setSize(table.getWidth(), table.getRowHeight());

		} catch (Exception e) {
			ClientChat.appendTextToChat(e.getMessage(), ClientChat.LEVEL_ERROR);
		}

		skipBack = new JButton("Zurück");
		skipBack.setBounds(25, 300, 100, 40);
		skipBack.addActionListener(action);
		skipBack.setActionCommand("SKIP_BACK");
		contentPane.add(skipBack);
		contentPane.add(th);
		contentPane.add(table);
		this.add(contentPane, BorderLayout.CENTER);
		contentPane.setOpaque(false);
		this.setOpaque(false);

	}

	public void startAutoReloadServerTableList() {

		Thread t = new Thread(new ReloadServerTableThread());
		t.start();

	}

	private static void reloadServerTableList() {
		try {
			contentPane.remove(table);
			contentPane.remove(th);
			ArrayList<ServerAttributes> serverlist = ClientMainThread.serverlist;

			String column_names[] = { "Server-Name", "IP-Address", "User-Count" };
			table = new JTable(new DefaultTableModel(column_names, serverlist.size()));
			th = table.getTableHeader();

			th.setBounds(275, 100, 400, 20);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setBounds(275, 120, 400, 300);
			table.setEnabled(false);

			table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
			table.setRowHeight(40);

			TableColumnModel colModel = table.getColumnModel();
			colModel.getColumn(0).setPreferredWidth(150);
			colModel.getColumn(1).setPreferredWidth(60);
			colModel.getColumn(2).setPreferredWidth(30);

			if (connectButtons.size() > 0) {
				for (JButton button : connectButtons) {

					if (contentPane.getComponentAt(button.getLocation()) != null)
						contentPane.remove(button);
				}
				connectButtons.clear();
			}
			int buttonCount = 0, x = table.getLocation().x + table.getWidth(), y = 120, width = table.getRowHeight(),
					height = table.getRowHeight();
			for (int i = 0; i < serverlist.size(); i++) {
				// row = i; col = self-count
				ServerAttributes temp = serverlist.get(i);

				table.getModel().setValueAt(temp.getName(), i, 0);
				table.getModel().setValueAt(temp.getIP(), i, 1);
				table.getModel().setValueAt(temp.getUser_count(), i, 2);

				JButton templateConnectButton = new JButton();
				templateConnectButton.setToolTipText("connect");
				templateConnectButton.setIcon(lanIcon);
				templateConnectButton.setBounds(x, y, width, height);
				templateConnectButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String actionCommand = e.getActionCommand();

						if ("CONNECT_TO_SERVER"
								.equals(new String(actionCommand.substring(0, e.getActionCommand().length() - 2)))) {
							int buttonID = Integer.valueOf(actionCommand.substring(actionCommand.length() - 2));
							ClientMainThread.name = textField.getText();
							ClientMainThread.connect_Server = serverlist.get(buttonID);
							boolean connected = ClientMainThread.connectToServer();
							if (connected) { // Verbindung hergestellt
								ClientMainThread.shouldSearchForServer = false;

							} else { // Verbindung nicht hergestellt

							}
						}
					}
				});
				String buttonID = String.valueOf(buttonCount);
				if (buttonCount < 10)
					buttonID = "0" + buttonID;

				templateConnectButton.setActionCommand("CONNECT_TO_SERVER" + buttonID);
				y += height;
				connectButtons.add(templateConnectButton);
				contentPane.add(templateConnectButton);
				buttonCount++;
			}
			table.setSize(table.getWidth(), table.getRowHeight() * serverlist.size());
			contentPane.add(th);
			contentPane.add(table);
			contentPane.repaint();

		} catch (Exception e) {

			ClientChat.appendTextToChat(e.getMessage(), ClientChat.ERROR);
		}
	}

	private class Action implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getActionCommand().equals("SKIP_BACK")) {

				BingoFrame.showPane(BingoFrame.MID_BUTTON_PANEL);
			}
		}
	}

	private static class ReloadServerTableThread implements Runnable {

		public void run() {
			while (true) {
				if (ClientMainThread.shouldSearchForServer) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						ClientMainThread.appendTextMessages(e.getMessage(), ClientChat.LEVEL_ERROR);
					}
					reloadServerTableList();
				}
			}
		}
	}

	public static void nameAccepted(boolean accepted) {

		if (accepted) {
			BingoFrame.showPane(BingoFrame.CLIENT_CONTROL_PANEL);
		} else {
			ClientMainThread.shouldSearchForServer = true;
			nameAlreadyTaken.setText("Dieser Name ist bereits vergeben.");
		}
	}
}