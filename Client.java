import java.io.*;
import java.net.*;

public class Client {
  public static void main(String[] args) {
    String hostname = "localhost";
    int port = 1234;

    try (Socket socket = new Socket(hostname, port)) {
      OutputStream output = socket.getOutputStream();
      PrintWriter writer = new PrintWriter(output, true);

      InputStream input = socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));

      writer.println("R|202407250004|15|NT-proBNP|0.300|22.000|0.440|0.000|<0.30|ng/ml|Neg(-)|20240725162107|0|0|4ee046e6c5fb0020|1234");

      String resposta = reader.readLine();
      System.out.println(resposta);
    } catch (UnknownHostException ex) {
      System.out.println("Servidor não encontrado: " + ex.getMessage());
    } catch (IOException ex) {
      System.out.println("Erro de I/O: " + ex.getMessage());
    }
  }
}