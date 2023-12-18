package sap.lamp_thing.consumer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class RunLampThingConsumerHTTP {

	static final int LAMP_THING_PORT = 8888;

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
	
		LampThingHTTPProxy thing = new LampThingHTTPProxy("localhost", LAMP_THING_PORT);
		Future<Void> fut = thing.setup(vertx);
		fut.onSuccess(h -> {
			vertx.deployVerticle(new VanillaLampThingConsumerAgent(thing));
		});
	}

}
