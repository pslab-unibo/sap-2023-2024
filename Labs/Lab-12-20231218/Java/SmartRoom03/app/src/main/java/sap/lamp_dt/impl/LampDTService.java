package sap.lamp_dt.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import sap.lamp_dt.impl.LampPAShadMQTT;

/**
 * 
 * Lamp Digital Twin Service 
 * 
 * 
 * @author aricci
 *
 */
public class LampDTService extends AbstractVerticle {

	private LampDTModel model;	
	public static final int HTTP_PORT = 9888;
	public static final int MQTT_PORT = 1883;
	
	public LampDTService(LampDTModel model) {
		this.model = model;
	}
	
	@Override
	public void start(Promise<Void> startPromise) throws Exception {
	

		ArrayList<Future> allFutures = new ArrayList<Future>();		
		try {
			LampDTAdapter httpAdapter = new LampDTAdapter(model, HTTP_PORT, this.getVertx());
			Promise<Void> p = Promise.promise();
			httpAdapter.setupAdapter(p);
			Future<Void> fut = p.future();
			allFutures.add(fut);
			fut.onSuccess(res -> {
				log("HTTP adapter installed.");
			}).onFailure(f -> {
				log("HTTP adapter not installed.");
			});
		} catch (Exception ex) {
			log("HTTP adapter installation failed.");
		}

		try {
			LampDTShadMQTT mqttShad = new LampDTShadMQTT(model, "MyLampDT", "MyLamp", "localhost", MQTT_PORT);
			Future<Void> fut = mqttShad.setup(this.getVertx());
			allFutures.add(fut);
			fut.onSuccess(res -> {
				log("MQTT shad inbound installed.");
			}).onFailure(f -> {
				log("MQTT shad inbkound not installed.");
			});
		} catch (Exception ex) {
			log("MQTT adapter installation failed.");
		}				
		
		CompositeFuture
			.all(allFutures)
			.onSuccess(res -> {
				log("setup ok.");
				startPromise.complete();
			});
	}

	protected void log(String msg) {
		System.out.println("[LampDTService] " + msg);
	}
}
