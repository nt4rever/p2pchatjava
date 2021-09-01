package com.nt4rever.p2pchatgui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

import javax.swing.JTextField;
import javax.swing.JButton;

public class Application extends JFrame {

	private JPanel contentPane;
	private JTextField txtUsername;
	private JTextField txtPort;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application frame = new Application();
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
	public Application() {
		setTitle("P2P Chat by nt4rever");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 294, 223);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lbUsername = new JLabel("username:");
		lbUsername.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		lbUsername.setBounds(10, 35, 82, 28);
		contentPane.add(lbUsername);

		txtUsername = new JTextField();
		txtUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtUsername.setBounds(105, 35, 127, 28);
		contentPane.add(txtUsername);
		txtUsername.setColumns(10);

		JLabel lbPort = new JLabel("port");
		lbPort.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		lbPort.setBounds(10, 77, 82, 28);
		contentPane.add(lbPort);

		txtPort = new JTextField();
		txtPort.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtPort.setColumns(10);
		txtPort.setBounds(105, 77, 127, 28);
		contentPane.add(txtPort);

		JButton btnJoin = new JButton("Join");
		btnJoin.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnJoin.setBounds(115, 126, 109, 28);
		contentPane.add(btnJoin);

		Random rand = new Random();
		int value = rand.nextInt(1000) + 5000;

		txtPort.setText(String.valueOf(value));
		btnJoin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!txtUsername.getText().isEmpty() && !txtPort.getText().isEmpty()) {
					dispose();
					try {
						new Peer(txtUsername.getText(), txtPort.getText());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					System.out.println("Fill username and port!");
				}
			}
		});

	}
}
