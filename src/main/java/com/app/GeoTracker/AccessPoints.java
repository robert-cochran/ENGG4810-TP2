package software;

/**
 * GPSandRSSI constructor
 *
 * <p>AccessPoints constructor addGPSandRSSI updateNodeList rssiToMetres GpsEstimationTrilateration
 * param: x1,2,3 y1,2,3 r1,2,3 returns: lat. long centroidPoint (deprecated?)
 */
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

/**
 * Input: ArrayList of longitude and latitudes recorded Output: Updated ArrayList of all incomplete
 * coordinates extrapolated/interpolated
 *
 * <p>Averages output equidistant between points and extrapolates for points at end of list
 *
 * <p>How is RSSI used?
 *
 * <p>Test Cases:
 *
 * <p>Large list 0 gps coordinates given 1 gps coordinate given start last middle 2 gps coordinates
 * given start last start 2nd start middle 2nd middle middle middle+1 end-1 end 3 coords All but one
 *
 * <p>Small list 1 coord no gps 1 coord w/gps 2 coords no gps 2 coords 1 gps 2 coords 2 gps
 *
 * <p>Empty list
 */

/**
 * This is intended to only be used with AccessPoint
 *
 * <p>Holds a single reading of an rssi and the gps that the reading was taken at
 *
 * @author s4313513
 */
class GPSandRSSI {

  String longitude;
  String latitude;
  String rssi;

  GPSandRSSI(String latitude, String longitude, String rssi) {
    this.longitude = longitude;
    this.latitude = latitude;
    this.rssi = rssi;
  }
}

/**
 * This is intended to be used as a static list, it wont change, only be updated
 *
 * <p>So unlike the availableWifi class, this will not produce identical objects
 *
 * <p>All objects of AccessPoint should be unique (identified by their bssid)
 *
 * @author s4313513
 */
class AccessPoint {

  public String ssid;
  public String bssid;
  /**
   * Checks for duplicate of all previous points in a single reading TreeMap used so that no two
   * readings should have the same gps, thus potential to provide different rssi's and cause chaos
   * Key for tree map is a gps of [lat,long]
   */
  public TreeMap<String, GPSandRSSI> gpsAndRssiReadingsMap;

  public String estimatedLatitude;
  public String estimatedLongitude;
  // private ArrayList<DataPointStruct> coordinateList;

  AccessPoint(String ssid, String bssid) {
    this.ssid = ssid;
    this.bssid = bssid;
    this.gpsAndRssiReadingsMap = new TreeMap<String, GPSandRSSI>();
    this.estimatedLatitude = "null";
    this.estimatedLongitude = "null";
  }
}

/**
 * Class provides a way to interact with AccessPoint
 *
 * <p>Allows for adding of newly collected wifiPoints to the AccessPoint list
 *
 * @author s4313513
 */
public class AccessPoints {

  private TreeMap<String, AccessPoint> uniqueBssidMap;

  public AccessPoints() {
    this.uniqueBssidMap = new TreeMap<String, AccessPoint>();
  }

  public void addAccessPointData(
      String ssid, String bssid, String rssi, String latitude, String longitude) {
    // checks if mac address has already been added
    if (!uniqueBssidMap.containsKey(bssid)) {
      System.out.println("access point created");
      uniqueBssidMap.put(bssid, new AccessPoint(ssid, bssid));
    }
    String gpsKey = latitude + longitude;
    // if lat and long already exist as a pair in the Map, they wont be added
    GPSandRSSI gpsAndRssi = new GPSandRSSI(latitude, longitude, rssi);
    if (uniqueBssidMap.get(bssid).gpsAndRssiReadingsMap.put(gpsKey, gpsAndRssi) == null) {
      System.out.println("gps and rssi added to access point");
    }

    // now that the GPSandRSSIlist has one more value, see if it can estimate (or update) a gps
    if (uniqueBssidMap.get(bssid).gpsAndRssiReadingsMap.size() > 2) {
      System.out.println("estimating GPS for an access point");
      estimateBssidPointGPS(bssid);
    }
  }

