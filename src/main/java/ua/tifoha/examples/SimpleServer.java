package ua.tifoha.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Vitaliy Sereda on 02.11.17.
 */
public class SimpleServer {
	public static void main(String[] args) throws IOException {
//		ExecutorService exec = Executors.newCachedThreadPool();
		ExecutorService exec = Executors.newFixedThreadPool(2000);
		try (ServerSocket ss = new ServerSocket(8080)) {
			while (true) {
				final Socket s = ss.accept();
				exec.submit(() -> Utils.process(s));
			}
		}
	}

}
