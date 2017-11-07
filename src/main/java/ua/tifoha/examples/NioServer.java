package ua.tifoha.examples;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Vitaliy Sereda on 05.11.17.
 */
public class NioServer {
	public static void main(String[] args) throws IOException {
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ExecutorService exec = Executors.newFixedThreadPool(2000);
		ssc.bind(new InetSocketAddress(8080));
		while (true) {
			final SocketChannel sc = ssc.accept();
			exec.submit(() -> Utils.process(sc));
		}
	}
}
