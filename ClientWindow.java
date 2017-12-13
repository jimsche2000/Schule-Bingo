import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;

public class ClientWindow extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientWindow frame = new ClientWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientWindow() {
		setTitle("BINGO - Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("BINGO - Client");
		lblNewLabel.setFont(new Font("Source Sans Pro Semibold", Font.BOLD, 40));
		lblNewLabel.setBounds(275, 11, 250, 49);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Name");
		lblNewLabel_1.setFont(new Font("Source Sans Pro Semibold", Font.BOLD, 25));
		lblNewLabel_1.setBounds(44, 65, 213, 31);
		contentPane.add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(44, 107, 213, 65);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblServerliste = new JLabel("Server-Liste");
		lblServerliste.setFont(new Font("Source Sans Pro Semibold", Font.BOLD, 25));
		lblServerliste.setBounds(275, 65, 139, 31);
		contentPane.add(lblServerliste);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setBounds(275, 107, 238, 200);

		contentPane.add(table);
		
		JButton btnRefresh = new JButton("refresh");
		btnRefresh.setBounds(424, 71, 89, 23);
		contentPane.add(btnRefresh);
	}
}
