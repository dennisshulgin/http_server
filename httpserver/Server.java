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


			if(this.handler != null) {
				try {
					String body = this.handler.handle(httpRequest, httpResponse);
					if(body != null && !body.isEmpty()) {
						if(httpResponse.getHeader("Content-Length") == null) {
							httpResponse.addHeader("Content-Type", "text/html; charset=utf-8");
						}
						httpResponse.setBody(body);
					} 
				} catch(Exception e) {
					e.printStackTrace();
					httpResponse.setStatusCode(500);
					httpResponse.setStatus("Internal Server Error");
					httpResponse.addHeader("Content-Type", "text/html; charset=utf-8");
					httpResponse.setBody("<html><head><title>Error</title></head><body><h2>Internal Server Error</h2></body></html>");
				}
			} else {
				httpResponse.setStatusCode(404);
				httpResponse.setStatus("Not Found");
				httpResponse.addHeader("Content-Type", "text/html; charset=utf-8");
				httpResponse.setBody("<html><head><title>Not Found</title></head><body><h2>NOT FOUND</h2></body></html>");
			}

                        channel.write(ByteBuffer.wrap(httpResponse.getBytes()));
                        channel.close();
		}
	}
}

