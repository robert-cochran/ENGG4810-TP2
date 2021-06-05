package software;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Check addLookAt actually creates two separate lookAt's make it modular should be structured like
 * an actual class
 *
 * <p>Structure? create kml this is a blank kml has lookAt campus
 *
 * <p>use appendElement to add each 'level' (i.e. kml and document) method to add points and paths?
 * these are mandatory but will i essentially be creating classes that double up? addCoordinates
 * takes in the data structure automatically creates paths, points and placemarks
 *
 * @author s4313513
 */
public class KmlCreate {

  private ArrayList<Float> longitudeArray;
  private ArrayList<Float> latitudeArray;
  private Element pathsTempCoordinates;
  private Element pathsHumidityCoordinates;
  private Element pathsRssiCoordinates;
  private Element pathsUvCoordinates;
  private Element pathsSkippedCoordinates;
  private Element pathsBatteryVoltageCoordinates;
  private Element pointsCoordinates;
  private Element pointsFolder;
  private Document doc;
  private Integer index = 0;

  /**
   * @throws FileNotFoundException
   * @throws ParserConfigurationException
   * @throws TransformerException
   */
  public KmlCreate()
      throws FileNotFoundException, ParserConfigurationException, TransformerException {
    /*final Kml kml = new Kml();
    kml.createAndSetPlacemark().withName("London, UK").withOpen(Boolean.TRUE).createAndSetPoint()
    		.addToCoordinates(-0.126236, 51.500152);
    kml.marshal(new File("HelloKml.kml"));*/
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    /** root elements */
    doc = docBuilder.newDocument();
    Element kmlElement = doc.createElement("kml");
    kmlElement.setAttribute("xmlns", "http://www.opengis.net/kml/2.2");
    doc.appendChild(kmlElement);
    Element docElement = doc.createElement("Document");
    kmlElement.appendChild(docElement);
    appendElement("name", "TP2", docElement, doc);
    appendElement("open", "1", docElement, doc);
    appendElement("description", "ENGG4810 - KML Sensor Data Visualisation", docElement, doc);
    addStyle(docElement, doc);
    /** Paths Folder */
    Element pathsFolder = appendElement("Folder", "", docElement, doc);
    appendElement("name", "Paths", pathsFolder, doc);
    appendElement(
        "description",
        "Absolute Extruded Path visualizes data in order of reading",
        pathsFolder,
        doc);
    /** Path */
    String basicDescription = "Height represents magnitude of data recorded";
    String style = "#yellowLineGreenPoly";
    pathsTempCoordinates =
        createPlacemark(
            appendElement("Placemark", "", pathsFolder, doc),
            "Temperature",
            basicDescription + "magnitude of temperature",
            style,
            pathsFolder,
            doc);
    pathsHumidityCoordinates =
        createPlacemark(
            appendElement("Placemark", "", pathsFolder, doc),
            "Humidity",
            basicDescription + "magnitude of temperature",
            style,
            pathsFolder,
            doc);
    pathsRssiCoordinates =
        createPlacemark(
            appendElement("Placemark", "", pathsFolder, doc),
            "RSSI",
            basicDescription + "magnitude of RSSI",
            style,
            pathsFolder,
            doc);
    pathsUvCoordinates =
        createPlacemark(
            appendElement("Placemark", "", pathsFolder, doc),
            "UV",
            basicDescription + "whether data was collected inside or outside",
            style,
            pathsFolder,
            doc);
    pathsBatteryVoltageCoordinates =
        createPlacemark(
            appendElement("Placemark", "", pathsFolder, doc),
            "Battery Voltage",
            basicDescription + "magnitude of battery voltage",
            style,
            pathsFolder,
            doc);
    pathsSkippedCoordinates =
        createPlacemark(
            appendElement("Placemark", "", pathsFolder, doc),
            "Skipped Measurements",
            basicDescription + "magnitude of skipped measures at that location",
            style,
            pathsFolder,
            doc);
    /** Points Folder */
    pointsFolder = appendElement("Folder", "", docElement, doc);
    appendElement("name", "Points", pointsFolder, doc);
    appendElement(
        "description",
        "Raw sesnor data included in placemark for each location that data "
            + "was recorded at. Simple tessellated path connects location points in the order "
            + "they were generated.",
        pointsFolder,
        doc);
    /** Points Path */
    pointsCoordinates =
        createPlacemark(
            appendElement("Placemark", "", pointsFolder, doc),
            "Tessellated",
            "Tessellated path connecting points",
            "",
            pointsFolder,
            doc);
    // pointsPlacemarkPath = appendElement("Placemark", "", pointsFolder, doc);
    /*appendElement("name", "Tessellated", pointsPlacemarkPath, doc);
    appendElement("description", "Tessellated path connecting points", pointsPlacemarkPath, doc);
    addLookAt(appendElement("LookAt", "", pointsPlacemarkPath, doc), pointsPlacemarkPath, doc);
    Element pointsLineString = appendElement("LineString", "", pointsPlacemarkPath, doc);
    	appendElement("tessellate", "1", pointsLineString, doc);
    	pointsCoordinates = appendElement("coordinates", "", pointsLineString, doc);*/
  }

