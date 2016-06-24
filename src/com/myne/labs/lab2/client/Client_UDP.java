package com.myne.labs.lab2.client;

//����������� ��������� Java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Client_UDP {

	public static void main(String args[]) {
		System.out.println("Client started....");

		int port = 1160;
		String server = "localhost";

		try {
			// �������� UDP-������
			DatagramSocket sock = new DatagramSocket();

			// ��������� �������� �� �������� � �������
			sock.setSoTimeout(10000);

			// �������� ����������������� �����������
			// �������� ������ ��� ����� � �������.
			InputStreamReader stisr = new InputStreamReader(System.in);
			BufferedReader bufConsoleReader = new BufferedReader(stisr);

			String strFromConsole = "";
			System.out.println("Ented text for sending to server: ");

			while ((strFromConsole = bufConsoleReader.readLine()) != null) {
				System.out.println("Send to server: " + strFromConsole);
				int size = 0;

				// �������� ���������� ��� ��������
				byte[] byteDataForSend = strFromConsole.getBytes();
				DatagramPacket dp = new DatagramPacket(byteDataForSend, byteDataForSend.length);

				// ��������� ������
				// � ����� ���������� ����������
				InetAddress ip = InetAddress.getByName(server);
				dp.setAddress(ip);
				dp.setPort(port);

				// �������� ����������
				sock.send(dp);

				// �������� ���������� ��� ������
				byte[] buf = new byte[1000];
				DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
				List<String> result = new ArrayList<>();

				try {
					// ����� ����������
					sock.receive(datagramPacket);

					// ���������� ������ � ����� �� �����
					/**
					 * ���� ������ ������ �����- ���� 1000 ����, �� �����������
					 * �� ��������� ���������. ����- �� ��������� � ������
					 * ���������� ������ ����� ���: �200 OK begin=number1
					 * more=number2�, ��� number1 � ����� ������� ������� �
					 * ������ ����������, number2 ����� 0, ���� ������
					 * ���������� �������� ��������� ����������� ������, � 1 �
					 * ���- ������ ������.
					 * 
					 */
					buf = datagramPacket.getData();
					String date = new String(buf);
					String[] splitedFirstPart = date.split("\n");
					for (String string : splitedFirstPart) {
						result.add(string);
					}
					size += result.size() - 3;

					// check if it last datagram
					String noMoreDataPackets = " more=0";
					String moreDataPackets = "200 OK begin=1 more=1";
					if (date.contains(noMoreDataPackets)) {
						System.out.println("\nLAST PACKET\n");
					} else if (date.contains(moreDataPackets)) {

						while (true) {
							System.out.println("\nMOREMORE\n");
							byte[] buf2 = new byte[1000];
							DatagramPacket datagramPacket2 = new DatagramPacket(buf2, buf2.length);
							sock.receive(datagramPacket2);
							buf2 = datagramPacket2.getData();
							String date2 = new String(buf2);
							String[] splitedFirstPart2 = date2.split("\n");
							for (String string : splitedFirstPart2) {
								result.add(string);
							}
							size += splitedFirstPart2.length - 3;
							if (date2.contains(noMoreDataPackets)) {
								break;
							}

						}
					}

					for (String string : result) {
						System.out.println(string);
					}
					if (strFromConsole.startsWith("\\get") && result.size() > 0) {
						System.out.println("���������� ����� � �����: " + size);
					}

				} catch (SocketTimeoutException e) {
					// ��������� �������� ������ ����������
					System.out.println("Request timeout");
					System.exit(-1);
				}
				System.out.println("Ented text for sending to server: ");

			}

		} catch (IOException e) {
			// ��������� ������ �������� ������
			System.out.println("I/O Error");
			System.exit(-1);
		}
	}

}
