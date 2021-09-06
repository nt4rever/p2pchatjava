package com.nt4rever.p2pchatgui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.json.Json;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.io.FileOutputStream;

public class Peer extends JFrame {
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextField txtPeers;
	private JTextArea textArea;
	private JTextArea txtLog;
	private String username;
	private JTextField txtFile;
	private Peer mainFrame;

	public Peer(String username, String port) throws IOException {
		this.username = username;
		this.mainFrame = this;
		setTitle(username);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 767, 492);
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
		scrollPane_1.setBounds(481, 11, 248, 179);
		contentPane.add(scrollPane_1);

		txtLog = new JTextArea();
		scrollPane_1.setViewportView(txtLog);
		txtLog.setText("Log:");
		txtLog.append("\n[" + getLogTime() + "]" + " join (" + username + ":" + port + ")");

		txtPeers = new JTextField();
		txtPeers.setText("localhost:8888");
		txtPeers.setBounds(513, 241, 170, 29);
		contentPane.add(txtPeers);
		txtPeers.setColumns(10);

		JLabel lblNewLabel = new JLabel("Add peers (ip:port)");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(506, 201, 177, 29);
		contentPane.add(lblNewLabel);

		JButton btnAdd = new JButton("Add");
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnAdd.setBounds(513, 281, 170, 29);
		contentPane.add(btnAdd);

		JButton btnExit = new JButton("Exit");

		btnExit.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnExit.setBounds(513, 413, 170, 29);
		contentPane.add(btnExit);

		txtFile = new JTextField();
		txtFile.setColumns(10);
		txtFile.setBounds(513, 324, 170, 29);
		contentPane.add(txtFile);

		JButton btnSendFile = new JButton("Send File");
		btnSendFile.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnSendFile.setBounds(513, 364, 170, 29);
		contentPane.add(btnSendFile);
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
						txtPeers.setText("localhost:");
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

		btnSendFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				txtFile.setText("");
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					String nameFile = fileChooser.getSelectedFile().getName();
					String pathFile = (fileChooser.getSelectedFile().getAbsolutePath());
					txtFile.setText(pathFile);
					txtLog.append("\nSendFile: " + txtFile.getText());
					txtLog.append("\nPath: " + pathFile);
					sendFile(nameFile, pathFile, serverThread);
				}

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
				new PeerThread(socket, textArea, mainFrame).start();
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

	public void sendFile(String nameFile, String pathFile, ServerThread serverThread) {
		serverThread.sendFile(nameFile, pathFile);
		textArea.append("\nSending file " + nameFile + "...");
	}

	public String getLogTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return formatter.format(date);
	}

	public void showSaveFile(String nameFileReceive) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fileChooser.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = new File(fileChooser.getSelectedFile().getAbsolutePath() + "/" + nameFileReceive);
			if (!file.exists()) {
				try {
					file.createNewFile();
					Thread.sleep(1000);
					InputStream input = new FileInputStream(nameFileReceive);
					OutputStream output = new FileOutputStream(file.getAbsolutePath());
					copyFileReceive(input, output, nameFileReceive);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void copyFileReceive(InputStream inputStr, OutputStream outputStr, String path) throws IOException {
		byte[] buffer = new byte[1024];
		int lenght;
		while ((lenght = inputStr.read(buffer)) > 0) {
			outputStr.write(buffer, 0, lenght);
		}
		inputStr.close();
		outputStr.close();
		File fileTemp = new File(path);
		if (fileTemp.exists()) {
			fileTemp.delete();
		}
		textArea.append("\nReceive file success!");
	}
}
