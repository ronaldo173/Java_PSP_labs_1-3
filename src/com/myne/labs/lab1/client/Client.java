package com.myne.labs.lab1.client;

import java.io.*;
import java.net.*;

/**
 * Клиент считывает команды, вводимые пользователем, через стандарнтый ввод и
 * пересылает их на сервер. Приняв ответ сервера, клиент выводит на экран
 * полученный от сервера файл, подсчитывает количество строк в этом файле и
 * отображает результат на экране. Строка, содержащая команду \end, при этом
 * игнорируется. Сообщения об ошибках выводятся на экран.
 *
 * 
 */
public class Client {

	public static void main(String args[]) {
		// задали порт для тсп
		int port = 1160;
		String serverIp = "localhost";
		System.out.println("..start working...");
		// BufferedReader bufferRead = new BufferedReader(new
		// InputStreamReader(System.in));) {

		try {
			// Создание сокета
			Socket s = new Socket(serverIp, port);
			// Создание буферизированного символьного
			// входного потока для сокета.
			InputStream is = s.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			// Создание текстового потока вывода
			// для сокета
			OutputStream os = s.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			PrintWriter pw = new PrintWriter(osw);
			// Создание буферизированного символьного
			// входного потока для ввода с консоли.
			InputStreamReader stisr = new InputStreamReader(System.in);
			BufferedReader bufConsoleReader = new BufferedReader(stisr);
			boolean repeat = true;
			while (repeat) {
				// Считывание ввода пользователя
				System.out.print("Enter what send to server: ");
				String readFromConsole = bufConsoleReader.readLine();
				// Отправка строки на сервер
				pw.println(readFromConsole);
				// Очистка буфера
				pw.flush();

				// Прием строк от сервера
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