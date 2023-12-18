package sap.lamp_dt.consumer;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

/**
 * Lamp DT proxy 
 * 
 * @author aricci
 *
 */
public class LampDTProxy implements LampDTAPI {

	private Vertx vertx;
	private WebClient client;

	private int dtPort;
	private String dtHost;

	private static final String DT_BASE_PATH = "/api";
	private static final String DTD_FULL_PATH = DT_BASE_PATH;
	private static final String PROPERTIES_BASE_PATH = DT_BASE_PATH + "/properties";
	private static final String PROPERTY_STATE = "state";
	private static final String PROPERTY_HISTORY = "history";
	private static final String PROPERTY_STATE_FULL_PATH = PROPERTIES_BASE_PATH + "/" + PROPERTY_STATE;
	private static final String PROPERTY_HISTORY_FULL_PATH = PROPERTIES_BASE_PATH + "/" + PROPERTY_HISTORY;
	// private static final String ACTIONS_BASE_PATH = DT_BASE_PATH + "/actions";
	// private static final String ACTION_ON = "on";
	// private static final String ACTION_ON_FULL_PATH = ACTIONS_BASE_PATH + "/" + ACTION_ON;
	// private static final String ACTION_OFF = "off";
	// private static final String ACTION_OFF_FULL_PATH = ACTIONS_BASE_PATH + "/" + ACTION_OFF;
	private static final String EVENTS_FULL_PATH = DT_BASE_PATH + "/events";
			
	public LampDTProxy(String dtHost, int dtPort){
		this.dtPort = dtPort;
		this.dtHost = dtHost;
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
	
	public Future<JsonObject> getState() {
		Promise<JsonObject> promise = Promise.promise();
		client
			.get(this.dtPort, dtHost, PROPERTY_STATE_FULL_PATH)
			.send()
			.onSuccess(response -> {
				promise.complete(response.bodyAsJsonObject());
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}

	public Future<JsonArray> getHistory() {
		Promise<JsonArray> promise = Promise.promise();
		client
			.get(this.dtPort, dtHost, PROPERTY_HISTORY_FULL_PATH)
			.send()
			.onSuccess(response -> {
				promise.complete(response.bodyAsJsonArray());
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}
	
	/*
	public Future<Void> on() {
		Promise<Void> promise = Promise.promise();
		client
			.post(this.dtPort, dtHost, ACTION_ON_FULL_PATH)
			.send()
			.onSuccess(response -> {
				promise.complete(null);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}
	
	public Future<Void> off() {
		Promise<Void> promise = Promise.promise();
		client
			.post(this.dtPort, dtHost, ACTION_OFF_FULL_PATH)
			.send()
			.onSuccess(response -> {
				promise.complete(null);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}
	*/

	public Future<Void> subscribe(Handler<JsonObject> handler) {
		Promise<Void> promise = Promise.promise();
		HttpClient cli = vertx.createHttpClient();
		cli.webSocket(this.dtPort, dtHost, EVENTS_FULL_PATH, res -> {
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
	
	protected void log(String msg) {
		System.out.println("[LampDTProxy]["+System.currentTimeMillis()+"] " + msg);
	}

}
