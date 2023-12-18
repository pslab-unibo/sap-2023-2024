package sap.lamp_dt.consumer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class RunLampDTConsumer {

	static final int LAMP_DT_PORT = 9888;

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
	
		LampDTProxy thing = new LampDTProxy("localhost", LAMP_DT_PORT);
		Future<Void> fut = thing.setup(vertx);
		fut.onSuccess(h -> {
			vertx.deployVerticle(new VanillaLampDTConsumerAgent(thing));
		});
	}

}
