package com.nt4rever.p2pchatgui;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;


public class ServerThread extends Thread{
	private ServerSocket serverSocket;
	private Set<ServerThreadThread> serverThreadThreads = new HashSet<ServerThreadThread>();

	public ServerThread(String portNum) throws IOException {
		serverSocket = new ServerSocket(Integer.valueOf(portNum));

	}

	@Override
	public void run() {
		try {
			while (true) {
				ServerThreadThread serverThreadThread = new ServerThreadThread(serverSocket.accept(), this);
				serverThreadThreads.add(serverThreadThread);
				System.out.println("Log: accept");
				serverThreadThread.start();

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public Set<ServerThreadThread> getServerThreadThreads() {
		return serverThreadThreads;
	}

	public void sendMessage(String message) {
		try {
			serverThreadThreads.forEach(t -> t.getPrintWriter().println(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
