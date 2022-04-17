package httpserver;

import java.util.*;

public class HttpResponse {
	private final static String NEW_LINE = "\n";

	private final Map<String, String> headers = new HashMap<>();

	private String body;

	private int statusCode = 200;

	private String status = "OK";

	public HttpResponse() {
		this.headers.put("Server", "myhttpserver");
		this.headers.put("Connection", "close");
	}
	
	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}

	public void addHeaders(Map<String, String> map) {
		this.headers.putAll(map);
	}

	public void setBody(String body) {
		this.headers.put("Content-Length", String.valueOf(body.length()));
		this.body = body;
	}

	public String getBody() {
		return this.body;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatusCode(int code) {
		this.statusCode = code;
	}

	public Map<String, String> getHeaders() {
		return this.headers;
	}

	public String getHeader(String key) {
		return this.headers.get(key);
	}

	public String message() {
		StringBuilder message = new StringBuilder();
		message.append("HTTP/1.1")
			.append(" ")
			.append(statusCode)
			.append(" ")
			.append(status)
			.append(NEW_LINE);
		for(Map.Entry<String, String> header : this.headers.entrySet()) {
			message.append(header.getKey())
				.append(": ")
				.append(header.getValue())
				.append(NEW_LINE);
		}

		message.append(NEW_LINE);
		message.append(body);
		return message.toString();
	}

	public byte[] getBytes() {
		return message().getBytes();
	}
}	
