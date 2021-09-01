package com.nt4rever.p2pchatgui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.json.Json;
import javax.swing.JButton;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class Peer extends JFrame {

	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextField txtPeers;
	private JTextArea textArea;
	private JTextArea txtLog;
	private String username;
	private String port;

	public Peer(String username, String port) throws IOException {
		this.username = username;
		this.port = port;
		setTitle(username);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 684, 492);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 11, 435, 391);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		txtMessage = new JTextField();
		txtMessage.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtMessage.setBounds(20, 413, 316, 29);
		contentPane.add(txtMessage);
		txtMessage.setColumns(10);

		JButton btnSend = new JButton("Send");
		btnSend.setBounds(346, 413, 109, 29);
		contentPane.add(btnSend);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(481, 11, 170, 179);
		contentPane.add(scrollPane_1);

		txtLog = new JTextArea();
		scrollPane_1.setViewportView(txtLog);
		txtLog.setText("Log:");
		txtLog.append("\n[" + getLogTime() + "]" + " join (" + username + ":" + port + ")");

		txtPeers = new JTextField();
		txtPeers.setText("localhost:8888");
		txtPeers.setBounds(481, 241, 170, 29);
		contentPane.add(txtPeers);
		txtPeers.setColumns(10);

		JLabel lblNewLabel = new JLabel("Add peers (ip:port)");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(481, 201, 177, 29);
		contentPane.add(lblNewLabel);

		JButton btnAdd = new JButton("Add");
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnAdd.setBounds(481, 281, 170, 29);
		contentPane.add(btnAdd);

		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnExit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnExit.setBounds(481, 321, 170, 29);
		contentPane.add(btnExit);
		setVisible(true);

		ServerThread serverThread = new ServerThread(port);
		serverThread.start();
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String peers = txtPeers.getText();
				if (!peers.isEmpty()) {
					try {
						updateListenToPeers(peers);
						txtPeers.setText("");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			}
		});

		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String message = txtMessage.getText();
				if (!message.isEmpty()) {
					communicate(message, serverThread);
					textArea.append("\n[You]:" + message);
					txtMessage.setText("");
				}

			}
		});
		
		btnExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}

	public void updateListenToPeers(String peers) throws IOException {
		String[] inputValues = peers.split(" ");
		for (String i : inputValues) {
			String[] address = i.split(":");
			Socket socket = null;
			try {
				txtLog.append("\n[" + getLogTime() + "]" + " add peer (" + i + ")");
				socket = new Socket(address[0], Integer.valueOf(address[1]));
				new PeerThread(socket, textArea).start();
			} catch (Exception e) {
				e.printStackTrace();
				if (socket != null) {
					socket.close();
				}
			}
		}
	}

	public void communicate(String message, ServerThread serverThread) {
		try {
			StringWriter stringWriter = new StringWriter();
			Json.createWriter(stringWriter)
					.writeObject(Json.createObjectBuilder().add("username", username).add("message", message).build());
			serverThread.sendMessage(stringWriter.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getLogTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return formatter.format(date);
	}
}
