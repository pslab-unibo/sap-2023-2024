package sap.light_sensor_thing.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import sap.common.ThingAbstractAdapter;

/**
 * 
 * Light Sensor Thing Service 
 * 
 *  
 * @author aricci
 *
 */
public class LightSensorThingService extends AbstractVerticle {

	private LightSensorThingAPI model;
	private List<ThingAbstractAdapter<LightSensorThingAPI>> adapters;
	
	public static final int HTTP_PORT = 8890;
	
	public LightSensorThingService(LightSensorThingAPI model) {
		this.model = model;
		adapters = new LinkedList<ThingAbstractAdapter<LightSensorThingAPI>>();
	}
	
	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		installAdapters(startPromise);
	}	
	
	/**
	 * Installing all available adapters.
	 * 
	 * Typically driven by using some config file.
	 *  	 
	 */
	protected void installAdapters(Promise<Void> promise) {		
	
		ArrayList<Future> allFutures = new ArrayList<Future>();		
		try {
			/*
			 * Installing only the HTTP adapter.
			 */
			LightSensorThingHTTPAdapter httpAdapter = new LightSensorThingHTTPAdapter(model, "localhost", HTTP_PORT, this.getVertx());
			Promise<Void> p = Promise.promise();
			httpAdapter.setupAdapter(p);
			Future<Void> fut = p.future();
			allFutures.add(fut);
			fut.onSuccess(res -> {
				log("HTTP adapter installed.");
				adapters.add(httpAdapter);
			}).onFailure(f -> {
				log("HTTP adapter not installed.");
			});
		} catch (Exception ex) {
			log("HTTP adapter installation failed.");
		}
						
		CompositeFuture.all(allFutures).onComplete(res -> {
			log("Adapters installed.");
			promise.complete();
		});
	}

	protected void log(String msg) {
		System.out.println("[LightSensorThingService] " + msg);
	}
}