  /**
   * Runs every time a node is added (access point = node)
   *
   * <p>Node/AccessPoint List Collection of mac addresses to rssi's with the GPS it was taken at
   *
   * <p>should also save an estimated gps after enough data collection
   *
   * <p>needs to check for 3 readings
   *
   * <p>will be updated continuously if more readings can improve accuracy
   *
   * <p>how will estimated gps locations be retrieved?
   *
   * <p>need to use AvailableWifi from given datastructure and long lat
   *
   * <p>Assumption: Long/Lat given are correct, i.e. a lock has been established
   *
   * @param degrees
   * @return
   */
  private void estimateBssidPointGPS(String bssid) {
    ArrayList<String[]> gpsRssiPoints = new ArrayList<String[]>();
    // search for unique accessPoint from AccessPoints with given bssid
    // example router in axon209
    AccessPoint bssidPoint = this.uniqueBssidMap.get(bssid);
    // this returns a set of unique gps positions in [lat, long]
    // this is all the gps positions that recorded the axon209 router for example and the rssi at
    // that point
    Set<String> gpsKey = bssidPoint.gpsAndRssiReadingsMap.keySet();
    // for each unique [lat,long] in gpsAndRssiMap/gpsKey, we want the rssi that goes with that
    // find all the gps and rssi's it has
    for (String gps : gpsKey) {
      TreeMap<String, GPSandRSSI> gpsAndRssiPoint = uniqueBssidMap.get(bssid).gpsAndRssiReadingsMap;
      String[] gpsRssiPoint = new String[3];
      gpsRssiPoint[0] = gpsAndRssiPoint.get(gps).latitude;
      gpsRssiPoint[1] = gpsAndRssiPoint.get(gps).longitude;
      gpsRssiPoint[2] = gpsAndRssiPoint.get(gps).rssi;
      gpsRssiPoints.add(gpsRssiPoint);
    }

    int oneDegreeInMetres = 111319;
    System.out.println(
        Double.valueOf(gpsRssiPoints.get(0)[0]) + " " + Double.valueOf(gpsRssiPoints.get(1)[0]));
    double lat1metres =
        (Double.valueOf(gpsRssiPoints.get(0)[0]) - Double.valueOf(gpsRssiPoints.get(1)[0]))
            * oneDegreeInMetres; // lat0 - lat1
    double lat2metres =
        (Double.valueOf(gpsRssiPoints.get(0)[0]) - Double.valueOf(gpsRssiPoints.get(2)[0]))
            * oneDegreeInMetres; // lat0 - lat2

    double lon1metres =
        (Double.valueOf(gpsRssiPoints.get(0)[1]) - Double.valueOf(gpsRssiPoints.get(1)[1]))
            * oneDegreeInMetres; // lon0 - lon1
    double lon2metres =
        (Double.valueOf(gpsRssiPoints.get(0)[1]) - Double.valueOf(gpsRssiPoints.get(2)[1]))
            * oneDegreeInMetres; // lon0 - lon2

    double rssi0 = rssiToMetres(Integer.valueOf(gpsRssiPoints.get(0)[2]) * (-1));
    double rssi1 = rssiToMetres(Integer.valueOf(gpsRssiPoints.get(1)[2]) * (-1));
    double rssi2 = rssiToMetres(Integer.valueOf(gpsRssiPoints.get(2)[2]) * (-1));

    ArrayList<Double> gps =
        GpsEstimationTrilateration(
            0.0, 0.0, rssi0, lat1metres, lon1metres, rssi1, lat2metres, lon2metres, rssi2);
    double estimatedLatitude =
        gps.get(0) / oneDegreeInMetres + Double.valueOf(gpsRssiPoints.get(0)[0]);
    double estimatedLongitude =
        gps.get(1) / oneDegreeInMetres + Double.valueOf(gpsRssiPoints.get(0)[1]);

    // save it to estimatedLat, estimatedLong
    this.uniqueBssidMap.get(bssid).estimatedLongitude = String.valueOf(estimatedLongitude);
    this.uniqueBssidMap.get(bssid).estimatedLatitude = String.valueOf(estimatedLatitude);
    System.out.println(bssidPoint.estimatedLongitude);
  }

