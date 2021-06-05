package com.app.GeoTracker;

import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.fusesource.mqtt.client.Message;

// import sun.security.krb5.internal.crypto.crc32;

public class Start {

  /**
   * Outline 1 - pre loop: Will read from CSV FIRST to write KML without looping and waiting for
   * mqtt packets
   *
   * <p>Outline 2 - loop: Loop Reads from mqttClient Adds to list Writes that list to KML (if valid
   * messages are received) Writes that lis to CSV (if valid msgs)
   *
   * <p>Requirements Will only write new files if a new msg packet is received (must be unique)
   *
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {

    System.out.println("Hello World");

    /**
     * To Do: Code saving more than 5 access points GPS Triangulation or multilateration Check all
     * parsing statements, or anything that needs to be sanitized before array splits Check that no
     * null pointer exceptions could be called Check that no out of bounds exceptions could be
     * called Extruded path for each unique ssid (rssi being the bssid that gives the strongest
     * signal) remove stacktraces break up skipped packets for first one to show skipped packets
     * before Check through spec to see what else im missing
     *
     * <p>To Do: Final Commit have final commit preparation sorted (i.e. what i need to submit)
     * executable jar hand in sheet include java to install (maybe on usb?) proof of linting markup
     * of design and build documentation (writeup software doc?) write code in java style include
     * code style guide (google java style guide)
     */

    /** Reads a pre-written CSV Writes KML "CSVtoKML.xml" */
    KmlCreate csvToKml = new KmlCreate();
    // JFileChooser fileChooser = new JFileChooser();
    System.out.println("Initiating reading CSV from file system");
    JFrame frame = new JFrame();
    String CSVFileName =
        JOptionPane.showInputDialog(
            frame, "Name of file to read CSV data from (include .csv at end)");
    ArrayList<String[]> csvToList = CSV.read(CSVFileName);
    if (!csvToList.isEmpty()) {
      TreeMap<String, DataPointStruct> csvDataPointMap = new TreeMap<String, DataPointStruct>();
      for (String[] csvLine : csvToList) {
        DataPointStruct dp = new DataPointStruct(csvLine[2]);
        csvDataPointMap.put(dp.deviceTime, dp);
      }
      for (String timeStamp : csvDataPointMap.keySet()) {
        csvToKml.addDataPoint(csvDataPointMap.get(timeStamp));
      }
      csvToKml.writeKML("CSVtoKML.kml");
    } else {
      System.out.println("Unable to read CSV file: KML not generated");
    }

    /**
     * Collects a new mqtt packet until timer finishes writes to kml, writes to csv
     *
     * <p>Will not write packets missing '47tp2' or bad checksums
     *
     * <p>Packets without a GPS lock or have GPS disabled will not be written to the kml They will
     * be written to the CSV though with mention of GPS being locked or disabled
     *
     * <p>Packet format [id, time, longitude, latitude, temp, humidity, uv, volt, gps not locked,
     * gps not disabled, wifi ssid, bssid, rssi, crcSum]
     */
    // // KmlCreate kml = new KmlCreate();
    // MqttClient connection = new MqttClient();
    // TreeMap<String, DataPointStruct> dataPointMap = new TreeMap<String, DataPointStruct>();
    // String packetString = "";
    // int i = 0;
    // AccessPoints accessPoints = new AccessPoints();
    // while (true) {
    //   Message message = connection.MqttMsgRcv();
    //   // null check, if true the empty string is assigned to ignore this message
    //   if (message != null) {
    //     packetString = new String(message.getPayload());
    //   } else packetString = "";
    //   System.out.println(i++ + " " + packetString);
    //   /** Add's point to the mapping if it is valid */
    //   if (packetString.contains("47tp2")) {
    //     // creates dataPoint if crcCheck is true
    //     if (CRC.crcCheck(packetString)) {
    //       DataPointStruct dataPoint = new DataPointStruct(packetString);

          // if this returns null, dataPoint is new
          // if (dataPointMap.put(dataPoint.deviceTime, dataPoint) == null) {
          //   dataPointMap = DataHandling.determineSkippedPackets(dataPointMap);
          //   FileHandling.writeToFile(dataPointMap);
  //         }
  //       }
  //     }
  //   }
  }
}
