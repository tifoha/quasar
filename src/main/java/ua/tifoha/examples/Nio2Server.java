package ua.tifoha.examples;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Vitaliy Sereda on 05.11.17.
 */
public class Nio2Server {
	public static void main(String[] args) throws IOException {
		Collection<SocketChannel> sockets = new HashSet<>();
		ServerSocketChannel ssc = ServerSocketChannel.open();
//		ExecutorService exec = Executors.newFixedThreadPool(1000);
		ssc.bind(new InetSocketAddress(8080));
		ssc.configureBlocking(false);
		while (true) {
			SocketChannel sc = null;
			try {
				sc = ssc.accept();
			} catch (IOException e) {
				System.out.println("Connection error: " + e);
			}

			if (sc != null) {
				sc.configureBlocking(false);
				sockets.add(sc);
			}
			for (SocketChannel socket : sockets) {
				ByteBuffer buf = ByteBuffer.allocateDirect(16);
				final int read = socket.read(buf);
				if (read == -1) {
					sockets.remove(socket);
				} else if (read > 0) {
					buf.flip();
					for (int i = 0; i < buf.limit(); i++) {
						buf.put(i, (byte) Utils.transmografy(buf.get(i)));
					}
					socket.write(buf);
					buf.clear();
				}
			}
		}
	}
}
