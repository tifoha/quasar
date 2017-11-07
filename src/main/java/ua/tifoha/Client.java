package ua.tifoha;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * Created by Vitaliy Sereda on 03.10.17.
 */
public class Client {
	public static void main(String[] args) throws IOException {
		try (Socket socket = new Socket("localhost", 8088)) {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.append("Hrllo");
			writer.flush();
//			writer.close();
			Reader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			CharBuffer buffer = ByteBuffer.allocate(1024).asCharBuffer();

			final int read = reader.read(buffer);
			System.out.println(buffer.toString());

		}
	}
}
