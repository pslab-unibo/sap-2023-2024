/*
 * Adapted from Eclipse Paho Client
 * https://www.eclipse.org/paho/index.php?page=clients/java/index.php
 */	
package mqtt_tests;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import io.vertx.core.json.JsonObject;
import sap.smartroom.case2.MsgEvent;


class Sender extends Thread {

    private String topic;
    private int qos;
    private MemoryPersistence persistence = new MemoryPersistence();
    private String broker;
    private String clientId = "sender-000";

    public Sender(String topic, String broker, int qos) {
    	this.broker = broker;
    	this.qos = qos;
    	this.topic = topic;
    }
    
	public void run() {
		try {
	        MqttClient client = new MqttClient(broker, clientId, persistence);
	        MqttConnectOptions connOpts = new MqttConnectOptions();
	        connOpts.setCleanSession(true);
	        log("Connecting to broker: "+broker);
	        client.connect(connOpts);
	        log("Connected");
	        
	        for (int i = 0; i < 10; i++) {
	    		String content = "Hello #" + i;
	    		log("Publishing message: "+content);
	            MqttMessage message = new MqttMessage(content.getBytes());
	            message.setQos(qos);
	            client.publish(topic, message);
	            log("Message " + i + " published");	        	
	        }
	        client.disconnect();
	        log("Disconnected");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void log(String msg) {
		System.out.println("[SENDER] " + msg);
	}
}

public class TestMQTT {
	public static void main(String[] args) {

	    String topic        = "myTopic";
	    int qos             = 2;
	    String broker       = "tcp://localhost:1883";
	    String clientId     = "Receiver-000";
	    MemoryPersistence persistence = new MemoryPersistence();
	
	    
	    try {
	        MqttClient client = new MqttClient(broker, clientId, persistence);
	        MqttConnectOptions connOpts = new MqttConnectOptions();
	        connOpts.setCleanSession(true);
	        System.out.println("[RECEIVER] Connecting to broker: "+broker);
	        client.connect(connOpts);
	        System.out.println("[RECEIVER] Connected");

			client.subscribe(topic, (top, msg) -> {
			    byte[] payload = msg.getPayload();
			    System.out.println("[RECEIVER] " + new String(payload));
			});    

			new Sender(topic, broker, qos).start();
			
			
	    } catch(MqttException me) {
	        System.out.println("reason "+me.getReasonCode());
	        System.out.println("msg "+me.getMessage());
	        System.out.println("loc "+me.getLocalizedMessage());
	        System.out.println("cause "+me.getCause());
	        System.out.println("excep "+me);
	        me.printStackTrace();
	    }
	}
}