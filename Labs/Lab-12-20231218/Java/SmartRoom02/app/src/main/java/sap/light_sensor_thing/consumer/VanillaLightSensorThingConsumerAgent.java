package sap.light_sensor_thing.consumer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class VanillaLightSensorThingConsumerAgent extends AbstractVerticle {

	
	private LightSensorThingAPI thing;
	private int nEventsReceived;
	
	public VanillaLightSensorThingConsumerAgent(LightSensorThingAPI thing) {
		this.thing = thing; 
		nEventsReceived = 0;
	}
	
	/**
	 * Main agent body.
	 */
	public void start(Promise<Void> startPromise) throws Exception {
		log("Presence Detection consumer agent started.");		
		
		log("Getting the status...");		
		Future<Double> level = thing.getLightLevel();

		Future<Void> subscribeRes = level.compose(res -> {
			log("Light level: " + res);			
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
