package com.myne.labs.lab2.server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Vector;

class UDPFileTransfer {
	// Vector fileContent;

	public static void main(String args[]) {
		System.out.println("Server started....");

		String dirname = "texts";
		File wd = new File(dirname);
		if (wd.exists() && wd.isDirectory()) {
			System.out.println("Directory with files: " + dirname);
			FileTransfer ft = new FileTransfer(wd);

			try {
				DatagramSocket sock = new DatagramSocket(1160);
				byte buff[] = new byte[1000];
				DatagramPacket dp = new DatagramPacket(buff, buff.length);

				while (true) {
					sock.receive(dp);
					InetAddress clientIP = dp.getAddress();
					int clientPort = dp.getPort();
					System.out.println("Datagram from " + clientIP.getHostAddress() + ":" + clientPort);
					UDPFTransHandler fth = new UDPFTransHandler(dp, ft);
					int err = fth.getErrorCode();
					Vector resp = fth.getResponces();
					for (Enumeration e = resp.elements(); e.hasMoreElements();) {
						DatagramPacket rsp = (DatagramPacket) e.nextElement();
						rsp.setAddress(clientIP);
						rsp.setPort(clientPort);
						sock.send(rsp);
						System.out.println("    Responce sent, error code " + err);
					}

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

class UDPFTransHandler {
	DatagramPacket request;
	FileTransfer ft;
	Vector responces;
	// We suppose that maximum size of the status line is 30 bytes.
	int maxDataSize = 970;
	// int responceNumber;
	int err;
	String errMsg;

	public UDPFTransHandler(DatagramPacket d, FileTransfer f) {
		request = d;
		ft = f;
		err = 501;
		errMsg = "";
		responces = new Vector();

		if (request.getLength() > 0) {
			parse();
		} else {
			err = 500;
			errMsg = "Illegal request format";
			String str = err + " " + errMsg + "\n\n";
			System.out.println(str);
			byte[] data = str.getBytes();
			DatagramPacket responce = new DatagramPacket(data, data.length);
			responces.addElement(responce);
		}
	}

	void parse() {
		byte[] buff = request.getData();
		String s = new String(buff);
		
		
		Vector output = new Vector();

		int eol = s.indexOf("\n");

		if ( !s.startsWith("\\")) {
			err = 500;
			errMsg = "Illegal request format";
			String str = err + " " + errMsg + "\n\n";
			// System.out.println(str);
			byte[] data = str.getBytes();
			DatagramPacket responce = new DatagramPacket(data, data.length);
			responces.addElement(responce);
		} else {
			String requestLine = s;
			System.out.println(requestLine);
			if (requestLine.startsWith("\\get")) {
				int firstsps = requestLine.indexOf(" ");
				String command = requestLine.substring(0, firstsps);
				String argument = requestLine.substring(firstsps + 1).trim();
				int arglen = argument.length();
				if (arglen > 0) {
					output = ft.getFile(argument);
					if (output != null) {
						err = 200;
						errMsg = "OK";
					} else {
						err = 402;
						errMsg = "Not Found";
					}
				} else {
					err = 401;
					errMsg = "Empty argument";
				}

			} else if (requestLine.startsWith("\\list")) {
				String ls[] = ft.list();

				for (int i = 0; i < ls.length; i++) {
					output.addElement(ls[i]);
				}
				err = 200;
				errMsg = "OK";

			} else {
				err = 400;
				errMsg = "Unknown command";
			}

			if (err != 200) {
				String str = err + " " + errMsg + "\n\n";
				// System.out.println(str);
				byte[] data = str.getBytes();
				DatagramPacket responce = new DatagramPacket(data, data.length);
				responces.addElement(responce);
			} else {
				int curString = 0;
				int firstString = 1;
				int count = 0;
				byte[] answer = new byte[1000];

				for (Enumeration e = output.elements(); e.hasMoreElements();) {
					String out = (String) e.nextElement() + "\n";
					byte[] tt = out.getBytes();
					// System.out.println(out + " " + tt.length + " count =" +
					// count);
					if (tt.length + count < maxDataSize) {
						System.arraycopy(tt, 0, answer, count, tt.length);
						count = count + tt.length;
						curString++;
					} else {
						String status = err + " " + errMsg + " begin=" + firstString + " more=" + 1 + "\n\n";
						byte[] tt1 = status.getBytes();
						byte[] data = new byte[1000];
						System.arraycopy(tt1, 0, data, 0, tt1.length);
						System.arraycopy(answer, 0, data, tt1.length, count);
						int dataLength = tt1.length + count;
						DatagramPacket responce = new DatagramPacket(data, dataLength);
						responces.addElement(responce);
						count = tt.length;
						System.arraycopy(tt, 0, answer, 0, tt.length);
						curString++;
						firstString = curString;
					}
				}
				String status = err + " " + errMsg + " begin=" + firstString + " more=" + 0 + "\n\n";
				byte[] tt1 = status.getBytes();
				byte[] data = new byte[1000];
				System.arraycopy(tt1, 0, data, 0, tt1.length);
				System.arraycopy(answer, 0, data, tt1.length, count);
				int dataLength = tt1.length + count;
				DatagramPacket responce = new DatagramPacket(data, dataLength);
				responces.addElement(responce);
				// System.out.println("Recponse = " + responces.size());
			}
		}
	}

	public int getErrorCode() {
		return err;
	}

	public Vector getResponces() {
		return responces;
	}

}
