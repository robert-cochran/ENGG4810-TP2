package software;

/**
 * http://www.sunshine2k.de/coding/javascript/crc/crc_js.html http://crccalc.com/
 *
 * @author s4313513
 */
public class CRC {

  /**
   * makes 16bit checksum of msg with crc CRC-16-CCITT
   *
   * @param crc
   * @param msg
   * @return
   */
  public static byte[] crcProgrammable1021(byte[] msg) {
    int remainder = 0x0;
    for (int i = 0; i < msg.length; i++) {
      remainder = remainder ^ 0x10000; // = 0001 0000 0000 0000 0000
      remainder = remainder ^ (msg[i] << 8);
      for (int j = 1; j < 9; j++) {
        // if leftmost (most significant) bit is set 1000,0000,0000,0000
        if ((remainder & 0x8000) == 0x8000) {
          // 0x1021 = 0b0001 0000 0010 0001
          remainder = (remainder << 1) ^ 0b10001000000100001;
        } else {
          remainder = remainder << 1;
        }
      }
      remainder = remainder & 0xffff; // Trim remainder to 16 bits
    }
    byte[] crc = new byte[2];
    crc[0] = (byte) (remainder >> 8);
    crc[1] = (byte) (remainder & 0b11111111);
    System.out.println(
        Integer.toBinaryString(remainder >> 8)
            + " "
            + Integer.toBinaryString(remainder & 0b0000000011111111));
    System.out.println(Integer.toBinaryString(crc[0]) + " " + crc[1]);
    return crc;
  }

  /**
   * CRC-16 CCITT-FALSE poly:1021
   *
   * @param msg
   * @return
   */
  public static int crc1021(byte[] message) {
    int crc = 0x0000; // initial value
    int polynomial = 0x1021; // 0001 0000 0010 0001  (0, 5, 12)

    for (byte onebyte : message) {
      for (int i = 0; i < 8; i++) {
        boolean bit = ((onebyte >> (7 - i) & 1) == 1);
        boolean c15 = ((crc >> 15 & 1) == 1);
        crc <<= 1;
        if (c15 ^ bit) crc ^= polynomial;
      }
    }

    crc &= 0xffff;
    // System.out.println("CRC16-CCITT = " + Integer.toHexString(crc));

    return crc;
  }

  public static boolean crcCheck(String packetString) {
    if (packetString == "") {
      return false;
    }
    String[] splitPacket = packetString.split(",");
    String checkSum = splitPacket[splitPacket.length - 1];

    int lastComma = packetString.lastIndexOf(',');
    String packetStringMinusChecksum = packetString.substring(0, lastComma);
    // System.out.println(packetStringMinusChecksum);
    if (Integer.toHexString(crc1021(packetStringMinusChecksum.getBytes())).equals(checkSum)) {
      return true;
    } else return false;
  }

  public static void main(String[] args) throws Exception {
    // System.out.println(crcCheck("47tp2,13:30:19,S2750.0172,E15301.6695,029,078,0,08.4,1,0,\r\naxon29,00-14-22-01-23-45,075,a15b"));
    System.out.println(
        Integer.toHexString(
            crc1021("47tp2,13:30:15,S2750.0361,E15301.6535,036,080,0,07.7,1,0".getBytes())));
  }
}
