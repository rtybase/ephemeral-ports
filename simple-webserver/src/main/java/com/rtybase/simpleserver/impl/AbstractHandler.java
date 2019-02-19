package com.rtybase.simpleserver.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.rtybase.simpleserver.SimpleServer;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public abstract class AbstractHandler implements HttpHandler {

	private final String rootPath;
	private final Set<String> supportedMethods;

	protected AbstractHandler(String rootPath, String... method) {
		this.rootPath = Objects.requireNonNull(rootPath, "rootPath must not be null!");
		supportedMethods = new HashSet<>(Arrays.asList(method));
	}

	protected abstract void exec(HttpExchange exchange) throws IOException;

	@Override
	public final void handle(HttpExchange exchange) throws IOException {
		String message = String.format(" '%s' request from '%s' for '%s'", exchange.getRequestMethod(),
				exchange.getRemoteAddress(), exchange.getRequestURI());
		System.out.println("New " + message);
		printRequestHeaders(exchange);

		if (isPathAllowed(exchange) && isMethodAllowed(exchange)) {
			exec(exchange);
		} else {
			doNotFound(exchange);
		}

		System.out.println("Completed " + message);
		exchange.close();
	}

	protected static void doNotFound(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(SimpleServer.HTTP_NOT_FOUND_RESPONSE, -1);
	}

	private boolean isPathAllowed(HttpExchange exchange) {
		String path = exchange.getRequestURI().getPath();
		boolean result = false;

		if (path != null) {
			Path pth = Paths.get(path);
			Path npth = pth.normalize();
			result = npth.startsWith(rootPath + "/");
		}

		return result;
	}

	private boolean isMethodAllowed(HttpExchange exchange) {
		return supportedMethods.contains(exchange.getRequestMethod());
	}

	private static void printRequestHeaders(HttpExchange exchange) {
		Headers headers = exchange.getRequestHeaders();
		final StringBuilder sb = new StringBuilder();
		headers.entrySet().forEach(entry -> {
			sb.append(entry.getKey());
			sb.append(": ");
			entry.getValue().forEach(v -> {
				sb.append(v);
				sb.append(" ");
			});
			sb.append("\n");
		});
		System.out.println(sb);
	}
}
