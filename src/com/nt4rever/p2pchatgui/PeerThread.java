package com.nt4rever.p2pchatgui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.json.Json;
import javax.json.JsonObject;
import javax.swing.JTextArea;

public class PeerThread extends Thread {
	private BufferedReader bufferedReader;
	private JTextArea textArea;

	public PeerThread(Socket socket, JTextArea textArea) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.textArea = textArea;
	}

	@Override
	public void run() {
		boolean flag = true;
		while (flag) {
			try {
				JsonObject jsonObject = Json.createReader(bufferedReader).readObject();
				if (jsonObject.containsKey("username")) {
					textArea.append("\n[" + jsonObject.getString("username") + "]:" + jsonObject.getString("message"));
				}
			} catch (Exception e) {
				flag = false;
				System.out.println(e.getMessage());
			}
		}
	}

}
