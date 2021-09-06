package com.nt4rever.p2pchatgui;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.json.Json;

public class ServerThread extends Thread {
	private final int BUFFER_SIZE = 100;
	private ServerSocket serverSocket;
	private Vector<ServerThreadThread> serverThreadThreads = new Vector<ServerThreadThread>();

	public ServerThread(String portNum) throws IOException {
		serverSocket = new ServerSocket(Integer.valueOf(portNum));
	}

	@Override
	public void run() {
		try {
			while (true) {
				Socket socket = serverSocket.accept();
				ServerThreadThread serverThreadThread = new ServerThreadThread(socket, this);
				serverThreadThreads.add(serverThreadThread);
				System.out.println("Log: accept " + socket);
				serverThreadThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Vector<ServerThreadThread> getServerThreadThreads() {
		return serverThreadThreads;
	}

	public void sendMessage(String message) {
		try {
			serverThreadThreads.forEach(t -> t.getPrintWriter().println(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendFile(String fileName, String filePath) {
		try {
			File fileInfo = new File(filePath);
			int len = (int) fileInfo.length();
			int fileSize = (int) Math.ceil(len / BUFFER_SIZE);
			StringWriter stringWriter = new StringWriter();
			Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder().add("fileName", fileName)
					.add("len", String.valueOf(len)).add("size", String.valueOf(fileSize)).build());
			serverThreadThreads.forEach(t -> {
				t.getPrintWriter().println(stringWriter.toString());
//				t.getPrintWriter().close();
				try {
					t.sendFile(filePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
