package sap.common.agent;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * General Proxy for Agents to interact with a WoT Thing 
 * 
 * - fixed protocol - HTTP protocol
 * 
 * @author aricci
 *
 */
public class Thing {

	protected Vertx vertx;
	protected WebClient client;

	protected int thingPort;
	protected String thingHost;

	private static final String THING_BASE_PATH = "/api";
	private static final String TD_FULL_PATH = THING_BASE_PATH;
	private static final String PROPERTIES_BASE_PATH = THING_BASE_PATH + "/properties";
	private static final String ACTIONS_BASE_PATH = THING_BASE_PATH + "/actions";
	private static final String EVENTS_FULL_PATH = THING_BASE_PATH + "/events";
				
	public Thing(String thingHost, int thingPort){
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
	
	public Future<JsonObject> readProperty(String name) {
		Promise<JsonObject> promise = Promise.promise();
		client
			.get(this.thingPort, thingHost, PROPERTIES_BASE_PATH + "/" + name)
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
	
	public Future<Void> invokeAction(String name) {
		Promise<Void> promise = Promise.promise();
		client
			.post(this.thingPort, thingHost, ACTIONS_BASE_PATH + "/" + name)
			.send()
			.onSuccess(response -> {
				promise.complete(null);
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
				WebSocket ws = res.result();
				ws.handler(buf -> {
					handler.handle(buf.toJsonObject());
				});
				promise.complete();
			}
		});
		return promise.future();			
	}
	

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
}
