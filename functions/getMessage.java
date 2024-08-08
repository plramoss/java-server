package functions;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

public class getMessage {
  static final char END_OF_BLOCK = '\u001c';
  static final char START_OF_BLOCK = '\u000b';
  static final char CARRIAGE_RETURN = 13;
  private static final int END_OF_TRANSMISSION = -1;

  public static String GetMessage(InputStream anInputStream) throws IOException {
    StringBuilder parsedMessage = new StringBuilder();
    boolean isNotHl7 = false;
    int characterReceived;

    try {
      characterReceived = anInputStream.read();
    } catch (SocketException e) {
      System.out.println("Unable to read from socket stream. Connection may have been closed: " + e.getMessage());
      return null;
    }

    if (characterReceived == END_OF_TRANSMISSION) return null;

    if (characterReceived != START_OF_BLOCK) {
      isNotHl7 = true;
      System.out.println("A mensagem recebida não é HL7.");
      parsedMessage.append((char) characterReceived);
    }

    while (true) {
      characterReceived = anInputStream.read();

      if (characterReceived == END_OF_TRANSMISSION) {
        if (isNotHl7) return parsedMessage.toString().replace('\r', '\n');
        throw new RuntimeException("Message terminated without end of message character");
      }

      if (characterReceived == END_OF_BLOCK && !isNotHl7) {
        if (anInputStream.read() != CARRIAGE_RETURN) {
          throw new RuntimeException("End of message character must be followed by a carriage return character");
        }
        break;
      }

      parsedMessage.append((char) characterReceived);
    }

    return parsedMessage.toString().replace('\r', '\n');
  }
}