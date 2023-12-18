package sap.pres_detect_thing.impl;

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
 * Presence Detection Thing Service 
 *  
 * @author aricci
 *
 */
public class PresDetectThingService extends AbstractVerticle {

	private PresDetectThingAPI model;
	private List<ThingAbstractAdapter> adapters;
	
	public static final int HTTP_PORT = 8889;
	
	public PresDetectThingService(PresDetectThingAPI model) {
		this.model = model;
		adapters = new LinkedList<ThingAbstractAdapter>();
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
			PresDetectThingHTTPAdapter httpAdapter = new PresDetectThingHTTPAdapter(model, "localhost", HTTP_PORT, this.getVertx());
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
		System.out.println("[PresDetectThingService] " + msg);
	}
}
