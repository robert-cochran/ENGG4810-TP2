package software;

import java.util.ArrayList;

/**
 * Should these be private or does it not matter?
 *
 * @author s4313513
 */
public class DataPointStruct {

  public String uv;
  public String temperature; // resolution of 1C/1%
  public String humidity; // resolution of 1C/1%
  public String batteryVoltage;
  public String longitude; // [-23.577083] [Dddmm.mmmm]
  public String latitude; // [ddmm.mmmm]
  public String gpsLock; // 0 = no lock
  public String gpsDisabled; // 1 = disabled
  public String deviceTime; // [hour:min:sec]

  public ArrayList<AvailableWifi> wifiAccessPoints;
  public boolean nodeLock; // a set of node locations have been established and are in range

  public int skippedPackets; // Skipped packets after this point.
  // (Except first packet, this will include skipped packets BEFORE AND AFTER

  /** Constructor for DataPointStruct */
  public DataPointStruct(String packet) {
    packet = packet.replace("47tp2,", "");
    String[] splitPacket = packet.split(",");
    this.deviceTime = splitPacket[0];
    this.addGPS(splitPacket[1], splitPacket[2]);
    this.temperature = splitPacket[3]; // Integer.parseInt(splitPacket[3]);
    this.humidity = splitPacket[4]; // Integer.parseInt(splitPacket[4]);
    if (Integer.parseInt(splitPacket[5]) == 0) {
      this.uv = "inside";
    } else {
      this.uv = "outside";
    }
    this.batteryVoltage = splitPacket[6]; // Float.parseFloat(splitPacket[6]);
    this.gpsLock = splitPacket[7];
    this.gpsDisabled = splitPacket[8];
    // packet could have no wifi, one wifi, two...5

    // determine length, have a loop that loops around 'length' times
    wifiAccessPoints = new ArrayList<AvailableWifi>();
    if (splitPacket.length < 13) {
      this.wifiAccessPoints.add(
          new AvailableWifi("No ssid's recorded", "No bssid's recorded", "No rssi's recorded"));
    }
    for (int i = 9; i + 2 < (splitPacket.length); i = i + 3) {
      this.wifiAccessPoints.add(
          new AvailableWifi(splitPacket[i], splitPacket[i + 1], splitPacket[i + 2]));
    }
    this.skippedPackets = 0;
  }

  /**
   * North is positive, South is negative, East is positive, West is negative
   *
   * <p>Example S2749:9950 E15301.6854
   *
   * @param latitude
   * @param longitude
   */
  public void addGPS(String latitude, String longitude) {
    latitude = latitude.replace("S", "-");
    latitude = latitude.replace("N", "");
    latitude = latitude.replace(".", "");
    float lat = (float) Integer.parseInt(latitude) / (float) 1000000;
    this.latitude = Float.toString(lat);

    longitude = longitude.replace("W", "-");
    longitude = longitude.replace("E", "");
    longitude = longitude.replace(".", "");
    float lon = (float) Integer.parseInt(longitude) / (float) 1000000;
    this.longitude = Float.toString(lon);
  }

  public static void main(String args[]) {
    System.out.println(10 / 3);
  }
}
