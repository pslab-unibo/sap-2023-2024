package sap.smartroom_agent;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * Proxy to interact with a PresDetectThing using HTTP protocol
 * 
 * @author aricci
 *
 */
public class PresDetectThingProxy implements PresDetectThingAPI {

	private Vertx vertx;
	private WebClient client;

	private int thingPort;
	private String thingHost;

	private static final String THING_BASE_PATH = "/api";
	private static final String TD_FULL_PATH = THING_BASE_PATH;
	private static final String PROPERTIES_BASE_PATH = THING_BASE_PATH + "/properties";
	private static final String PROPERTY_PRESENCE_DETECTED = "presenceDetected";
	private static final String PROPERTY_PRESENCE_DETECTED_FULL_PATH = PROPERTIES_BASE_PATH + "/" + PROPERTY_PRESENCE_DETECTED;
	private static final String EVENTS_FULL_PATH = THING_BASE_PATH + "/events";
			
	public PresDetectThingProxy(String thingHost, int thingPort){
		this.thingPort = thingPort;
		this.thingHost = thingHost;
	}

	public Future<Void> setup(Vertx vertx) {
		this.vertx = vertx;
		Promise<Void> promise = Promise.promise();
		vertx.executeBlocking(p -> {
			client = WebClient.create(vertx);
			promise.complete();
		});
		return promise.future();
	}
	
	public Future<Boolean> isDetected() {
		Promise<Boolean> promise = Promise.promise();
		client
			.get(this.thingPort, thingHost, PROPERTY_PRESENCE_DETECTED_FULL_PATH)
			.send()
			.onSuccess(response -> {
				JsonObject reply = response.bodyAsJsonObject();
				Boolean status = reply.getBoolean(PROPERTY_PRESENCE_DETECTED);
				promise.complete(status);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}
	

	public Future<Void> subscribe(Handler<JsonObject> handler) {
		Promise<Void> promise = Promise.promise();
		HttpClient cli = vertx.createHttpClient();
		cli.webSocket(this.thingPort, thingHost, EVENTS_FULL_PATH, res -> {
			if (res.succeeded()) {
				log("Connected!");
				WebSocket ws = res.result();
				ws.handler(buf -> {
					handler.handle(buf.toJsonObject());
				});
				promise.complete();
			}
		});
		return promise.future();			
	}
	

	@Override
	public Future<JsonObject> getTD() {
		Promise<JsonObject> promise = Promise.promise();
		client
			.get(this.thingPort, thingHost, TD_FULL_PATH)
			.send()
			.onSuccess(response -> {
				JsonObject reply = response.bodyAsJsonObject();
				promise.complete(reply);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}
	
	protected void log(String msg) {
		System.out.println("[PresDetectThingHTTPProxy]["+System.currentTimeMillis()+"] " + msg);
	}

}
