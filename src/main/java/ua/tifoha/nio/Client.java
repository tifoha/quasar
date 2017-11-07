package ua.tifoha.nio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Vitaliy Sereda on 03.10.17.
 */
public class Client {
	public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
		ioClient();
	}

	private static void ioClient() {
		try (final Socket s = new Socket("localhost", 8088)) {
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			writer.write("hello");
			writer.flush();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
//			reader.lines().forEach(System.out::println);
			System.out.println(reader.readLine());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void asyncClient() throws IOException, InterruptedException, ExecutionException {
		final ByteBuffer readBuffer = ByteBuffer.allocate(1024);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String message = reader.readLine();
		while (!"exit".equals(message)) {
			try (AsynchronousSocketChannel client = AsynchronousSocketChannel.open()) {
			InetSocketAddress hostAddress = new InetSocketAddress("localhost", 8088);
			Future<Void> future = client.connect(hostAddress);
			future.get();
				final byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
				final ByteBuffer buffer = ByteBuffer.wrap(bytes);
				final Future<Integer> write = client.write(buffer);

				write.get();
				readBuffer.clear();
				final Future<Integer> read = client.read(readBuffer);
				System.out.println(read.get());
				final String echo = new String(readBuffer.array()).trim();
				System.out.println(echo);
				message = reader.readLine();
			}
		}
	}
}
