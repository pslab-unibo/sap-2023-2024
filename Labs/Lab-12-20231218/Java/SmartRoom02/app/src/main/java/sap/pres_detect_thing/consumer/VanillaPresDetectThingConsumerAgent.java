package sap.pres_detect_thing.consumer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class VanillaPresDetectThingConsumerAgent extends AbstractVerticle {

	
	private PresDetectThingAPI thing;
	private int nEventsReceived;
	
	public VanillaPresDetectThingConsumerAgent(PresDetectThingAPI thing) {
		this.thing = thing; 
		nEventsReceived = 0;
	}
	
	/**
	 * Main agent body.
	 */
	public void start(Promise<Void> startPromise) throws Exception {
		log("Presence Detection consumer agent started.");		
		
		log("Getting the status...");		
		Future<Boolean> detected = thing.isDetected();

		Future<Void> subscribeRes = detected.compose(res -> {
			log("Status: " + res);			
			log("Subscribing...");
			return thing.subscribe(this::onNewEvent);
		}).onFailure(err -> {
			log("Failure " + err);
		});
		
		subscribeRes.onComplete(res3 -> {
			log("Subscribed!");
		});		
	}
	
	/**
	 * Handler to process observed events  
	 */
	protected void onNewEvent(JsonObject ev) {
		nEventsReceived++;
		log("New event: \n " + ev.toString() + "\nNum events received: " + nEventsReceived);
	}
	
	protected void log(String msg) {
		System.out.println("[PresDetectThingConsumerAgent]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
