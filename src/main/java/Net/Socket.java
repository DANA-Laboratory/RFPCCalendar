package Net;

import java.net.URISyntaxException;

/**
 * Created by AliReza on 9/9/2016.
 */
public class Socket {
    // io.socket.client.IO;
    static final int PORT = 9291;
    static private io.socket.client.Socket create(){
        try {
            return io.socket.client.IO.socket("http://localhost:" + PORT);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
    static private io.socket.client.Socket socket = create();
    static public io.socket.client.Socket get() {
        return  socket;
    }
}
