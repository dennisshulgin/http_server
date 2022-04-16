package httpserver;

class Main {
	public static void main(String... args) {
		Server server = new Server((req, res) -> 
				"<html><head><title>HI</title></head><body><h2>Hello</h2></body></html>");
		server.bootstrap();
	}
}
