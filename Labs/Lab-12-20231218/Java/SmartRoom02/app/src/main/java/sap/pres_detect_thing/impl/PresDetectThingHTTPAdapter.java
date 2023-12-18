package sap.pres_detect_thing.impl;

import java.util.Iterator;
import java.util.LinkedList;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import sap.common.ThingAbstractAdapter;

public class PresDetectThingHTTPAdapter extends ThingAbstractAdapter<PresDetectThingAPI> {

	private HttpServer server;
	private Router router;

	private String thingHost;
	private int thingPort;

	private static final String THING_BASE_PATH = "/api";
	private static final String TD_FULL_PATH = THING_BASE_PATH;
	private static final String PROPERTIES_BASE_PATH = THING_BASE_PATH + "/properties";
	private static final String PROPERTY_PRESENCE_DETECTED = "presenceDetected";
	private static final String PROPERTY_PRESENCE_DETECTED_FULL_PATH = PROPERTIES_BASE_PATH + "/" + PROPERTY_PRESENCE_DETECTED;
	private static final String EVENTS_FULL_PATH = THING_BASE_PATH + "/events";

	// event support
	private LinkedList<ServerWebSocket> subscribers;

	public PresDetectThingHTTPAdapter(PresDetectThingAPI model, String host, int port, Vertx vertx) {
		super(model, vertx);
		this.thingHost = host;
		this.thingPort = port;
	}

	protected void setupAdapter(Promise<Void> startPromise) {
		Future<JsonObject> tdfut = this.getModel().getTD();
		tdfut.onComplete(tdres -> {
			JsonObject td = tdres.result();

			router = Router.router(this.getVertx());
			try {
				router.get(TD_FULL_PATH).handler(this::handleGetTD);
				router.get(PROPERTY_PRESENCE_DETECTED_FULL_PATH).handler(this::handleGetDetected);

				populateTD(td);

			} catch (Exception ex) {
				ex.printStackTrace();
				log("API setup failed - " + ex.toString());
				startPromise.fail("API setup failed - " + ex.toString());
				return;
			}

			subscribers = new LinkedList<ServerWebSocket>();

			this.getModel().subscribe(ev -> {
				Iterator<ServerWebSocket> it = this.subscribers.iterator();
				while (it.hasNext()) {
					ServerWebSocket ws = it.next();
					if (!ws.isClosed()) {
						try {
							ws.write(ev.toBuffer());
						} catch (Exception ex) {
							it.remove();
						}
					} else {
						it.remove();
					}
				}
			});

			server = this.getVertx().createHttpServer();
			server.webSocketHandler(ws -> {
				if (!ws.path().equals(EVENTS_FULL_PATH)) {
					ws.reject();
				} else {
					log("New subscriber from " + ws.remoteAddress());
					subscribers.add(ws);
				}
			}).requestHandler(router).listen(thingPort, http -> {
				if (http.succeeded()) {
					startPromise.complete();
					log("HTTP Thing Adapter started on port " + thingPort);
				} else {
					log("HTTP Thing Adapter failure " + http.cause());
					startPromise.fail(http.cause());
				}
			});
		});
	}

	/**
	 * Configure the TD with the specific bindings provided by the adapter
	 * 
	 * @param td
	 */
	protected void populateTD(JsonObject td) {
		JsonArray detectedForm = 
				td
				.getJsonObject("properties")
				.getJsonObject("presenceDetected")
				.getJsonArray("forms");

		JsonObject httpDetectedForm = new JsonObject();
		httpDetectedForm.put("href", "http://" + thingHost + ":" + thingPort + PROPERTY_PRESENCE_DETECTED_FULL_PATH);
		detectedForm.add(httpDetectedForm);

		JsonArray detectedForms = 
				td
				.getJsonObject("events")
				.getJsonObject("presenceDetected")
				.getJsonArray("forms");

		JsonObject httpDetForm = new JsonObject();
		httpDetForm.put("href", "http://" + thingHost + ":" + thingPort + EVENTS_FULL_PATH);
		detectedForms.add(httpDetForm);

		JsonArray noMoreDetectedForms = 
				td
				.getJsonObject("events")
				.getJsonObject("presenceNoMoreDetected")
				.getJsonArray("forms");

		JsonObject httpNoMoreDetForm = new JsonObject();
		httpNoMoreDetForm.put("href", "http://" + thingHost + ":" + thingPort + EVENTS_FULL_PATH);
		noMoreDetectedForms.add(httpNoMoreDetForm);
	
	}

	protected void handleGetDetected(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		JsonObject reply = new JsonObject();
		Future<Boolean> fut = this.getModel().isDetected();
		fut.onSuccess(status -> {
			reply.put(PROPERTY_PRESENCE_DETECTED, status);
			res.end(reply.toBuffer());
		});
	}

	protected void handleGetTD(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		Future<JsonObject> fut = this.getModel().getTD();
		fut.onSuccess(td -> {
			res.end(td.toBuffer());
		});
	}

	protected void log(String msg) {
		System.out.println("[PresDetectHTTPAdapter][" + System.currentTimeMillis() + "] " + msg);
	}

}
