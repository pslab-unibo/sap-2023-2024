package sap.lamp_thing.consumer;

import java.net.URI;
import java.util.HashMap;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;

/**
 * Proxy to interact with a LampThing using MQTT protocol
 * 
 * @author aricci
 *
 */
public class LampThingMQTTProxy implements LampThingAPI {

	private Vertx vertx;

    private MqttClient client;	
    private final int qos = 1;
	private String host;
	private int port;
	
	private String thingId;
	private String consumerId;
	
	private String thingRequestsTopic;
	private String consumerRepliesTopic;
	private String thingEventsTopic;
	
	private long reqId;
	private HashMap<Long,Promise> pendingReqs;

		
	public LampThingMQTTProxy(String consumerId, String thingId, String host, int port) throws Exception {
		this.consumerId = consumerId;
		this.thingId = thingId;
		reqId = 0;
		this.host = host;
		this.port = port;
		pendingReqs = new HashMap<Long,Promise>();
	}
	
	
	public Future<Void> setup(Vertx vertx) {
		this.vertx = vertx;
		Promise<Void> promise = Promise.promise();
		client = MqttClient.create(vertx);
		client.connect(port, host, c -> {
			log("connected - topic: " + thingRequestsTopic);			
			
	        this.thingRequestsTopic = "things/" + thingId + "/requests";
	        this.thingEventsTopic = "things/" + thingId + "/events";
	        this.consumerRepliesTopic = "consumers/" + consumerId + "/replies";

			client.publishHandler(s -> {
    			// System.out.println("There are new message in topic: " + s.topicName());
    			// System.out.println("Content(as string) of the message: " + s.payload().toString());
    			// System.out.println("QoS: " + s.qosLevel());
		    	this.vertx.eventBus().publish("replies", new String(s.payload().toString()));    	
			})
			.subscribe(consumerRepliesTopic, qos);

	        /* handler to process MQTT msgs on the event-loop */ 
	        vertx.eventBus().consumer("replies", this::handleReplies);   
	        
	        promise.complete();

		});
		
		
        return promise.future();
	        
	}

	public Future<JsonObject> getTD() {
		Promise<JsonObject> promise = Promise.promise();
		JsonObject req = new JsonObject();
		reqId++;
		req.put("request", "readTD");
		req.put("reqId", reqId);
		req.put("replyTopic", consumerRepliesTopic);
		this.sendMsg(this.thingRequestsTopic, req);
		pendingReqs.put(reqId, promise);
		return promise.future();
	}
	
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
		

	private void sendMsg(String topic, JsonObject msg) {
		client.publish(topic,
				  Buffer.buffer(msg.encode()),
				  MqttQoS.AT_LEAST_ONCE,
				  false,
				  false);
	}
		
	void notifyReply(JsonObject reply) {
    	vertx.eventBus().publish("replies", reply);    	
	}

	void notifyEvent(JsonObject ev) {
    	vertx.eventBus().publish("events", ev);    	
	}

	/**
	 * Handler to process replies arrived as MQTT messages
	 * 
	 * @param msg
	 */
	private void handleReplies(Message<String> msg) {
		log("Processing new request: " + msg.body());
    	JsonObject reply = new JsonObject(msg.body());
    	long reqId = reply.getLong("reqId");    	
		Promise p = pendingReqs.get(reqId);    	
    	String result = reply.getString("reply");
    	if (result.equals("ok")) {
	    	String reqType = reply.getString("request");
	    	if (reqType.equals("readState")) {
	    		log("Got status: " + reply.getString("state"));
	        	String status = reply.getString("state");
	    		p.complete(status);
	    	} else if (reqType.equals("readTD")) {
	    		log("Got TD: " + reply.getString("state"));
	        	String td = reply.getString("td");
	    		p.complete(new JsonObject(td));
	    	} else{
	    		p.complete();
	    	}
    	} else {
    		p.fail(result);
    	}
	}	
	
	protected void log(String msg) {
		System.out.println("[LampThingMQTTProxy]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
