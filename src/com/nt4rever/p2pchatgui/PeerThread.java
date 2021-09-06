package com.nt4rever.p2pchatgui;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.Socket;

import javax.json.Json;
import javax.json.JsonObject;

import javax.swing.JTextArea;
import javax.swing.ProgressMonitorInputStream;

public class PeerThread extends Thread {
	private BufferedReader bufferedReader;
	private JTextArea textArea;
	private InputStream ins;
	private Peer mainFrame;
	private final int BUFFER_SIZE = 100;

	public PeerThread(Socket socket, JTextArea textArea, Peer mainFrame) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.mainFrame=mainFrame;
		ins = socket.getInputStream();
		this.textArea = textArea;
	}

	@Override
	public void run() {
		boolean flag = true;
		boolean isReceivingFile = false;
		int len = 1;
		int fileSize = 1;
		String fileName = "rev";
		while (flag) {
			try {
				if (isReceivingFile) {
					FileOutputStream fos = new FileOutputStream(fileName);
					ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(null,
							"Downloading file please wait...", ins);
					BufferedInputStream bis = new BufferedInputStream(pmis);
					byte[] buffer = new byte[BUFFER_SIZE];
					int count, percent = 0;
					while ((count = bis.read(buffer)) != -1) {
						percent = percent + count;
						int p = (percent / fileSize);
						System.out.println("Downloading file  " + p + "%");
						fos.write(buffer, 0, count);
						if (p >= 100)
							break;
					}
					fos.flush();
					fos.close();
					mainFrame.showSaveFile(fileName);
					isReceivingFile = false;
					continue;
				}
				JsonObject jsonObject = Json.createReader(bufferedReader).readObject();
				if (jsonObject.containsKey("username")) {
					textArea.append("\n[" + jsonObject.getString("username") + "]:" + jsonObject.getString("message"));
				}
				if (jsonObject.containsKey("fileName")) {
					fileName = jsonObject.getString("fileName");
					len = Integer.parseInt(jsonObject.getString("len"));
					fileSize = Integer.parseInt(jsonObject.getString("size"));
					textArea.append("\nReceiving file " + fileName + "(len:" + len + ",size:" + fileSize + ")...");
					isReceivingFile = true;
				}
			} catch (Exception e) {
				flag = false;
				System.out.println(e.getMessage());
			}
		}
	}

}
