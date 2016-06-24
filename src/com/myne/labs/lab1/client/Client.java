package com.myne.labs.lab1.client;

import java.io.*;
import java.net.*;

/**
 * ������ ��������� �������, �������� �������������, ����� ����������� ���� �
 * ���������� �� �� ������. ������ ����� �������, ������ ������� �� �����
 * ���������� �� ������� ����, ������������ ���������� ����� � ���� ����� �
 * ���������� ��������� �� ������. ������, ���������� ������� \end, ��� ����
 * ������������. ��������� �� ������� ��������� �� �����.
 *
 * 
 */
public class Client {

	public static void main(String args[]) {
		// ������ ���� ��� ���
		int port = 1160;
		String serverIp = "localhost";
		System.out.println("..start working...");
		// BufferedReader bufferRead = new BufferedReader(new
		// InputStreamReader(System.in));) {

		try {
			// �������� ������
			Socket s = new Socket(serverIp, port);
			// �������� ����������������� �����������
			// �������� ������ ��� ������.
			InputStream is = s.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			// �������� ���������� ������ ������
			// ��� ������
			OutputStream os = s.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			PrintWriter pw = new PrintWriter(osw);
			// �������� ����������������� �����������
			// �������� ������ ��� ����� � �������.
			InputStreamReader stisr = new InputStreamReader(System.in);
			BufferedReader bufConsoleReader = new BufferedReader(stisr);
			boolean repeat = true;
			while (repeat) {
				// ���������� ����� ������������
				System.out.print("Enter what send to server: ");
				String readFromConsole = bufConsoleReader.readLine();
				// �������� ������ �� ������
				pw.println(readFromConsole);
				// ������� ������
				pw.flush();

				// ����� ����� �� �������
				int counterOfStringsInFile = 0;

				if (readFromConsole.startsWith("\\get")) {

					while (true) {
						String answerFromServer = br.readLine();
						if (answerFromServer.startsWith("\\end")) {
							System.out.println("\n..end get from server");
							System.out.println("STRING IN FILE: " + counterOfStringsInFile + "\n");
							break;
						}
						System.out.println(answerFromServer);
						counterOfStringsInFile++;

					}
				} else {

					while (true) {
						String answerFromServer = br.readLine();
						if (answerFromServer.startsWith("\\end")) {
							System.out.println("\n..end get from server");
							break;
						}
						System.out.println(answerFromServer);

					}
				}

			}
			s.close();
		} catch (UnknownHostException e) {
			System.out.println("Unknown host " + args[0]);
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Socket error		in CharCountClient");
			System.exit(0);
		}
	}
}