  /**
   * Needs to be given the list of wifi access points that were taken for that position
   *
   * <p>Assumption: If we are here then the latest update to the GPS has been done already
   *
   * @return a String[] of latLong coords, will return 0 for both if: - there arent 3 unique bssid's
   *     provided - there arent 3 gps positions estimated for each bssid - there couldnt be a gps
   *     positioned estimated (some error was thrown instead :( )
   */
  public String[] estimateCurrentGPS(ArrayList<AvailableWifi> availableWifiPoints) {
    ArrayList<String[]> availableWifiWithEstimatedGPS = new ArrayList<String[]>();
    ArrayList<Double> estimatedPosition = null;
    // for each bssid in list
    // find corresponding AccessPoint
    for (AvailableWifi wifiPoint : availableWifiPoints) {
      // see if AccessPoint has an estimated Position (String != null)
      // this.uniqueBssidMap.get(wifiPoint.bssid);//.estimatedLatitude.equals("2");
      if (this.uniqueBssidMap.get(wifiPoint.bssid).estimatedLatitude != null) {
        // if it does, save the estimatedGPS to a collection (list?)
        // each item in collection will have estimated latitude/longitude and the rssi for this
        // reading
        AccessPoint accessPointWithGPS = uniqueBssidMap.get(wifiPoint.bssid);
        String[] GPSandRSSI = new String[3];
        GPSandRSSI[0] = accessPointWithGPS.estimatedLatitude;
        GPSandRSSI[1] = accessPointWithGPS.estimatedLongitude;
        GPSandRSSI[2] = wifiPoint.rssi;
        availableWifiWithEstimatedGPS.add(GPSandRSSI);
      }
    }

    String[] estimatedGPS = new String[2];
    // if the by the end of the list there are at least 3 points with a gps (length of collection is
    // 3 or more)
    if (availableWifiWithEstimatedGPS.size() > 2) {
      // run GPSEstimationTrilateration with the wifiPoints and the rssi attached to those points
      String[] accessPoint0GpsRssi = availableWifiWithEstimatedGPS.get(0);
      String[] accessPoint1GpsRssi = availableWifiWithEstimatedGPS.get(1);
      String[] accessPoint2GpsRssi = availableWifiWithEstimatedGPS.get(2);

      // x = long, y = lat, long first, lat second
      //			estimatedPosition = GpsEstimationTrilateration(
      //					Double.valueOf(accessPoint0GpsRssi[1]), (Double.valueOf(accessPoint0GpsRssi[0])),
      // (Double.valueOf(accessPoint0GpsRssi[2])),
      //							(Double.valueOf(accessPoint1GpsRssi[1])), (Double.valueOf(accessPoint1GpsRssi[0])),
      // (Double.valueOf(accessPoint1GpsRssi[2])),
      //									(Double.valueOf(accessPoint2GpsRssi[1])), (Double.valueOf(accessPoint2GpsRssi[0])),
      // (Double.valueOf(accessPoint2GpsRssi[2])));

      int oneDegreeInMetres = 111319;

      double lat1metres =
          (Double.valueOf(accessPoint0GpsRssi[0]) - Double.valueOf(accessPoint1GpsRssi[0]))
              * oneDegreeInMetres; // lat0 - lat1
      double lat2metres =
          (Double.valueOf(accessPoint0GpsRssi[0]) - Double.valueOf(accessPoint2GpsRssi[0]))
              * oneDegreeInMetres; // lat0 - lat2

      double lon1metres =
          (Double.valueOf(accessPoint0GpsRssi[1]) - Double.valueOf(accessPoint1GpsRssi[1]))
              * oneDegreeInMetres; // lon0 - lon1
      double lon2metres =
          (Double.valueOf(accessPoint0GpsRssi[1]) - Double.valueOf(accessPoint2GpsRssi[1]))
              * oneDegreeInMetres; // lon0 - lon2

      double rssi0 = rssiToMetres(Integer.valueOf(accessPoint0GpsRssi[2]) * (-1));
      double rssi1 = rssiToMetres(Integer.valueOf(accessPoint1GpsRssi[2]) * (-1));
      double rssi2 = rssiToMetres(Integer.valueOf(accessPoint2GpsRssi[2]) * (-1));

      ArrayList<Double> gps =
          GpsEstimationTrilateration(
              0.0, 0.0, rssi0, lat1metres, lon1metres, rssi1, lat2metres, lon2metres, rssi2);
      double estimatedLatitude =
          gps.get(0) / oneDegreeInMetres + Double.valueOf(accessPoint0GpsRssi[0]);
      double estimatedLongitude =
          gps.get(1) / oneDegreeInMetres + Double.valueOf(accessPoint0GpsRssi[1]);

      // save it to estimatedLat, estimatedLong
      estimatedGPS[1] = String.valueOf(estimatedLongitude);
      estimatedGPS[0] = String.valueOf(estimatedLatitude);
      // System.out.println(bssidPoint.estimatedLongitude);
    } else {
      estimatedGPS[1] = "0";
      estimatedGPS[0] = "0";
    }

    // if more than 3 applicable points, either:
    // pick first 3 or
    // write a function to recalculate given an extra point
    // so just a single parameter to the function, not sure this would work
    // recalculate the function 3 times if given one extra point and return the centroid

    return estimatedGPS;
  }

