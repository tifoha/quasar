package ua.tifoha.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Vitaliy Sereda on 05.11.17.
 */
public class NastyChamp {
	public static void main(String[] args) throws IOException {
		for (int i = 0; i < 30000; i++) {
			try {
				new Socket("localhost", 8080);
				System.out.println(i);
			} catch (IOException e) {
				System.out.println("Could not connect: " + e);
			}
		}
		new BufferedReader(new InputStreamReader(System.in)).readLine();
	}
}
