package sap.lamp_thing.consumer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class RunLampThingConsumerMQTT {

	static final int LAMP_THING_PORT = 1883;

	public static void main(String[] args) throws Exception {
		Vertx vertx = Vertx.vertx();
		
		LampThingMQTTProxy thing = new LampThingMQTTProxy("my-consumer","MyLamp", "localhost", LAMP_THING_PORT);
		Future<Void> fut = thing.setup(vertx);
		
		fut.onSuccess(h -> {
			vertx.deployVerticle(new VanillaLampThingConsumerAgent(thing));
		});
	}

}
