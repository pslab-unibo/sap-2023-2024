package sap.lamp_thing.impl;

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

public class LampThingHTTPAdapter extends ThingAbstractAdapter<LampThingAPI> {

	private HttpServer server;
	private Router router;

	private String thingHost;
	private int thingPort;

	private static final String THING_BASE_PATH = "/api";
	private static final String TD_FULL_PATH = THING_BASE_PATH;
	private static final String PROPERTIES_BASE_PATH = THING_BASE_PATH + "/properties";
	private static final String PROPERTY_STATE = "state";
	private static final String PROPERTY_STATE_FULL_PATH = PROPERTIES_BASE_PATH + "/" + PROPERTY_STATE;
	private static final String ACTIONS_BASE_PATH = THING_BASE_PATH + "/actions";
	private static final String ACTION_ON = "on";
	private static final String ACTION_ON_FULL_PATH = ACTIONS_BASE_PATH + "/" + ACTION_ON;
	private static final String ACTION_OFF = "off";
	private static final String ACTION_OFF_FULL_PATH = ACTIONS_BASE_PATH + "/" + ACTION_OFF;
	private static final String EVENTS_FULL_PATH = THING_BASE_PATH + "/events";
	private static final String EVENT_STATE_CHANGED = "stateChanged";
	
	private static final int DONE = 201;


	// event support
	private LinkedList<ServerWebSocket> subscribers;

	public LampThingHTTPAdapter(LampThingAPI model, String host, int port, Vertx vertx) {
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
				router.get(PROPERTY_STATE_FULL_PATH).handler(this::handleGetPropertyState);
				router.post(ACTION_ON_FULL_PATH).handler(this::handleActionOn);
				router.post(ACTION_OFF_FULL_PATH).handler(this::handleActionOff);

				populateTD(td);

			} catch (Exception ex) {
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
				.getJsonObject(PROPERTY_STATE)
				.getJsonArray("forms");

		JsonObject httpStateForm = new JsonObject();
		httpStateForm.put("href", "http://" + thingHost + ":" + thingPort + PROPERTY_STATE_FULL_PATH);
		stateForms.add(httpStateForm);

		JsonArray onForms = 
				td
				.getJsonObject("actions")
				.getJsonObject(ACTION_ON)
				.getJsonArray("forms");

		JsonObject httpOnForm = new JsonObject();
		httpOnForm.put("href", "http://" + thingHost + ":" + thingPort + ACTION_ON_FULL_PATH);
		onForms.add(httpOnForm);

		JsonArray offForms = 
				td
				.getJsonObject("actions")
				.getJsonObject(ACTION_OFF)
				.getJsonArray("forms");

		JsonObject httpOffForm = new JsonObject();
		httpOffForm.put("href", "http://" + thingHost + ":" + thingPort + ACTION_OFF_FULL_PATH);
		offForms.add(httpOffForm);

		JsonArray stateChangedForms = 
				td
				.getJsonObject("events")
				.getJsonObject(EVENT_STATE_CHANGED)
				.getJsonArray("forms");

		JsonObject httpStateChangedForm = new JsonObject();
		httpStateChangedForm.put("href", "http://" + thingHost + ":" + thingPort + EVENTS_FULL_PATH);
		stateChangedForms.add(httpStateChangedForm);
	}

	protected void handleGetPropertyState(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		JsonObject reply = new JsonObject();
		Future<String> fut = this.getModel().getState();
		fut.onSuccess(status -> {
			reply.put(PROPERTY_STATE, status);
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

	protected void handleActionOn(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		log("ON request.");
		Future<Void> fut = this.getModel().on();
		fut.onSuccess(ret -> {
			res.setStatusCode(DONE).end();
		});
	}

	protected void handleActionOff(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		log("OFF request.");
		Future<Void> fut = this.getModel().off();
		fut.onSuccess(ret -> {
			res.setStatusCode(DONE).end();
		});
	}

	protected void log(String msg) {
		System.out.println("[LampThingHTTPAdapter][" + System.currentTimeMillis() + "] " + msg);
	}

}
