import java.io.*;
import java.net.*;
import java.util.*;
import functions.*;

public class Server {

  private int maxConnections;
  private int listenPort;

  public Server(int listenPort, int maxConnections) {
    this.listenPort = listenPort;
    this.maxConnections = maxConnections;
  }

  public void acceptConnections() {
    try (ServerSocket server = new ServerSocket(listenPort, 5)) {
      while (true) {
        handleConnection(server.accept());
      }
    } catch (BindException e) {
      System.out.println("Unable to bind to port " + listenPort);
    } catch (IOException e) {
      System.out.println("Unable to instantiate a ServerSocket on port: " + listenPort);
    }
  }

  protected void handleConnection(Socket connection) {
    ConnectionHandler.processRequest(connection);
  }

  public static void main(String[] args) {
    Server server = new Server(1080, 3);
    server.setUpConnectionHandlers();
    server.acceptConnections();
  }

  public void setUpConnectionHandlers() {
    for (int i = 0; i < maxConnections; i++) {
      Thread handlerThread = new Thread(new ConnectionHandler(), "Handler " + i);
      handlerThread.setDaemon(true);
      handlerThread.start();
    }
  }

  private static class ConnectionHandler implements Runnable {
    private Socket connection;
    private static final List<Socket> pool = new LinkedList<>();

    public void handleConnection() {
      try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        System.out.println("Handling client at " + connection.getInetAddress().getHostAddress() + " on port " + connection.getPort());

        String parsedHL7Message = getMessage.GetMessage(in);
        System.out.print(parsedHL7Message);

        String parsedAckMessage = ackMessage.GetAckMessage(parsedHL7Message);
        out.write(parsedAckMessage.getBytes());
      } catch (IOException e) {
        System.out.println("Error while reading and writing to connection: " + e.getMessage());
        throw new RuntimeException(e);
      } finally {
        try {
          connection.close();
        } catch (IOException e) {
          System.out.println("Error while attempting to close connection: " + e.getMessage());
          throw new RuntimeException(e);
        }
      }
    }

    public static void processRequest(Socket request) {
      synchronized (pool) {
        pool.add(request);
        pool.notifyAll();
      }
    }

    public void run() {
      while (true) {
        synchronized (pool) {
          while (pool.isEmpty()) {
            try {
              pool.wait();
            } catch (InterruptedException e) {
              return;
            }
          }
          connection = pool.remove(0);
        }
        handleConnection();
      }
    }
  }
}