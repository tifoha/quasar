package ua.tifoha.examples;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vitaliy Sereda on 03.11.17.
 */
public class Utils {

	public static int transmografy(int c) {
		if (Character.isLetter(c)) {
			return c ^ ' ';
		}
		return c;
	}

	public static void process(Socket s) {
		try (final InputStream is = s.getInputStream();
			 final OutputStream os = s.getOutputStream()) {
			System.out.println("Connected: " + s);
			int read;
			while ((read = is.read()) != -1) {
				os.write(Utils.transmografy(read));
			}
		} catch (IOException e) {
			System.err.println("Connection fail");
		}
	}

	public static void process(SocketChannel sc) {
		ByteBuffer buf = ByteBuffer.allocateDirect(16);
		try {
			while (sc.read(buf) != -1) {
				buf.flip();
				for (int i = 0; i < buf.limit(); i++) {
					buf.put(i, (byte) transmografy(buf.get(i)));
				}
				sc.write(buf);
				buf.clear();
			}
		} catch (IOException e) {
			System.err.println("Connection fail");
		}
	}

	public static void main(String[] args) {
		String requestString = "POST /cgi-bin/process.cgi HTTP/1.1\n\r" +
				"User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\n\r" +
				"Host: www.tutorialspoint.com\n\r" +
				"Content-Type: text/xml; charset=utf-8\n\r" +
				"Content-Length: length\n\r" +
				"Accept-Language: en-us\n\r" +
				"Accept-Encoding: gzip, deflate\n\r" +
				"Connection: Keep-Alive\n\r" +
				"\n\r" +
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\r" +
				"<string xmlns=\"http://clearforest.com/\">string</string>";
		final ByteBuffer bbuf = ByteBuffer.allocateDirect(1024);
		bbuf.put(requestString.getBytes(StandardCharsets.UTF_8));
		bbuf.rewind();
		final CharBuffer cbuf = StandardCharsets.UTF_8.decode(bbuf);
		final char SPACE = ' ';
		String method = readTo(cbuf, " ");
		String requestUri = readTo(cbuf, " ");
		String httpVersion = readTo(cbuf, "\n\r");

////		readTo(cbuf, " ");
//
//		for (int i = 0; i < Math.min(7, cbuf.limit()); i++) {
//			if (cbuf.get(i) == SPACE) {
//				method = cbuf.subSequence(0, i).toString();
//				cbuf.position(++i);
//				break;
//			}
//		}
//		for (int count = 0, i = cbuf.position(); i < cbuf.limit(); i++, count++) {
//			if (cbuf.get(i) == SPACE) {
//				requestUri = cbuf.subSequence(0, count).toString();
//				cbuf.position(++i);
//				break;
//			}
//		}
//		for (int count = 0, i = cbuf.position(); i < cbuf.limit(); i++, count++) {
//			if (cbuf.get(i) == '\n' && cbuf.get(i + 1) == '\r') {
//				httpVersion = cbuf.subSequence(0, count).toString();
//				cbuf.position(i + 2);
//				break;
//			}
//		}
		List<String> strings = new LinkedList<>();
		String s = "";
		while (true).isEmpty()) {
			!(s = readTo(cbuf, "\n\r"));
			strings.add(s);
		}
		Map<String, String> headers = new LinkedHashMap<>();

//		for (int count = 0, i = cbuf.position(); i < cbuf.limit(); i++, count++) {
//			if (cbuf.get(i) == ':' && cbuf.get(i + 1) == SPACE) {
//				httpVersion = cbuf.subSequence(0, count).toString();
//				cbuf.position(i + 2);
//				break;
//			}
//			if (cbuf.get(i) == '\n' && cbuf.get(i + 1) == '\r') {
//				httpVersion = cbuf.subSequence(0, count).toString();
//				cbuf.position(i + 2);
//				break;
//			}
//		}

		System.out.println(method);
		System.out.println(requestUri);
		System.out.println(httpVersion);
		System.out.println(strings);
	}

	private static String readTo(CharBuffer cbuf, String stopString) {
		int stopIndex = 0;
		boolean end = false;
		StringBuilder sb = new StringBuilder();
		while (cbuf.hasRemaining()) {
			char c = cbuf.get();
			if (stopString.charAt(stopIndex) == c) {
				stopIndex++;
				if (stopIndex == stopString.length()) {
					return sb.substring(0, sb.length() - stopIndex + 1);
				}
			}
			sb.append(c);
		}
		throw new RuntimeException("END");
	}
//	private static CharBuffer readToBuffer(CharBuffer cbuf, String stopString) {
//		int stopIndex = 0;
//		boolean end = false;
//		StringBuilder sb = new StringBuilder();
//		while (cbuf.hasRemaining()) {
//			char c = cbuf.get();
//			if (stopString.charAt(stopIndex) == c) {
//				stopIndex++;
//				if (stopIndex == stopString.length()) {
//					return sb.substring(0, sb.length() - stopIndex + 1);
//				}
//			}
//			sb.append(c);
//		}
//		ch
//		throw new RuntimeException("END");
//	}

//	private Request split()
////	private static boolean isEOL(CharBuffer buf) {
////
////	}

//	public static class Request {
//		static final char SPACE = ' ';
//
//		String method;
//		String path;
//		String httpVersion;
//
//		public Request(CharBuffer buf) {
//			method = parseMethod(buf);
//		}
//
//		private String parseMethod(CharBuffer buf) {
//			for (int i = 0; i < 7; i++) {
//				if ()
//			}
//		}
//	}
//
//	enum Methods {
//
//	}
}
