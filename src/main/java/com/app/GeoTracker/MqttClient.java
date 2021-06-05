package software;

import java.util.concurrent.TimeUnit;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

/**
 * Input: ? Output: ArrayList<DataStructure> of all recorded points
 *
 * <p>only have access to the topics beginning with /engg4810_2018/
 *
 * <p>use mqtt.fx client to test
 *
 * <p>use # to display all messages
 *
 * @author s4313513
 */
public class MqttClient {
  private BlockingConnection connection;
  private String url = "tcp://tp-mqtt.zones.eait.uq.edu.au:1883";
  private String topic = "/engg4810_2018/g47"; // g47";
  private String username = "engg4810_2018";
  private String password = "blpc7n2DYExpBGY5BP7";

  public MqttClient() throws Exception {
    // setup
    System.out.println("Connecting to Broker1 using MQTT");
    MQTT mqtt = new MQTT();
    mqtt.setHost(url);
    mqtt.setUserName(username);
    mqtt.setPassword(password);
    // connection
    connection = mqtt.blockingConnection();
    System.out.println("Initiating connection");
    connection.connect();
    System.out.println("Connected to " + url);
    Topic[] topics = {new Topic(topic, QoS.AT_LEAST_ONCE)};
    System.out.println("Subscribing to " + topic);
    connection.subscribe(topics);
  }

  /**
   * @param message
   * @param sendCount
   * @throws Exception
   */
  public void MqttMsgSend(String message, int sendCount) throws Exception {
    int index = 0;
    while (index < sendCount) {
      connection.publish("/engg4810_2018/g47", message.getBytes(), QoS.AT_LEAST_ONCE, false);
      index++;
    }
  }

  public Message MqttMsgRcv() throws Exception {
    return connection.receive(3, TimeUnit.SECONDS);
  }

  public static void main(String[] args) throws Exception {}
}
