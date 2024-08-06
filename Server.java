import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.*;
import ca.uhn.hl7v2.HL7Exception;
import java.io.*;
import java.net.*;

public class Server {
  private final int port;

  public Server(int port) {
    this.port = port;
  }

  public void start() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Servidor iniciado na porta " + port);

      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Cliente conectado.");

        new ServerThread(socket).start();
      }
    } catch (IOException e) {
      System.out.println("Erro no servidor: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private class ServerThread extends Thread {
    private Socket socket;

    public ServerThread(Socket socket) {
      this.socket = socket;
    }

    public void run() {
      try (
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true)
      ) {
        String message;
        StringBuilder messageBuilder = new StringBuilder();

        while ((message = reader.readLine()) != null && !message.isEmpty()) {
          messageBuilder.append(message).append("\r");
        }

        String receivedMessage = messageBuilder.toString();
        processMessage(receivedMessage);

        writer.println("Mensagem processada com sucesso.");
      } catch (IOException | HL7Exception e) {
        System.out.println("Erro na comunicacao com o cliente: " + e.getMessage());
        e.printStackTrace();
      } finally {
        try {
          socket.close();
        } catch (IOException e) {
          System.out.println("Erro ao fechar socket: " + e.getMessage());
        }
      }
    }

    private void processMessage(String message) throws HL7Exception {
      if (message.startsWith("MSH")) {
        Parser parser = new PipeParser();
        Message hl7Message = parser.parse(message);
        System.out.println();
        System.out.println("Mensagem HL7 recebida:\n" + hl7Message.encode());
      } else if (message.startsWith("<")) {
        XMLParser xmlParser = new DefaultXMLParser(new CanonicalModelClassFactory("2.6"));
        Message hl7XmlMessage = xmlParser.parse(message);
        System.out.println();
        System.out.println("Mensagem HL7 Xml recebida:\n" + hl7XmlMessage.encode());
      } else {
        System.out.println();
        System.out.println("Formato de mensagem não reconhecido:\n" + message);
      }
    }
  }

  public static void main(String[] args) {
    int port = 7777;
    Server server = new Server(port);
    server.start();
  }
}
