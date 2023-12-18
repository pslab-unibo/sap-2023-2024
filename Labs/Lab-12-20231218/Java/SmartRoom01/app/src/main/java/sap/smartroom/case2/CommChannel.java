package sap.smartroom.case2;

import java.util.UUID;


import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import io.vertx.core.json.JsonObject;
import sap.smartroom.AbstractEventSource;

public class CommChannel extends AbstractEventSource {

	public static final String BROKER_DEFAULT_ADDRESS = "tcp://localhost:1883";
	IMqttClient broker;
	String channelId;

	public CommChannel(String channelName) throws Exception {
		this(BROKER_DEFAULT_ADDRESS, channelName);
	}
	
	public CommChannel(String mqttBrokerAddress, String channelName) throws Exception {
		channelId = UUID.randomUUID().toString();
		broker = new MqttClient(mqttBrokerAddress,channelId);		

		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		broker.connect(options);		

		broker.subscribe(channelName, (topic, msg) -> {
		    byte[] payload = msg.getPayload();
		    JsonObject ev = new JsonObject(new String(payload));
		    long ts = System.currentTimeMillis();
		    this.notifyEvent(new MsgEvent(ts, ev));
		});    
	}

	
	public void sendMsg(String channel, JsonObject msg) {
        byte[] payload = msg.toString().getBytes();        
        MqttMessage mqttMsg = new MqttMessage(payload); 
        mqttMsg.setQos(0);
        mqttMsg.setRetained(true);
        try {
			broker.publish(channel, mqttMsg);
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}        
	}
	
}
