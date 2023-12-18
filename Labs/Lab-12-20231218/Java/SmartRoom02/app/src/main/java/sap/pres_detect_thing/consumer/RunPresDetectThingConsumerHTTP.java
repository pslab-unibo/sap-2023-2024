package sap.pres_detect_thing.consumer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class RunPresDetectThingConsumerHTTP {

	static final int PRES_DETECT_THING_PORT = 8889;

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
	
		PresDetectThingHTTPProxy thing = new PresDetectThingHTTPProxy("localhost", PRES_DETECT_THING_PORT);
		Future<Void> fut = thing.setup(vertx);
		fut.onSuccess(h -> {
			vertx.deployVerticle(new VanillaPresDetectThingConsumerAgent(thing));
		});
	}

}
