package sap.lamp_dt.impl;

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

public class LampDTAdapter  {

	private HttpServer server;
	private Router router;

	private int dtPort;

	
	private Vertx vertx;	
	private LampDTAppAPI model;
	
	private static final String DT_BASE_PATH = "/api";
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


	// event support
	private LinkedList<ServerWebSocket> subscribers;

	public LampDTAdapter(LampDTAppAPI model, int port, Vertx vertx) {
		this.model = model;
		this.vertx = vertx;
		this.dtPort = port;
	}

	protected void setupAdapter(Promise<Void> startPromise) {
			router = Router.router(vertx);
			try {
				router.get(PROPERTY_STATE_FULL_PATH).handler(this::handleGetPropertyState);
				router.get(PROPERTY_HISTORY_FULL_PATH).handler(this::handleGetPropertyHistory);
				// router.post(ACTION_ON_FULL_PATH).handler(this::handleActionOn);
				// router.post(ACTION_OFF_FULL_PATH).handler(this::handleActionOff);

			} catch (Exception ex) {
				log("API setup failed - " + ex.toString());
				startPromise.fail("API setup failed - " + ex.toString());
				return;
			}

			subscribers = new LinkedList<ServerWebSocket>();

			model.subscribe(ev -> {
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

			server = vertx.createHttpServer();
			server.webSocketHandler(ws -> {
				if (!ws.path().equals(EVENTS_FULL_PATH)) {
					ws.reject();
				} else {
					log("New subscriber from " + ws.remoteAddress());
					subscribers.add(ws);
				}
			}).requestHandler(router).listen(dtPort, http -> {
				if (http.succeeded()) {
					startPromise.complete();
					log("HTTP DT Adapter started on port " + dtPort);
				} else {
					log("HTTP DT Adapter failure " + http.cause());
					startPromise.fail(http.cause());
				}
			});
	}

	protected void handleGetPropertyState(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		Future<JsonObject> fut = model.getState();
		fut.onSuccess(state -> {
			res.end(state.toBuffer());
		});
	}

	protected void handleGetPropertyHistory(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		Future<JsonArray> fut = model.getHistory();
		fut.onSuccess(arr -> {
			res.end(arr.toBuffer());
		});
	}

	/*
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
	 */
	
	protected void log(String msg) {
		System.out.println("[LampThingHTTPAdapter][" + System.currentTimeMillis() + "] " + msg);
	}

}
