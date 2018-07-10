package software;

import java.io.FileNotFoundException;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class FileHandling {

  /**
   * Takes in a TreeMap of timeStamps and dataPointStructs and creates a new CSV and KML
   *
   * <p>If a dataPoint does not contain valid gps data, it will not be written
   *
   * @param dataPointMap
   * @throws FileNotFoundException
   * @throws ParserConfigurationException
   * @throws TransformerException
   */
  public static void writeToFile(TreeMap<String, DataPointStruct> dataPointMap)
      throws FileNotFoundException, ParserConfigurationException, TransformerException {
    KmlCreate kml = new KmlCreate();
    Set<String> dataPointSet = dataPointMap.keySet();
    kml = new KmlCreate();
    for (String timeKey : dataPointSet) {
      DataPointStruct dp = dataPointMap.get(timeKey);
      if (!dp.latitude.contains("No GPS")) {
        kml.addDataPoint(dataPointMap.get(timeKey));
      }
    }
    kml.writeKML("UCtoKML.kml");
    CSV.write(dataPointMap, "UCtoCSV.csv");
  }
}
