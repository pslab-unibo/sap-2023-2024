package sap.pres_detect_thing.impl;

import io.vertx.core.Vertx;

/**
 * Launching the Presence Detection Thing service.
 * 
 * @author aricci
 *
 */
public class RunPresDetectThingService {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		PresDetectThingModel model = new PresDetectThingModel("MyPresDetect");
		model.setup(vertx);
		
		PresDetectThingService service = new PresDetectThingService(model);
		vertx.deployVerticle(service);
	}

}
