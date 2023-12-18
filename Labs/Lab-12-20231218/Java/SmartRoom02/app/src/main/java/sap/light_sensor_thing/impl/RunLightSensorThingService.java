package sap.light_sensor_thing.impl;

import io.vertx.core.Vertx;

/**
 * Launching the Light Sensor Thing service.
 * 
 * @author aricci
 *
 */
public class RunLightSensorThingService {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		LightSensorThingModel model = new LightSensorThingModel("MyLightSensor");
		model.setup(vertx);
		
		LightSensorThingService service = new LightSensorThingService(model);
		vertx.deployVerticle(service);
	}

}
