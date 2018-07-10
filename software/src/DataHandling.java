package software;

import java.util.Set;
import java.util.TreeMap;

public class DataHandling {
  /**
   * If point has no valid gps nothing is done with it the last valid gps point will have it's
   * skipped packet property++ If point has valid gps, it will start incrementing its skippedPacket
   * for every skipped packet after that one
   *
   * @param dataPointMap
   * @return
   */
  public static TreeMap<String, DataPointStruct> determineSkippedPackets(
      TreeMap<String, DataPointStruct> dataPointMap) {
    boolean startingGPSFound = false;
    DataPointStruct validGPS = null;
    int noValidGPSYet = 0;
    Set<String> dataPointSet = dataPointMap.keySet();
    for (String timeKey : dataPointSet) {
      DataPointStruct dp = dataPointMap.get(timeKey);
      if (dp.gpsLock.equals("1") && dp.gpsDisabled.equals("0")) {
        if (!startingGPSFound) {
          dp.skippedPackets = noValidGPSYet;
          startingGPSFound = true;
        }
        for (AvailableWifi wifi : dp.wifiAccessPoints) {
          // accessPoints.addAccessPointData(wifi.ssid, wifi.bssid, wifi.rssi, dp.latitude,
          // dp.longitude);
        }
        validGPS = dp;
      } else {
        // if estimation needed (gps not locked or gps is disabled?)
        // if (dp.gpsLock.equals('0') || dp.gpsDisabled.equals('1')) { //this wont work if first few
        // readings?) {
        // are there 3 unique bssid's in the list?
        /*ArrayList<AvailableWifi> availableWifiPoints = all the wifi's from the packet;
        String [] estLatLong = accessPoints.estimateCurrentGPS(availableWifiPoints);
        if (estLatLong[0] == "0") {
        	value couldn't be generated
        	maybe have a line on why it couldnt be generated in the gps string??
        }
        else {
        	dp.latitude = estLatLong[0];
        	dp.longitude = estLatLong[1];
        }*/
        /**
         * IF GPS COULD NOT BE DETERMINED, THEN WE WRITE OUT IF NO LOCK OR DISABLED Possibly write
         * out if GPS could not be determined for this position
         */
        if (dp.gpsLock.equals("0")) {
          dp.latitude = "No GPS - lock unavailable and localization unavailable";
          dp.longitude = "No GPS - lock unavailable and localization unavailable";
        }
        if (dp.gpsDisabled.equals("1")) {
          dp.latitude = "No GPS - gps disabled and localization unavailable";
          dp.longitude = "No GPS - gps disabled and localization unavailable";
        }
        if (validGPS != null) {
          validGPS.skippedPackets = validGPS.skippedPackets + 1;
        } else {
          noValidGPSYet++;
        }
      }
      dataPointMap.put(timeKey, dp);
    }
    return dataPointMap;
  }
}
