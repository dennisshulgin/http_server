package httpserver;

import java.util.*;

public class HttpRequest {

	private final HttpMethod httpMethod;

	private final String url;

	private final Map<String, String> headers;

	private final String body;

	private final static String DELIMITER = "\r\n\r\n";

	private final static String NEW_LINE = "\n";

	private final static String HEAD_DELIMITER = ":";

	private final String message;

	public HttpRequest(String message) {
		this.message = message;

		String[] parts = message.split(DELIMITER);
		String head = parts[0];

		String[] headers = head.split(NEW_LINE);
		String[] firstLine = headers[0].split(" ");
		this.httpMethod = HttpMethod.valueOf(firstLine[0]);;
		this.url = firstLine[1];

		this.headers = Collections.unmodifiableMap(
				new HashMap<>() {{
					for(int i = 1; i < headers.length; i++) {
						String[] headPair = headers[i].split(HEAD_DELIMITER, 2);
						put(headPair[0].trim(), headPair[1].trim());
					}	
				}});

		String bodyLength = this.headers.get("Content-Type");
		int length = bodyLength != null ? Integer.parseInt(bodyLength) : 0;
		this.body = parts.length > 1 ? parts[1].trim().substring(0, length) : "";
		
	}
	
	public HttpMethod getHttpMethod() {
		return this.httpMethod;
	}

	public Map<String, String> getHeaders() {
		return this.headers;
	}

	public String getBody() {
		return this.body;
	}

	public String getUrl() {
		return this.url;
	}
}