  /**
   * Creates a placemark and attaches the text content to the coordinates element provided
   *
   * @param elementPlacemark
   * @param elementName
   * @param description
   * @param elementStyle
   * @param parent
   * @param doc
   * @return
   */
  public Element createPlacemark(
      Element elementPlacemark,
      String elementName,
      String description,
      String elementStyle,
      Element parent,
      Document doc) {
    appendElement("name", elementName, elementPlacemark, doc);
    appendElement("description", description, elementPlacemark, doc);
    addLookAt(appendElement("LookAt", "", elementPlacemark, doc), elementPlacemark, doc);
    appendElement("styleUrl", elementStyle, elementPlacemark, doc);
    Element pathsLineString = appendElement("LineString", "", elementPlacemark, doc);
    appendElement("extrude", "1", pathsLineString, doc);
    appendElement("tessellate", "1", pathsLineString, doc);
    appendElement("altitudeMode", "relativeToGround", pathsLineString, doc);
    return appendElement("coordinates", "", pathsLineString, doc);
  }

  /**
   * writes kml file
   *
   * @param directory
   * @throws TransformerException
   */
  public void writeKML(String directory) throws TransformerException {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(new File(directory));
    transformer.transform(source, result);
    System.out.println("KML File saved to " + directory);
  }

  /**
   * Creates the style that is used to link up placemark's in the 'paths' folder
   *
   * @param docElement
   * @param doc
   */
  private void addStyle(Element docElement, Document doc) {
    /** Green Polygon */
    Element stylePoly = appendElement("Style", "", docElement, doc);
    stylePoly.setAttribute("id", "yellowLineGreenPoly");
    Element lineStyle = appendElement("LineStyle", "", stylePoly, doc);
    appendElement("color", "7f00ffff", lineStyle, doc);
    appendElement("width", "4", lineStyle, doc);
    Element polyStyle = appendElement("PolyStyle", "", stylePoly, doc);
    appendElement("color", "7f00ff00", polyStyle, doc);
    Element styleSun = appendElement("Style", "", docElement, doc);
    styleSun.setAttribute("id", "sunIconForUVData");
    Element iconStyle = appendElement("IconStyle", "", styleSun, doc);
    Element icon = appendElement("Icon", "", iconStyle, doc);
    /** Sun Icon */
    appendElement("href", "http://maps.google.com/mapfiles/kml/pal4/icon33.png", icon, doc);
  }

  /**
   * Provide the name for the child, the text inside and it's parent and it will attach those
   * elements
   *
   * <p>Used to create chain/hierarchy of elements in the KML doc
   *
   * @param elementName
   * @param elementText
   * @param elementParent
   * @param doc
   * @return
   */
  private Element appendElement(
      String elementName, String elementText, Element elementParent, Document doc) {
    Element element = doc.createElement(elementName);
    element.appendChild(doc.createTextNode(elementText));
    elementParent.appendChild(element);
    return element;
  }

  /**
   * Appends new placemark to the kml file
   *
   * @param elementName
   * @param description
   * @param styleUrl
   * @param coordinate
   * @param elementParent
   * @param doc
   */
  private void appendPlacemark(
      String elementName,
      String description,
      String styleUrl,
      String coordinate,
      Element elementParent,
      Document doc) {
    Element placemark = appendElement("Placemark", "", elementParent, doc);
    appendElement("name", elementName, placemark, doc);
    appendElement("description", description, placemark, doc);
    appendElement("styleUrl", styleUrl, placemark, doc);
    Element coordinates = appendElement("Point", "", placemark, doc);
    appendElement("coordinates", coordinate, coordinates, doc);
  }

  /**
   * Where the file positions itself upon opening
   *
   * <p>Currently static data Maybe this can be default but overwritten by first point?
   *
   * @param lookAt
   * @param parent
   * @param document
   */
  private void addLookAt(Element lookAt, Element parent, Document document) {
    appendElement("longitude", "153.016389", lookAt, document);
    appendElement("latitude", "-27.500278", lookAt, document);
    appendElement("altitude", "10", lookAt, document);
    appendElement("heading", "-100", lookAt, document);
    appendElement("tilt", "44", lookAt, document);
    appendElement("range", "300", lookAt, document);
  }

