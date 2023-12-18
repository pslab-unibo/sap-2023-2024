package sap.lamp_thing.impl;

import io.vertx.mqtt.MqttClient;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class LampThingMQTTAdapter extends ThingAbstractAdapter<LampThingAPI> {

    private MqttClient client;	
    private final int qos = 1;
	private String host;
	private int port;
	private String thingRequestsTopic;
	private String thingEventsTopic;
	
	public LampThingMQTTAdapter(LampThingAPI model, String host, int port, Vertx vertx) throws Exception {
		super(model, vertx);
		this.host = host;
		this.port = port;
	}
	
	protected void setupAdapter(Promise<Void> promise) {
    	Future<JsonObject> tdf = this.getModel().getTD();
    	tdf.onComplete(td -> {
    		try {
    			String thingId = td.result().getString("id");
		        this.thingRequestsTopic = "things/" + thingId +"/requests";
		        this.thingEventsTopic = "things/"+ thingId  +"/events";
	
		       	client = MqttClient.create(this.getVertx());
		    	client.connect(port, host, c -> {
					log("MQTT adapter connected - topic: " + thingRequestsTopic);
		    		client.publishHandler(s -> {
			    			// System.out.println("There are new message in topic: " + s.topicName());
			    			// System.out.println("Content(as string) of the message: " + s.payload().toString());
			    			// System.out.println("QoS: " + s.qosLevel());
		    		    	this.getVertx().eventBus().publish("requests", new String(s.payload().toString()));    	
		    			})
		    			.subscribe(thingRequestsTopic, qos);		
	
		    		});        	
	
		    	this.getVertx().eventBus().consumer("requests", this::handleRequests);
	
	
				this.getModel().subscribe(ev -> {
					sendMsg(thingEventsTopic, ev);
				});
		        promise.complete();
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        	promise.fail(ex.getMessage());
	        }
    	});
        
	}

	private void handleRequests(Message<String> msg) {
		log("Processing new request: " + msg.body());
    	JsonObject request = new JsonObject(msg.body());
    	String reqType = request.getString("request");
    	String consumerReplyTopic = request.getString("replyTopic");
    	long reqId = request.getLong("reqId");
    	
		JsonObject reply = new JsonObject();
		reply.put("reqId", reqId);
		reply.put("request", reqType);
		
		if (reqType.equals("readState")) {    	
    		reply.put("reply", "ok");
    		Future<String> status = this.getModel().getState();
    		status.onSuccess(res -> {
    			reply.put("state", res);
    			sendMsg(consumerReplyTopic, reply);		
    		});
    	} else if (reqType.equals("readTD")) {    	
    		reply.put("reply", "ok");
    		Future<JsonObject> td = this.getModel().getTD();
    		td.onSuccess(res -> {
    			reply.put("td", res);
    			sendMsg(consumerReplyTopic, reply);		
    		});
    	} else if (reqType.equals("on")) {
    		Future<Void> fut = this.getModel().on();
    		fut.onSuccess(ret -> {
	    		reply.put("reply", "ok");
    			sendMsg(consumerReplyTopic, reply);		
    		});
    	} else if (reqType.equals("off")) {
    		Future<Void> fut = this.getModel().off();
    		fut.onSuccess(ret -> {
	    		reply.put("reply", "ok");
    			sendMsg(consumerReplyTopic, reply);		
    		});
    	} else {
    		reply.put("reply", "error");
			sendMsg(consumerReplyTopic, reply);		
    	}
	}
		
	private void sendMsg(String topic, JsonObject msg) {
		client.publish(topic,
				  Buffer.buffer(msg.encode()),
				  MqttQoS.AT_LEAST_ONCE,
				  false,
				  false);
	}

	protected void log(String msg) {
		System.out.println("[LampThingMQTTAdapter]["+System.currentTimeMillis()+"] " + msg);
	}
	
	void notifyRequest(String req) {
    	this.getVertx().eventBus().publish("requests", req);    	
	}
}
