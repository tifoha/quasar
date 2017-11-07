package ua.tifoha.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Vitaliy Sereda on 03.10.17.
 */
public class Server {
	public static void main(String[] args) {
		final byte[] response = "HTTP/1.0 200 OK\r\nDate: Fri, 31 Dec 1999 23:59:59 GMT\r\nContent-Type: text/html\r\nContent-Length: 0\r\n\r\n".getBytes(StandardCharsets.UTF_8);
		ByteBuffer writeBuffer = ByteBuffer.wrap(response);
		ByteBuffer readBuffer = ByteBuffer.allocate(8);
		ExecutorService exec = Executors.newFixedThreadPool(8);
			try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()) {
				server.bind(new InetSocketAddress("localhost", 8088));
				while (true) {
					final Future<AsynchronousSocketChannel> accept = server.accept();
//					exec.submit(() -> {
						try {
							try (AsynchronousSocketChannel channel = accept.get()) {
								while (channel != null && channel.isOpen()) {
	//								final ByteBuffer buff = ByteBuffer.allocate(32);
									readBuffer.flip();
									final Future<Integer> read = channel.read(readBuffer);
									System.out.println("Read bytes: " + read.get());
									final Future<Integer> write = channel.write(writeBuffer);
									System.out.println("Write bytes: " + write.get());
									writeBuffer.flip();
	//								writeBuffer.clear();
								}
							}

						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
//					});
				}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