  /**
   * This places a single placemark on the map as well as connecting all the previous placemarks to
   * this new one
   *
   * @param longitude
   * @param latitude
   * @param magnitude
   * @param dataType
   */
  public void addDataPoint(DataPointStruct dp) {
    /*
    	 * pathsTempCoordinates;
    private Element pathsHumidityCoordinates;
    private Element pathsRssiCoordinates;
    private Element pathsUvCoordinates;
    private Element pathsSkippedCoordinates;
    private Element pathsBatteryVoltageCoordinates;
    	 */

    // pathsList
    updateCoordinatesList(pathsTempCoordinates, dp.longitude, dp.latitude, dp.temperature);
    if (dp.wifiAccessPoints.get(0).rssi.contains("No")) {
      updateCoordinatesList(pathsRssiCoordinates, dp.longitude, dp.latitude, "0");
    } else {
      updateCoordinatesList(
          pathsRssiCoordinates, dp.longitude, dp.latitude, dp.wifiAccessPoints.get(0).rssi);
    }
    updateCoordinatesList(pathsUvCoordinates, dp.longitude, dp.latitude, dp.uv);
    updateCoordinatesList(pathsHumidityCoordinates, dp.longitude, dp.latitude, dp.humidity);
    updateCoordinatesList(
        pathsSkippedCoordinates, dp.longitude, dp.latitude, String.valueOf(dp.skippedPackets * 10));
    updateCoordinatesList(
        pathsBatteryVoltageCoordinates, dp.longitude, dp.latitude, dp.batteryVoltage);
    // pointsList
    String pointsCoordinatesList = pointsCoordinates.getTextContent();
    pointsCoordinatesList = pointsCoordinatesList + " " + dp.longitude + "," + dp.latitude + ",0";
    pointsCoordinates.setTextContent(pointsCoordinatesList);
    // placemarks
    String style = "";
    // make style = dataType if thats more efficient, just check this wont fuck up file reading
    boolean lockState = false;
    if (dp.gpsLock.equals("1")) {
      lockState = true;
    }
    boolean disabledState = false;
    if (dp.gpsDisabled.equals("1")) {
      disabledState = true;
    }
    String collectedData =
        "Placemark = "
            + this.index++
            + "\r\n"
            + "TimeStamp = "
            + dp.deviceTime
            + "\r\n"
            + "Longitude = "
            + dp.longitude
            + "\r\n"
            + "Latitude = "
            + dp.latitude
            + "\r\n"
            + "Temperature = "
            + dp.temperature
            + "C\r\n"
            + "Humidity = "
            + dp.humidity
            + "%\r\n"
            + "UVA/UVB = "
            + dp.uv
            + "\r\n"
            + "Battery voltage = "
            + dp.batteryVoltage
            + "%\r\n"
            + "skipped packets = "
            + dp.skippedPackets
            + "\r\n\r\n"
            + "GPS Lock = "
            + lockState
            + "\r\n"
            + "GPS Disabled = "
            + disabledState
            + "\r\n\r\n"
            + "";
    for (int i = 0; i < dp.wifiAccessPoints.size(); i++) {
      collectedData =
          collectedData
              + "ssid = "
              + dp.wifiAccessPoints.get(i).ssid
              + "\r\n"
              + "bssid = "
              + dp.wifiAccessPoints.get(i).bssid
              + "\r\n"
              + "rssi = "
              + dp.wifiAccessPoints.get(i).rssi
              + "\r\n\r\n";
    }
    appendPlacemark(
        "Placemark",
        collectedData,
        "#sunIconForUVData",
        dp.longitude + "," + dp.latitude,
        pointsFolder,
        doc);
  }

  public void updateCoordinatesList(
      Element pathsCoordinates, String longitude, String latitude, String data) {
    String pathsCoordinatesList = pathsCoordinates.getTextContent();
    pathsCoordinatesList = pathsCoordinatesList + " " + longitude + "," + latitude + "," + data;
    pathsCoordinates.setTextContent(pathsCoordinatesList);
  }

  public static void main(String[] args)
      throws FileNotFoundException, TransformerException, ParserConfigurationException {
    /*KmlCreate kml = new KmlCreate();
    kml.addDataPoint("153.016854", "-27.499950", "10", "UV");
    kml.addDataPoint("153.016695", "-27.500172", "5", "");
    kml.addDataPoint("153.016535", "-27.500361", "15", "UV");
    kml.writeKML("h:\\eclipse-workspace\\file.kml");*/

    // pathsCoordinates
    /*153.0142468,-27.4980919,10 " +
    "153.012008,-27.499010,20 " +
    "153.012686,-27.500539,30 " +
    "153.016725,-27.499919,50 " +
    "153.016669,-27.500055,80 " +
    "153.016579,-27.500194,100";*/

  }
}
