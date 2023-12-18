package sap.lamp_dt.impl;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;

/**
 * Lamp DT in-bound Shadowing module, based on MQTT protocol
 * 
 * @author aricci
 *
 */
public class LampDTShadMQTT  {

	private Vertx vertx;

    private MqttClient client;	
    private final int qos = 1;
	private String host;
	private int port;
	
	private LampDTShadAPI model;
	private String dtId;
	private String thingId;
	
	private String shadowingTopicFromPAtoDT;
	private String shadowingTopicFromDTtoPA;
	
		
	public LampDTShadMQTT(LampDTShadAPI model, String dtId, String thingId, String host, int port) throws Exception {
		this.model = model;
		this.thingId = thingId;
		this.dtId = dtId;
		this.host = host;
		this.port = port;
	}
	
	
	public Future<Void> setup(Vertx vertx) {
		this.vertx = vertx;
		Promise<Void> promise = Promise.promise();
		client = MqttClient.create(vertx);
        this.shadowingTopicFromPAtoDT = "dt/" +  dtId + "/shadowing";
        this.shadowingTopicFromDTtoPA = "pa/" +  thingId +"/shadowing";

		client.connect(port, host, c -> {
			log("MQTT DT shadowing module connected - in topic: " + shadowingTopicFromPAtoDT + " - out topic: " + shadowingTopicFromDTtoPA);
			
			/* PA shad messages */
			client.publishHandler(s -> {
				
		    	this.vertx.eventBus().publish("shadowing", new String(s.payload().toString()));    	
			})
			.subscribe(shadowingTopicFromPAtoDT, qos);

	        /* handler to process MQTT msgs on the event-loop */ 
	        vertx.eventBus().consumer("shadowing", this::handleShadowing);   
	        
	        /* initial sync request */
	        JsonObject msgSync = new JsonObject();
	        msgSync.put("type", "syncRequest");
	        sendMsg(shadowingTopicFromDTtoPA, msgSync);
	        
	        promise.complete();

		});
		
		
        return promise.future();
	        
	}

	/*
	public Future<String> getState() {
		Promise<String> promise = Promise.promise();
		JsonObject req = new JsonObject();
		reqId++;
		req.put("request", "readState");
		req.put("reqId", reqId);
		req.put("replyTopic", consumerRepliesTopic);
		this.sendMsg(this.thingRequestsTopic, req);
		pendingReqs.put(reqId, promise);
		return promise.future();
	}
	
	public Future<Void> on() {
		Promise<Void> promise = Promise.promise();
		JsonObject req = new JsonObject();
		reqId++;
		req.put("request", "on");
		req.put("reqId", reqId);
		req.put("replyTopic", consumerRepliesTopic);
		this.sendMsg(this.thingRequestsTopic, req);
		pendingReqs.put(reqId, promise);
		return promise.future();
	}
	
	public Future<Void> off() {
		Promise<Void> promise = Promise.promise();
		JsonObject req = new JsonObject();
		reqId++;
		req.put("request", "off");
		req.put("reqId", reqId);
		req.put("replyTopic", consumerRepliesTopic);
		this.sendMsg(this.thingRequestsTopic, req);
		pendingReqs.put(reqId, promise);
		return promise.future();
	}

	public Future<Void> subscribe(Handler<JsonObject> handler) {        
		Promise<Void> promise = Promise.promise();
		client.publishHandler(s -> {
			JsonObject obj = new JsonObject(s.payload().toString());
    		handler.handle(obj);
		})
		.subscribe(thingEventsTopic, qos);
		promise.complete();
        return promise.future();
	}
		
	
		
	void notifyReply(JsonObject reply) {
    	vertx.eventBus().publish("replies", reply);    	
	}

	void notifyEvent(JsonObject ev) {
    	vertx.eventBus().publish("events", ev);    	
	}
	 */
	
	private void sendMsg(String topic, JsonObject msg) {
		client.publish(topic,
				  Buffer.buffer(msg.encode()),
				  MqttQoS.AT_LEAST_ONCE,
				  false,
				  false);
	}
	
	private void handleShadowing(Message<String> msg) {
		// {"event":"stateChanged","data":{"state":"on"},"timestamp":1666016531655}
		log("Processing new shadowing: " + msg.body());
		JsonObject obj = new JsonObject(msg.body());
		String msgType = obj.getString("msg");
		if (msgType.equals("sync-event")) {
			JsonObject syncData = obj.getJsonObject("syncData");
			String ev = syncData.getString("event");
			long timeStamp = syncData.getLong("timestamp");
			if (ev.equals("stateChanged")) {
				JsonObject data = syncData.getJsonObject("data");
				String state = data.getString("state");
				this.model.updateState(state, timeStamp);
			} else {
				log("unknown shadowing event: " + ev);
			}
		} else if (msgType.equals("sync-state")) {
			JsonObject syncData = obj.getJsonObject("syncData");
			long timeStamp = syncData.getLong("timestamp");
			String state = syncData.getString("state");
			this.model.updateState(state, timeStamp);
		} else {
			log("unknown msg: " + msgType);
		}
	}	
	
	protected void log(String msg) {
		System.out.println("[LampDTShadMQTT]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
