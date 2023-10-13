package sap.escooters.presentation_layer;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import sap.escooters.service_layer.*;

public class EScooterManServer extends AbstractVerticle {

	private int port;
	private ServiceLayer serviceLayer;
    static Logger logger = Logger.getLogger("[EScooter Server]");	

	public EScooterManServer(int port, ServiceLayer serviceLayer) {
		this.port = port;
		this.serviceLayer = serviceLayer;
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
			serviceLayer.registerNewUser(id, name, surname);
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
			JsonObject info = serviceLayer.getUserInfo(userId);
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
			serviceLayer.registerNewEScooter(id);
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
			JsonObject info = serviceLayer.getEScooterInfo(escooterId);
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
			String rideId = serviceLayer.startNewRide(userId, escooterId);
			reply.put("result", "ok");
			reply.put("rideId", rideId);
		} catch (Exception  ex) {
			reply.put("result", "start-new-ride-failed");
		}
		sendReply(context, reply);
	}
	
	protected void getRideInfo(RoutingContext context) {
		logger.log(Level.FINE, "New ride info request: " + context.currentRoute().getPath());
	    String rideId = context.pathParam("rideId");
		JsonObject reply = new JsonObject();
		try {
			JsonObject info = serviceLayer.getRideInfo(rideId);
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
			serviceLayer.endRide(rideId);
			reply.put("result", "ok");
		} catch (RideNotFoundException ex) {
			reply.put("result", "ride-not-found");
		} catch (RideAlreadyEndedException ex) {
			reply.put("result", "ride-already-ended");
		}
		sendReply(context, reply);
	}
	
	private void sendReply(RoutingContext request, JsonObject reply) {
		HttpServerResponse response = request.response();
		response.putHeader("content-type", "application/json");
		response.end(reply.toString());
	}
	
}