  private static double degreesToRadians(double degrees) {
    return degrees * Math.PI / 180;
  }

  private static double metresBetweenCoordinates(
      double lat1, double lon1, double lat2, double lon2) {
    int earthRadiusKm = 6371;

    double dLat = degreesToRadians(lat2 - lat1);
    double dLon = degreesToRadians(lon2 - lon1);

    lat1 = degreesToRadians(lat1);
    lat2 = degreesToRadians(lat2);

    double a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return earthRadiusKm * c * 1000;
  }

  private static double rssiToMetres(int rssi) {
    return (0.0146 * rssi * rssi + 0.4693 * rssi + 3.289);
  }

  private static ArrayList<Double> GpsEstimationTrilateration(
      Double x1,
      Double y1,
      Double r1,
      Double x2,
      Double y2,
      Double r2,
      Double x3,
      Double y3,
      Double r3) {
    ArrayList<Double> unknownGPS = new ArrayList<Double>();
    double A = (-2 * x1 + 2 * x2);
    double B = (-2 * y1 + 2 * y2);
    double C = (r1 * r1) - (r2 * r2) - (x1 * x1) + (x2 * x2) - (y1 * y1) + (y2 * y2);
    double D = (-2 * x2 + 2 * x3);
    double E = (-2 * y2 + 2 * y3);
    double F = (r2 * r2) - (r3 * r3) - (x2 * x2) + (x3 * x3) - (y2 * y2) + (y3 * y3);
    double x = ((C * E) - (F * B)) / ((E * A) - (B * D));
    double y = ((C * D) - (A * F)) / ((B * D) - (A * E));
    System.out.println(A + " " + B + " " + C + " " + " " + D + " " + E + " " + F);
    System.out.println("x = " + x + " y = " + y);
    unknownGPS.add(x);
    unknownGPS.add(y);
    return unknownGPS;
  }

