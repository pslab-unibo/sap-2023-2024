package sap.light_sensor_thing.impl;

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

public class LightSensorThingHTTPAdapter extends ThingAbstractAdapter<LightSensorThingAPI> {

	private HttpServer server;
	private Router router;

	private String thingHost;
	private int thingPort;

	private static final String THING_BASE_PATH = "/api";
	private static final String TD_FULL_PATH = THING_BASE_PATH;
	private static final String PROPERTIES_BASE_PATH = THING_BASE_PATH + "/properties";
	private static final String PROPERTY_LIGHT_LEVEL = "lightLevel";
	private static final String PROPERTY_LIGHT_LEVEL_FULL_PATH = PROPERTIES_BASE_PATH + "/" + PROPERTY_LIGHT_LEVEL;
	private static final String EVENTS_FULL_PATH = THING_BASE_PATH + "/events";
	private static final String EVENT_LIGHT_LEVEL_CHANGED = "lightLevelChanged";

	// event support
	private LinkedList<ServerWebSocket> subscribers;

	public LightSensorThingHTTPAdapter(LightSensorThingAPI model, String host, int port, Vertx vertx) {
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
				router.get(PROPERTY_LIGHT_LEVEL_FULL_PATH).handler(this::handleGetLightLevel);

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
		JsonArray stateForms = 
				td
				.getJsonObject("properties")
				.getJsonObject(PROPERTY_LIGHT_LEVEL)
				.getJsonArray("forms");

		JsonObject httpStateForm = new JsonObject();
		httpStateForm.put("href", "http://" + thingHost + ":" + thingPort + PROPERTY_LIGHT_LEVEL_FULL_PATH);
		stateForms.add(httpStateForm);

		JsonArray lcForms = 
				td
				.getJsonObject("events")
				.getJsonObject(EVENT_LIGHT_LEVEL_CHANGED)
				.getJsonArray("forms");

		JsonObject httpForm = new JsonObject();
		httpForm.put("href", "http://" + thingHost + ":" + thingPort + EVENTS_FULL_PATH);
		lcForms.add(httpForm);

	
	}

	protected void handleGetLightLevel(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		JsonObject reply = new JsonObject();
		Future<Double> fut = this.getModel().getLightLevel();
		fut.onSuccess(level -> {
			reply.put(PROPERTY_LIGHT_LEVEL, level);
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
		System.out.println("[LightLevelHTTPAdapter][" + System.currentTimeMillis() + "] " + msg);
	}

}
