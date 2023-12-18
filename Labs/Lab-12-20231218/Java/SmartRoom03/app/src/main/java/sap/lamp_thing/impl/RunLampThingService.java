package sap.lamp_thing.impl;

import io.vertx.core.Vertx;

/**
 * Launching the Lamp Thing service.
 * 
 * @author aricci
 *
 */
public class RunLampThingService {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		LampThingModel model = new LampThingModel("MyLamp");
		model.setup(vertx);
		
		LampThingService service = new LampThingService(model);
		vertx.deployVerticle(service);
	}

}
