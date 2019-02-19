package com.rtybase.simpleserver.impl;

import java.io.IOException;
import java.io.OutputStream;

import com.rtybase.simpleserver.SimpleServer;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class EphemeralPortReportHandler extends AbstractHandler {
	public static final String PATH = "/myport";

	public EphemeralPortReportHandler() {
		super(PATH, "GET");
	}

	@Override
	protected void exec(HttpExchange exchange) throws IOException {
		int ephemeralPort = exchange.getRemoteAddress().getPort();

		Headers header = exchange.getResponseHeaders();
		header.set(SimpleServer.CONTENT_TYPE, "application/json");
		header.set("Connection", "close");

		String response = String.format("{\"myport\": %d}", ephemeralPort);
		exchange.sendResponseHeaders(SimpleServer.HTTP_OK_RESPONSE, response.length());
		sendText(response, exchange);
	}

	private static void sendText(String text, HttpExchange exchange) {
		try {
			OutputStream out = exchange.getResponseBody();
			byte[] bytes = text.getBytes();
			out.write(bytes, 0, bytes.length);
			out.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
