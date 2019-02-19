package com.rtybase.simpleserver.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.rtybase.simpleserver.SimpleServer;
import com.sun.net.httpserver.Headers;

import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class FileHandler extends AbstractHandler {
	private static final int BUFFER_SIZE = 512;
	private static final String ROOT = "src/main/resources";

	public static final String PATH = "/files";

	public FileHandler() {
		super(PATH, "GET");
	}

	@Override
	public void exec(HttpExchange exchange) throws IOException {
		String filePath = exchange.getRequestURI().getPath();

		File file = getFileOutOfPath(filePath);

		if (file != null) {
			setResponseHeaders(exchange, filePath);

			long len = file.length();
			if (len == 0) {
				len = -1;
			}
			exchange.sendResponseHeaders(SimpleServer.HTTP_OK_RESPONSE, len);
			sendFile(file, exchange);
		} else {
			doNotFound(exchange);
		}
	}

	private static void setResponseHeaders(HttpExchange exchange, String file) {
		Headers header = exchange.getResponseHeaders();

		String filePath = file.toLowerCase();
		if (filePath.endsWith(".jpeg")) {
			header.set(SimpleServer.CONTENT_TYPE, "image/jpeg");
		} else if (filePath.endsWith(".png")) {
			header.set(SimpleServer.CONTENT_TYPE, "image/png");
		} else if (filePath.endsWith(".json")) {
			header.set(SimpleServer.CONTENT_TYPE, "application/json");
		} else if (filePath.endsWith(".txt")) {
			header.set(SimpleServer.CONTENT_TYPE, "text/plain");
		} else {
			header.set(SimpleServer.CONTENT_TYPE, "application/octet-stream");
		}

		if (filePath.contains(SimpleServer.GZIP_MARK)) {
			header.set(SimpleServer.CONTENT_ENCODING, "gzip");
		}

		header.set("Connection", "close");
	}

	private static File getFileOutOfPath(String path) {
		File file = new File(ROOT, path);

		if (file.exists() && file.isFile()) {
			return file;
		} else {
			return null;
		}
	}

	protected static void sendFile(File file, HttpExchange exchange) {
		byte[] buffer = new byte[BUFFER_SIZE];
		int len = 0;

		try (FileInputStream in = new FileInputStream(file);
				OutputStream out = exchange.getResponseBody()) {

			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
				out.flush();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
