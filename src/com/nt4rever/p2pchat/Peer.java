package com.nt4rever.p2pchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;

import javax.json.Json;

public class Peer {

	public static void main(String[] args) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("?username and port for this peer: ");
		String[] setupValues = bufferedReader.readLine().split(" ");
		System.out.println(setupValues[0]+"|"+setupValues[1]);
		ServerThread serverThread = new ServerThread(setupValues[1]);
		serverThread.start();
		new Peer().updateListenToPeers(bufferedReader, setupValues[0], serverThread);
	}

	public void updateListenToPeers(BufferedReader bufferedReader, String username, ServerThread serverThread)
			throws IOException {
		System.out.println("?peers (hostname:port): ");
		String input = bufferedReader.readLine();
		String[] inputValues = input.split(" ");
		for (String i : inputValues) {
			String[] address = i.split(":");
			Socket socket = null;
			try {
				System.out.println("Log: add peer "+address[1]);
				socket = new Socket(address[0], Integer.valueOf(address[1]));
				new PeerThread(socket).start();
			} catch (Exception e) {
				if (socket != null) {
					socket.close();
				}
			}
		}
		communicate(bufferedReader, username, serverThread);
	}

	public void communicate(BufferedReader bufferedReader, String username, ServerThread serverThread) {
		try {
			System.out.println("Chat here (e[exit], c[change]: ");
			boolean flag = true;
			while (flag) {
				String message = bufferedReader.readLine();
				if (message.equals("c")) {
					updateListenToPeers(bufferedReader, username, serverThread);
				} else if (message.equals("e")) {
					flag = false;
					break;
				} else {
					StringWriter stringWriter = new StringWriter();
					Json.createWriter(stringWriter).writeObject(
							Json.createObjectBuilder().add("username", username).add("message", message).build());
					serverThread.sendMessage(stringWriter.toString());
				}

			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
