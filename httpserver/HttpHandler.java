package httpserver;

@FunctionalInterface
public interface HttpHandler {
	String handle(HttpRequest httpRequest, HttpResponse httpResponse);
}
