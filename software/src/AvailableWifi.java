package software;

/**
 * Collection of properties of a given access point reading
 *
 * <p>This is intended to be used only with an individual packet, so this class can produce
 * identical objects
 *
 * <p>SSID = Name of network e.g. 'axon209' Linksys, xfinitywifi, NETGEAR, dlink or just default
 * BSSID = Specific access point address, MAC address RSSI = strength of signal
 *
 * <p>Am i expected to return all the data points given to me from a specific location?
 */
public class AvailableWifi {

  public String ssid;
  public String bssid;
  public String rssi;

  public AvailableWifi(String ssid, String bssid, String rssi) {
    this.ssid = ssid;
    this.bssid = bssid;
    this.rssi = rssi;
  }
}
