package sap.lamp_dt.impl;

import io.vertx.core.Vertx;

/**
 * Launching the Lamp Digital Twin service.
 * 
 * @author aricci
 *
 */
public class RunLampDTService {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		LampDTModel model = new LampDTModel("MyLampDT");
		model.setup(vertx);
		
		LampDTService service = new LampDTService(model);
		vertx.deployVerticle(service);
	}

}
