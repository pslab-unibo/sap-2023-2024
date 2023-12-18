package sap.lamp_dt.consumer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class VanillaLampDTConsumerAgent extends AbstractVerticle {

	
	private LampDTAPI lampDT;
	private int nEventsReceived;
	
	public VanillaLampDTConsumerAgent(LampDTAPI thing) {
		this.lampDT = thing; 
		nEventsReceived = 0;
	}
	
	/**
	 * Main agent body.
	 */
	public void start(Promise<Void> startPromise) throws Exception {
		log("Lamp consumer agent started.");		
		
		log("Getting the status...");		

		lampDT.getState()
			.onSuccess(res -> {
				log("State: " + res);			
			})
			.onFailure(err -> {
				log("Failure " + err);
			});
	
		lampDT
			.subscribe(this::onNewEvent)
			.onComplete(res3 -> {
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
		System.out.println("[LampDTConsumerAgent]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