  //	private static ArrayList<Double> centroidPoint(ArrayList<Double> routerGPS1and3,
  //			ArrayList<Double> routerGPS1and2, ArrayList<Double> routerGPS3and2){
  //		ArrayList<Double> centroidPoint = new ArrayList<Double>();
  //		double centroidLat = (routerGPS1and3.get(0)+routerGPS1and2.get(0) + routerGPS3and2.get(0)) /
  // 3;
  //		centroidPoint.add(centroidLat);
  //		double centroidLong = (routerGPS1and3.get(1)+routerGPS1and2.get(1) + routerGPS3and2.get(1)) /
  // 3;
  //		centroidPoint.add(centroidLong);
  //		return centroidPoint;
  //	}

  public static ArrayList<Double> GpsEstimationTriangulation(
      double lat1, double long1, int rssi1, double lat2, double long2, int rssi2) {
    ArrayList<Double> routerGPS = new ArrayList<Double>();
    int oneDegreeInMetres = 111319;
    double rssi1metres = rssiToMetres(rssi1);
    double rssi2metres = rssiToMetres(rssi2);
    double xlong = Math.abs(lat1 - lat2) * oneDegreeInMetres;
    double ylat = Math.abs(long1 - long2) * oneDegreeInMetres; // check that this is right
    double z = Math.sqrt(xlong * xlong + ylat * ylat);
    double theta = (-1 * (rssi2metres * rssi2metres)) + (z * z) + (rssi1metres * rssi1metres);
    theta = theta / (2 * z * rssi1metres);
    theta = Math.acos(theta); // whats causing thetas>1?
    double routerRelativeLatDegrees =
        (rssi2metres * Math.cos(theta))
            / oneDegreeInMetres; // is this supposed to be rssi1 or 2? what about 111139
    double routerRelativeLongDegrees = (rssi1metres * Math.sin(theta)) / oneDegreeInMetres;
    double routerLat = routerRelativeLatDegrees + lat1;
    double routerLong = routerRelativeLongDegrees + long2; // is this supposed to be long2 or 1?
    routerGPS.add(routerLat);
    routerGPS.add(routerLong);
    return routerGPS;
  }

