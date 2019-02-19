package com.rtybase.ephemeralportvalidator.httpclient;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

import android.util.Log;

public class HttpClient {
	private static final String TAG = "com.rtybase.ephemeralportvalidator.httpclient";

	private static final int TIME_OUT = 10000;

	public String doGet(String stringUrl) throws Exception {
		String response = null;
		Socket s = null;
		OutputStream os = null;
		InputStream is = null;

		try {
			URI uri = new URI(stringUrl);

			s = new Socket();
			s.setSoTimeout(TIME_OUT);
			s.connect(new InetSocketAddress(uri.getHost(), getPortFrom(uri)), TIME_OUT);
			os = s.getOutputStream();
			is = s.getInputStream();
			sendRequest(s, uri, os);
			response = readResponse(s, is);

		} catch (Exception ex) {
			Log.w(TAG, "closeHandler()", ex);
			throw ex;
		} finally {
			closeHandler(s);
			closeHandler(os);
			closeHandler(is);
		}
		return response;
	}

	private String readResponse(Socket s, InputStream is) throws Exception {
		StringBuilder response = new StringBuilder("Ephemeral port: ");
		response.append(s.getLocalPort());
		response.append("\n");

		try {
			int bt = 0;
			while ((bt = is.read()) != -1) {
				char ch = (char) bt;
				response.append(ch);
			}
		} catch (Exception ex) {
			if (response.length() == 0) {
				throw ex;
			}
		}

		return response.toString();
	}

	private void sendRequest(Socket s, URI uri, OutputStream os) throws Exception {
		StringBuilder sb = new StringBuilder();

		sb.append("GET ");
		sb.append(getPath(uri));
		sb.append(" HTTP/1.1\r\n");

		sb.append("Host: ");
		sb.append(uri.getHost());
		sb.append(":");
		sb.append(getPortFrom(uri));
		sb.append("\r\n");

		sb.append("Accept: application/json\r\n");
		sb.append("Connection: close\r\n\r\n");

		os.write(sb.toString().getBytes());
		os.flush();
	}

	private String getPath(URI uri) {
		String path = uri.getPath();
		if (path == null) {
			path = "/";
		}
		return path;
	}

	private int getPortFrom(URI uri) {
		int port = uri.getPort();
		if (port < 0) {
			port = 80;
		}
		return port;
	}

	private void closeHandler(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				Log.w(TAG, "closeHandler()", e);
			}
		}
	}

	private void closeHandler(Socket c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				Log.w(TAG, "closeHandler()", e);
			}
		}
	}
}
