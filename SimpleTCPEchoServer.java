import java.io.*;
import java.net.*;

public class SimpleTCPEchoServer {
  private static final int BUFFER_SIZE = 300;

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(1080);
    int receivedMessageSize;
    byte[] receivedByeBuffer = new byte[BUFFER_SIZE];

    while (true) {
      Socket clientSocket = serverSocket.accept();

      System.out.println("Handling client at " +
        clientSocket.getInetAddress().getHostAddress() + " through port " +
        clientSocket.getPort()
      );

      InputStream in = clientSocket.getInputStream();
      OutputStream out = clientSocket.getOutputStream();

      receivedMessageSize = in.read(receivedByeBuffer);
      out.write(receivedByeBuffer, 0, receivedMessageSize);

      clientSocket.close();
    }
  }
}
