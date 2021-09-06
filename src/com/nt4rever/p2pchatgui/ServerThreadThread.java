package com.nt4rever.p2pchatgui;

import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ServerThreadThread extends Thread {
	private ServerThread serverThread;
	private Socket socket;
	private PrintWriter printWriter;
	private DataOutputStream dos;
	private final int BUFFER_SIZE = 100;

	public ServerThreadThread(Socket socket, ServerThread serverThread) throws IOException {
		this.socket = socket;
		this.serverThread = serverThread;
		dos = new DataOutputStream(socket.getOutputStream());
	}

	@Override
	public void run() {
		try {
			this.printWriter = new PrintWriter(socket.getOutputStream(), true);
		} catch (Exception e) {
			serverThread.getServerThreadThreads().remove(this);
			e.printStackTrace();
			System.out.println("remove thread");
			interrupt();
		}
	}

	public PrintWriter getPrintWriter() {
		return printWriter;
	}

	public void sendFile(String pathFile) throws IOException {
		File filename = new File(pathFile);
		int len = (int) filename.length();
		int filesize = (int) Math.ceil(len / BUFFER_SIZE);
		InputStream input = new FileInputStream(filename);
		BufferedInputStream bis = new BufferedInputStream(input);
		byte[] buffer = new byte[BUFFER_SIZE];
		int count, percent = 0;
		while ((count = bis.read(buffer)) > 0) {
			percent = percent + count;
			int p = (percent / filesize);
			System.out.println(p + "%");
			dos.write(buffer, 0, count);
		}
		dos.flush();
	}

}
