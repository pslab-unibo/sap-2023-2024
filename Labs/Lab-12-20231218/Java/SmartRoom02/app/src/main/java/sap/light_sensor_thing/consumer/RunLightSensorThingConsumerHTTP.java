package sap.light_sensor_thing.consumer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class RunLightSensorThingConsumerHTTP {

	static final int LIGHT_SENSOR_THING_PORT = 8890;

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
	
		LightSensorThingHTTPProxy thing = new LightSensorThingHTTPProxy("localhost", LIGHT_SENSOR_THING_PORT);
		Future<Void> fut = thing.setup(vertx);
		fut.onSuccess(h -> {
			vertx.deployVerticle(new VanillaLightSensorThingConsumerAgent(thing));
		});
	}

}
