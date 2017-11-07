package ua.tifoha.examples.server;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

//import sun.net.httpserver.HttpContextImpl;
//import sun.net.httpserver.SSLStreams;
//import sun.net.httpserver.ServerImpl;

/**
 * Created by Vitaliy Sereda on 07.11.17.
 */
class HttpConnection {
//	HttpContextImpl context;
	//	SSLEngine engine;
//	SSLContext sslContext;
//	SSLStreams sslStreams;
//	InputStream i;
//	InputStream raw;
//	OutputStream rawout;
	SocketChannel sc;
	//	SelectionKey selectionKey;
//	String protocol;
	long time;
	volatile long creationTime;
	volatile long rspStartedTime;
	int remaining;
	boolean closed = false;
	Logger logger;
	volatile HttpConnection.State state;

	public String toString() {
		String var1 = null;
		if (this.sc != null) {
			var1 = this.sc.toString();
		}

		return var1;
	}

	HttpConnection() {
	}

	void setChannel(SocketChannel var1) {
		this.sc = var1;
	}

//	void setContext(HttpContextImpl var1) {
//		this.context = var1;
//	}

	HttpConnection.State getState() {
		return this.state;
	}

	void setState(HttpConnection.State var1) {
		this.state = var1;
	}

//	void setParameters(InputStream var1, OutputStream var2, SocketChannel var3, SSLEngine var4, SSLStreams var5, SSLContext var6, String var7, HttpContextImpl var8, InputStream var9) {
//		this.context = var8;
//		this.i = var1;
//		this.rawout = var2;
//		this.raw = var9;
//		this.protocol = var7;
//		this.engine = var4;
//		this.sc = var3;
//		this.sslContext = var6;
//		this.sslStreams = var5;
//		this.logger = var8.getLogger();
//	}

	SocketChannel getChannel() {
		return this.sc;
	}

	synchronized void close() {
		if (!this.closed) {
			this.closed = true;
			if (this.logger != null && this.sc != null) {
				this.logger.finest("Closing connection: " + this.sc.toString());
			}

			if (!this.sc.isOpen()) {
				System.out.println("Channel already closed");
			} else {
//				try {
//					if (this.raw != null) {
//						this.raw.close();
//					}
//				} catch (IOException var5) {
//					ServerImpl.dprint(var5);
//				}
//
//				try {
//					if (this.rawout != null) {
//						this.rawout.close();
//					}
//				} catch (IOException var4) {
//					ServerImpl.dprint(var4);
//				}
//
//				try {
//					if (this.sslStreams != null) {
//						this.sslStreams.close();
//					}
//				} catch (IOException var3) {
//					ServerImpl.dprint(var3);
//				}

				try {
					this.sc.close();
				} catch (IOException e) {
					System.out.println(e);
				}

			}
		}
	}

	void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	int getRemaining() {
		return this.remaining;
	}

//	SelectionKey getSelectionKey() {
//		return this.selectionKey;
//	}
//
//	InputStream getInputStream() {
//		return this.i;
//	}
//
//	OutputStream getRawOutputStream() {
//		return this.rawout;
//	}
//
//	String getProtocol() {
//		return this.protocol;
//	}
//
//	SSLEngine getSSLEngine() {
//		return this.engine;
//	}
//
//	SSLContext getSSLContext() {
//		return this.sslContext;
//	}

//	HttpContextImpl getHttpContext() {
//		return this.context;
//	}

	public enum State {
		IDLE,
		REQUEST,
		RESPONSE;

		private State() {
		}
	}
}
