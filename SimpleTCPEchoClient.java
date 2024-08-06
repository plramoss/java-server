import java.io.*;
import java.net.*;

public class SimpleTCPEchoClient {
  public static void main(String[] args) throws IOException {
    String testMessage = "This is a test message";
    byte[] byteBuffer = testMessage.getBytes();

    Socket socket = new Socket("localhost", 1080);
    System.out.println("Connected to Server");

    InputStream in = socket.getInputStream();
    OutputStream out = socket.getOutputStream();

    out.write(byteBuffer);
    in.read(byteBuffer);

    System.out.println("Message received from server: " + new String(byteBuffer));

    socket.close();;
  }
}