  public static void main(String[] args) {

    // System.out.println(gpsReverse.get(0) + " " + gpsReverse.get(1));
    //		System.out.println(rssiToMetres(-68));
    //		ArrayList<Double> routerGPS1and3 = GpsEstimationTriangulation(-27.50012, 153.014, -68,
    // -27.50003, 153.0145, -52); //69.93m
    //		System.out.println(metresBetweenCoordinates(-27.50012, 153.014, -27.50003, 153.0145));
    //		System.out.println(metresBetweenCoordinates(-27.49998, 153.0145, routerGPS1and3.get(0),
    // routerGPS1and3.get(1))); //4.5m
    //		ArrayList<Double> routerGPS1and2 = GpsEstimationTriangulation(-27.50012, 153.014, -68,
    // -27.50023, 153.014, -78); //4.5m
    //		System.out.println(metresBetweenCoordinates(-27.49998, 153.0145, routerGPS1and2.get(0),
    // routerGPS1and2.get(1)));
    //		ArrayList<Double> routerGPS3and2 = GpsEstimationTriangulation(-27.50003, 153.0145, -52,
    // -27.50023, 153.014, -78); //45.38m
    //		System.out.println(metresBetweenCoordinates(-27.49998, 153.0145, routerGPS3and2.get(0),
    // routerGPS3and2.get(1)));
    //		ArrayList<Double> centroidPoint = centroidPoint(routerGPS1and3, routerGPS1and2,
    // routerGPS3and2);
    //		System.out.println(metresBetweenCoordinates(-27.49998, 153.0145, centroidPoint.get(0),
    // centroidPoint.get(1))); //24.882m

    // test that it enters bssid and no duplicates
    // that that it enters unique values for bssid

    // System.out.println(rssiToMetres(20));

    String ssid0 = "axon29";
    String bssid0 = "00-14-22-01-23-45";
    String rssi0 = "068";
    String lat0 = "27.50012";
    String long0 = "153.014";

    String ssid1 = "uqWifi";
    String bssid1 = "00-14-22-01-23-44";
    String rssi1 = "078";
    String lat1 = "27.50023";
    String long1 = "153.014";

    String ssid2 = "eduroam";
    String bssid2 = "00-14-22-01-23-47";
    String rssi2 = "052";
    String lat2 = "27.50003";
    String long2 = "153.0145";

    // need to have 0,0 for lat0, long0
    // need to have distance between lat0 and lat1 for lat1 position
    int oneDegreeInMetres = 111319;
    double lat1metres = (27.50012 - 27.50023) * oneDegreeInMetres; // lat0 - lat1
    double lat2metres = (27.50012 - 27.50003) * oneDegreeInMetres; // lat0 - lat2
    System.out.println(lat1metres + " " + lat2metres);

    double lon1metres = (153.014 - 153.014) * oneDegreeInMetres; // lon0 - lon1
    double lon2metres = (153.014 - 153.0145) * oneDegreeInMetres; // lon0 - lon2
    System.out.println(lon1metres + " " + lon2metres);

    //		AccessPoints accessPoints = new AccessPoints();
    //		//System.out.println("0");
    //		accessPoints.addAccessPointData(ssid0, bssid0, rssi0 , lat0, long0);
    //		//this shouldnt work because they have the same gps
    //		//System.out.println("1");
    //		accessPoints.addAccessPointData(ssid0, bssid0, rssi1, lat1, long1);
    //		//System.out.println("2");
    //		accessPoints.addAccessPointData(ssid0, bssid0, rssi2, lat2, long2);
    // double metresLon1 = metresBetweenCoordinates(153, 153.014, 27.50023,153.014);
    // double metresLon2 = metresBetweenCoordinates(27.50012, 153.014, 27.50023,153.014);

    ArrayList<Double> gps =
        GpsEstimationTrilateration(
            0.0,
            0.0,
            rssiToMetres(68),
            lat1metres,
            lon1metres,
            rssiToMetres(78),
            lat2metres,
            lon2metres,
            rssiToMetres(52));
    double x = gps.get(0) / oneDegreeInMetres + 27.50012;
    double y = gps.get(1) / oneDegreeInMetres + 153.014;
    System.out.println(x + " " + y);

    // accessPoints.addAccessPointData(ssid1, bssid1, rssi0, lat2, long2);
    // accessPoints.addAccessPointData(ssid1, bssid1, rssi0, lat2, long2);
    // accessPoints.addAccessPointData(ssid1, bssid0, rssi0, lat2, long2);

    // ArrayList<AvailableWifi> wifis = new ArrayList<AvailableWifi>();

    // wifis.add(new AvailableWifi(ssid0, bssid0, rssi0));
    // wifis.add(new AvailableWifi(ssid1, bssid1, rssi1));
    // wifis.add(new AvailableWifi(ssid2, bssid2, rssi2));
    // accessPoints.estimateCurrentGPS(wifis);

    // test run
    // addAccessPoint(String ssid, String bssid, String rssi, String latitude, String longitude) x 3
    // w/different lat/long and x3 again for different bssid's

    AccessPoints accessPoints = new AccessPoints();
    // System.out.println("0");
    accessPoints.addAccessPointData(ssid0, bssid0, rssi0, lat0, long0);
    // this shouldnt work because they have the same gps
    // System.out.println("1");
    accessPoints.addAccessPointData(ssid0, bssid0, rssi1, lat1, long1);
    // System.out.println("2");
    accessPoints.addAccessPointData(ssid0, bssid0, rssi2, lat2, long2);
    System.out.println(accessPoints.uniqueBssidMap.get(bssid0).estimatedLatitude);
  }
}
