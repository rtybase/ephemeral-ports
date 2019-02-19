package com.rtybase.simpleserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.rtybase.simpleserver.impl.EphemeralPortReportHandler;
import com.rtybase.simpleserver.impl.FileHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class SimpleServer {
	private static final int PORT = 8181;
	private static final int INCOMMING_CONNECTIONS_BACKLOG = 500;

	public static final String GZIP_MARK = ".gzip";

	public static final int HTTP_OK_RESPONSE = 200;
	public static final int HTTP_NOT_FOUND_RESPONSE = 404;

	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_ENCODING = "Content-Encoding";

	public static void main(String[] args) throws Exception {
		startServer();
	}

	private static void startServer() throws IOException {
		System.out.print("Starting the server ... ");
		InetSocketAddress adr = new InetSocketAddress(PORT);

		HttpServer server = HttpServer.create(adr, INCOMMING_CONNECTIONS_BACKLOG);

		server.createContext(FileHandler.PATH, new FileHandler());
		server.createContext(EphemeralPortReportHandler.PATH, new EphemeralPortReportHandler());

		server.setExecutor(Executors.newFixedThreadPool(50));
		server.start();
		System.out.println("DONE.");
	}
}
