package com.myne.labs.lab1.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

class TCPFileTransfer {
	// Vector fileContent;

	public static void main(String args[]) {

		// путь к папке
		String dirname = "texts";
		File wd = new File(dirname);
		if (wd.exists() && wd.isDirectory()) {
			System.out.println("Directory with files: " + dirname);
			FileTransfer ft = new FileTransfer(wd);

			try {
				ServerSocket s = new ServerSocket(1160);
				while (true) {
					Socket c = s.accept();
					TCPFileTransferConn trc = new TCPFileTransferConn(c, ft);
					Thread t = new Thread(trc);
					t.start();
				}
				// s.close();
			} catch (IOException e) {
				System.out.println("Error in Socket");
				System.exit(0);
			}
		} else {
			System.out.println("Directory " + dirname + " invalid");
			System.exit(-1);
		}

	}
}

class TCPFileTransferConn implements Runnable {
	Socket s;
	FileTransfer ft;

	public TCPFileTransferConn(Socket ss, FileTransfer rr) {
		s = ss;
		ft = rr;
	}

	public void run() {
		try {
			InputStream is = s.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			OutputStream os = s.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			PrintWriter pw = new PrintWriter(osw);

			boolean end = false;

			String remoteIP = s.getInetAddress().getHostAddress();
			int remotePort = s.getPort();
			System.out.println("Connection from " + remoteIP + ", port " + remotePort);

			while (!end) {
				String istr = br.readLine();
				System.out.println("Get from client: " + istr);
				String str = istr.trim();

				if (str.length() == 0) {
					pw.println("Empty string found");
					// pw.flush();
				} else if (str.startsWith("\\")) {
					// String command = str.toLowerCase();
					String command = "";
					String argument = "";
					if (str.startsWith("\\quit")) {
						pw.println("Closing connection");
						pw.flush();
						System.out.println("Connection from " + remoteIP + ", port " + remotePort + " closed");
						end = true;
					} else if (str.startsWith("\\list")) {
						String ls[] = ft.list();

						for (int i = 0; i < ls.length; i++) {
							pw.println(ls[i]);
						}

						pw.println("\\end");
						pw.flush();

					} else if (str.startsWith("\\get")) {

						int firstsps = str.indexOf(" ");
						if (firstsps != -1) {
							command = str.substring(0, firstsps);
							argument = str.substring(firstsps + 1).trim();
						}
						int arglen = argument.length();
						if (arglen > 0) {
							Vector output = ft.getFile(argument);
							if (output != null) {
								// System.out.println("Rgrep returns " +
								// output.size() + " elements");
								for (Enumeration e = output.elements(); e.hasMoreElements();) {
									String out = (String) e.nextElement();
									// System.out.println(out);
									pw.println(out);
								}
								pw.println("\\end");
								pw.flush();
							} else {
								pw.println("403 Not Found");
								pw.flush();
							}
						} else {
							pw.println("401 Empty argument");
							pw.flush();
						}
					} else {
						pw.println("400 Unknown command");
						pw.flush();
					}
				} else {
					pw.println("402 Illegal command");
					pw.flush();

				}
			}
			s.close();
		} catch (IOException e) {
			System.out.println("Error in TCPFileTransferConn");
		}
	}
}
