package sap.escooters.infrastructure_layer.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;
import sap.escooters.application_layer.*;

public class EScooterManServer extends AbstractVerticle implements RideDashboardPort {

	private int port;
	private ApplicationAPI appAPI;
    static Logger logger = Logger.getLogger("[EScooter Server]");	

	public EScooterManServer(int port, ApplicationAPI appAPI) {
		this.port = port;
		this.appAPI = appAPI;
		logger.setLevel(Level.INFO);
	}
	
	
	public void start() {
		logger.log(Level.INFO, "EScooterMan server initializing...");
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		/* static files by default searched in "webroot" directory */
		router.route("/static/*").handler(StaticHandler.create().setCachingEnabled(false));
		router.route().handler(BodyHandler.create());
		
		router.route(HttpMethod.POST, "/api/users").handler(this::registerNewUser);
		router.route(HttpMethod.GET, "/api/users/:userId").handler(this::getUserInfo);
		router.route(HttpMethod.POST, "/api/escooters").handler(this::registerNewEScooter);
		router.route(HttpMethod.GET, "/api/escooters/:escooterId").handler(this::getEScooterInfo);
		router.route(HttpMethod.POST, "/api/rides").handler(this::startNewRide);
		router.route(HttpMethod.GET, "/api/rides/:rideId").handler(this::getRideInfo);
		router.route(HttpMethod.POST, "/api/rides/:rideId/end").handler(this::endRide);
		
		server.webSocketHandler(webSocket -> {
			  logger.log(Level.INFO, "Ride monitoring request: " + webSocket.path());
			  
			  if (webSocket.path().equals("/api/rides/monitoring")) {
				webSocket.accept();
				logger.log(Level.INFO, "New ride monitoring observer registered.");
		    	EventBus eb = vertx.eventBus();
		    	eb.consumer("ride-events", msg -> {
		    		JsonObject ev = (JsonObject) msg.body();
			    	logger.log(Level.INFO, "Changes in rides: " + ev.encodePrettily());
		    		webSocket.writeTextMessage(ev.encodePrettily());
		    	});
		    	/*
				webSocket.handler(buffer -> {
					
				});*/
			  } else {
				  logger.log(Level.INFO, "Ride monitoring observer rejected.");
				  webSocket.reject();
			  }
			});		
		
		
		
		server
		.requestHandler(router)
		.listen(port);

		logger.log(Level.INFO, "EScooterMan server ready - port: " + port);
	}
	
	protected void registerNewUser(RoutingContext context) {
		logger.log(Level.INFO, "New registration user request - " + context.currentRoute().getPath());
		JsonObject userInfo = context.body().asJsonObject();
		logger.log(Level.INFO, "Body: " + userInfo.encodePrettily());
		
		String id = userInfo.getString("id");
		String name = userInfo.getString("name");
		String surname = userInfo.getString("surname");
		
		JsonObject reply = new JsonObject();
		try {
			appAPI.registerNewUser(id, name, surname);
			reply.put("result", "ok");
		} catch (UserIdAlreadyExistingException ex) {
			reply.put("result", "user-id-already-existing");
		}
		sendReply(context, reply); 	
	}
	
	protected void getUserInfo(RoutingContext context) {
		logger.log(Level.INFO, "New user info request: " + context.currentRoute().getPath());
	    String userId = context.pathParam("userId");
		JsonObject reply = new JsonObject();
		try {
			JsonObject info = appAPI.getUserInfo(userId);
			reply.put("result", "ok");
			reply.put("user", info);
		} catch (UserNotFoundException ex) {
			reply.put("result", "user-not-found");
		}
		sendReply(context, reply);
	}

	protected void registerNewEScooter(RoutingContext context) {
		logger.log(Level.INFO, "new EScooter registration request: " + context.currentRoute().getPath());
		JsonObject escooterInfo = context.body().asJsonObject();
		logger.log(Level.INFO, "Body: " + escooterInfo.encodePrettily());
		
		String id = escooterInfo.getString("id");
		
		JsonObject reply = new JsonObject();
		try {
			appAPI.registerNewEScooter(id);
			reply.put("result", "ok");
		} catch (UserIdAlreadyExistingException ex) {
			reply.put("result", "escooter-id-already-existing");
		}
		sendReply(context, reply);
	}

	protected void getEScooterInfo(RoutingContext context) {
		logger.log(Level.INFO, "New escooter info request: " + context.currentRoute().getPath());
	    String escooterId = context.pathParam("escooterId");
		JsonObject reply = new JsonObject();
		try {
			JsonObject info = appAPI.getEScooterInfo(escooterId);
			reply.put("result", "ok");
			reply.put("escooter", info);
		} catch (EScooterNotFoundException ex) {
			reply.put("result", "escooter-not-found");
		}
		sendReply(context, reply);
	}
	
	protected void startNewRide(RoutingContext context) {
		logger.log(Level.INFO, "Start new ride request: " + context.currentRoute().getPath());
		JsonObject rideInfo = context.body().asJsonObject();
		logger.log(Level.INFO, "Body: " + rideInfo.encodePrettily());
		
		String userId = rideInfo.getString("userId");
		String escooterId = rideInfo.getString("escooterId");
		
		JsonObject reply = new JsonObject();
		try {
			String rideId = appAPI.startNewRide(userId, escooterId);
			reply.put("result", "ok");
			reply.put("rideId", rideId);
		} catch (Exception  ex) {
			reply.put("result", "start-new-ride-failed");
		}
		sendReply(context, reply);
	}
	
	protected void getRideInfo(RoutingContext context) {
		String rideId = context.pathParam("rideId");
		JsonObject reply = new JsonObject();
		try {
			JsonObject info = appAPI.getRideInfo(rideId);
			reply.put("result", "ok");
			reply.put("ride", info);
		} catch (RideNotFoundException ex) {
			reply.put("result", "ride-not-found");
		}
		sendReply(context, reply);
	}

	protected void endRide(RoutingContext context) {
		logger.log(Level.INFO, "End ride request: " + context.currentRoute().getPath());
	    String rideId = context.pathParam("rideId");
		JsonObject reply = new JsonObject();
		try {
			appAPI.endRide(rideId);
			reply.put("result", "ok");
		} catch (RideNotFoundException ex) {
			reply.put("result", "ride-not-found");
		} catch (RideAlreadyEndedException ex) {
			reply.put("result", "ride-already-ended");
		}
		sendReply(context, reply);
	}
	
	@Override
	public void notifyNumOngoingRidesChanged(int nOngoingRides) {
		logger.log(Level.INFO, "notify num rides changed");
		EventBus eb = vertx.eventBus();
		
		JsonObject obj = new JsonObject();
		obj.put("event", "num-ongoing-rides-changed");
		obj.put("nOngoingRides", nOngoingRides);
		
    	eb.publish("ride-events", obj);
	}
	
		
	private void sendReply(RoutingContext request, JsonObject reply) {
		HttpServerResponse response = request.response();
		response.putHeader("content-type", "application/json");
		response.end(reply.toString());
	}
	
}
