package mqtt_tests;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class TestMQTTVertx extends AbstractVerticle {
	
	public static void main(String[] args) throws Exception {		
		Vertx vertx = Vertx.vertx();
		MQTTVertxAgent agent = new MQTTVertxAgent();
		vertx.deployVerticle(agent);
	}
		
}
