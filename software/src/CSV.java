package software;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.TreeMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class CSV {

  // Delimiter used in CSV file
  private static final String NEW_LINE_SEPARATOR = "\n";

  // CSV file header
  private static final Object[] FILE_HEADER = {
    "Device Time",
    "Longitude",
    "Latitude",
    "Temperature",
    "Humidity",
    "UV",
    "Battery Voltage",
    "Gps Lock",
    "Gps Disabled",
    "ssid1",
    "bssid1",
    "rssi1",
    "ssid2",
    "bssid2",
    "rssi2",
    "ssid3",
    "bssid3",
    "rssi3",
    "ssid4",
    "bssid4",
    "rssi4",
    "ssid5",
    "bssid5",
    "rssi5"
  };

  /**
   * Reads from a CSV with given filename and produces a list of lines read Each line includes: a
   * timestamp in Unix format (e.g. 1525581237) group id (e.g. /engg4810_2018/g47) base64 encoded
   * string of the data recorded
   *
   * @param filename - a string of the name to search for and read
   * @return A list of data for each line in the csv
   * @throws IOException - if file cannot be found
   */
  public static ArrayList<String[]> read(String filename) throws IOException {
    ArrayList<String[]> csvList = new ArrayList<String[]>();
    Reader systemReader = null;
    try {
      systemReader = new FileReader(filename);
      Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(systemReader);
      for (CSVRecord record : records) {
        String[] csvLine = new String[3];
        csvLine[0] = record.get(0);
        csvLine[1] = record.get(1);
        csvLine[2] = new String(Base64.getDecoder().decode(record.get(2)));
        System.out.println(
            "Timestamp: "
                + csvLine[0]
                + "\r\n "
                + "Group id: "
                + csvLine[1]
                + "\r\n Data: "
                + csvLine[2]);
        csvList.add(csvLine);
        // systemReader.close();
      }
    } catch (Exception e) {
      System.out.println("CSV read error, file not found");
    } finally {
      try {
        systemReader.close();
      } catch (Exception e) {
        System.out.println("Error while closing CSV");
      }
    }

    return csvList;
  }

  /**
   * Writes a CSV file of the provided tree map of the data points collected so far
   *
   * <p>CSV file name is fixed as engg4810g47.csv
   *
   * <p>Each line denotes a unique reading from the microcontroller
   *
   * @param <E>
   * @param dataPoints
   */
  public static void write(TreeMap<String, DataPointStruct> dataPoints, String fileName) {
    FileWriter fileWriter = null;
    CSVPrinter csvFilePrinter = null;

    // Create the CSVFormat object with "\n" as a record delimiter
    CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

    try {

      // initialize FileWriter object
      fileWriter = new FileWriter(fileName);
      // initialize CSVPrinter object
      csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
      // Create CSV file header
      csvFilePrinter.printRecord(FILE_HEADER);

      // Write a new student object list to the CSV file
      for (DataPointStruct dp : dataPoints.values()) {
        String[] wifiList = CSV.createWifiList(dp);
        csvFilePrinter.printRecord(
            dp.deviceTime,
            dp.longitude,
            dp.latitude,
            dp.temperature,
            dp.humidity,
            dp.uv,
            dp.batteryVoltage,
            dp.gpsLock,
            dp.gpsDisabled,
            wifiList[0],
            wifiList[1],
            wifiList[2],
            wifiList[3],
            wifiList[4],
            wifiList[5],
            wifiList[6],
            wifiList[7],
            wifiList[8],
            wifiList[9],
            wifiList[10],
            wifiList[11],
            wifiList[12],
            wifiList[13],
            wifiList[14]);
      }

      System.out.println("CSV File was saved to " + fileName);

    } catch (Exception e) {
      System.out.println("Csv Write error, make sure file is closed");
      e.printStackTrace();
    } finally {
      try {
        fileWriter.flush();
        fileWriter.close();
        csvFilePrinter.close();
      } catch (IOException e) {
        System.out.println("Error while flushing or closing, make sure file is closed");
        // e.printStackTrace();
      }
    }
  }

  public static String[] createWifiList(DataPointStruct dp) {
    String[] wifiList = new String[15];

    for (int i = 0; i < 15; i = i + 3) {
      wifiList[i] = "No ssid recorded";
      wifiList[i + 1] = "No bssid recorded";
      wifiList[i + 2] = "No rssi recorded";
    }

    for (int i = 0; i < dp.wifiAccessPoints.size(); i++) {
      wifiList[i * 3] = dp.wifiAccessPoints.get(i).ssid;
      wifiList[(i * 3) + 1] = dp.wifiAccessPoints.get(i).bssid;
      wifiList[(i * 3) + 2] = dp.wifiAccessPoints.get(i).rssi;
    }

    return wifiList;
  }

  public static void main(String[] args) throws IOException {
    ArrayList<String[]> list = CSV.read("MQTTtoCSV.csv");
  }
}
