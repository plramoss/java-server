package functions;

import java.util.StringTokenizer;

public class ackMessage {
  static final char END_OF_BLOCK = '\u001c';
  static final char START_OF_BLOCK = '\u000b';
  static final char CARRIAGE_RETURN = 13;
  private static final int END_OF_TRANSMISSION = -1;
  private static final int MESSAGE_CONTROL_ID_LOCATION = 9;
  private static final String FIELD_DELIMITER = "|";

  public static String GetAckMessage(String aParsedHL7Message) {
    if (aParsedHL7Message == null)
      throw new RuntimeException("Invalid HL7 message for parsing operation" +
        ". Please check your inputs");

    String messageControlID = getMessageControlID(aParsedHL7Message);

    StringBuffer ackMessage = new StringBuffer();
    ackMessage.append(START_OF_BLOCK)
      .append("MSH|^~\\&|||||||ACK||P|2.2")
      .append(CARRIAGE_RETURN)
      .append("MSA|AA|")
      .append(messageControlID)
      .append(CARRIAGE_RETURN)
      .append(END_OF_BLOCK)
      .append(CARRIAGE_RETURN);

    return ackMessage.toString();
  }

  private static String getMessageControlID(String aParsedHL7Message) {
    int fieldCount = 0;
    StringTokenizer tokenizer = new StringTokenizer(aParsedHL7Message, FIELD_DELIMITER);

    while (tokenizer.hasMoreElements())
    {
      String token = tokenizer.nextToken();
      fieldCount++;
      if (fieldCount == MESSAGE_CONTROL_ID_LOCATION){
        return token;
      }
    }

    return "";
  }
}
