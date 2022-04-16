package httpserver;

import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.concurrent.*;
import java.util.*;
import java.net.*;

class Server {
	private static final int BUFFER_SIZE = 128;
	public AsynchronousServerSocketChannel socket;

	public static String HEADERS = "HTTP/1.1 200 OK\n" +
				 	"Server: denis\n" +
					"Connection: close\n" +
					"Content-Type: text/html\n" +
					"Content-Length: %s\n\n";

	private final HttpHandler handler;

	public Server(HttpHandler handler) {
		this.handler = handler;
	}

	public void bootstrap() {
		try {
			socket = AsynchronousServerSocketChannel.open();
			socket.bind(new InetSocketAddress("localhost", 8888));

			while(true) {
				Future<AsynchronousSocketChannel> future = socket.accept();
				clientHandle(future);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void clientHandle(Future<AsynchronousSocketChannel> future) throws Exception {
		AsynchronousSocketChannel channel = future.get();
		while(channel != null && channel.isOpen()) {
			ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                        StringBuilder builder = new StringBuilder();
                        boolean keepReading = true;                                                                                                              
                                                                                                                                                                 
                        while(keepReading) {                                                                                                                     
                                int resultRead = channel.read(buffer).get();
                                keepReading = resultRead == BUFFER_SIZE;                                                                                           
                                buffer.flip();
				CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
				builder.append(charBuffer);
				buffer.clear();
                        }                      
			
			HttpRequest httpRequest = new HttpRequest(builder.toString());
			HttpResponse httpResponse = new HttpResponse();

			String body = this.handler.handle(httpRequest, httpResponse);
			
                        String headers = String.format(HEADERS, body.length());
                        String resp = headers + body;
                        
                        channel.write(ByteBuffer.wrap(resp.getBytes()));
                        channel.close();
		}
	}
}

