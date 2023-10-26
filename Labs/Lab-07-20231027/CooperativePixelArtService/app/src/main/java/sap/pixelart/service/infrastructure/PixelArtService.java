package sap.pixelart.service.infrastructure;

import java.util.logging.Level;
import java.util.logging.Logger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import sap.pixelart.service.application.*;
import sap.pixelart.service.domain.*;

/**
 * 
 * Verticle impementing the behaviour of a REST Adapter for the 
 * PixelArt microservice
 * 
 * @author aricci
 *
 */
public class PixelArtService extends AbstractVerticle implements PixelGridEventObserver {

	private int port;
	private PixelArtAPI pixelArtAPI;
	static Logger logger = Logger.getLogger("[PixelArt Service]");
	static String PIXEL_GRID_CHANNEL = "pixel-grid-events";

	public PixelArtService(int port, PixelArtAPI appAPI) {
		this.port = port;
		this.pixelArtAPI = appAPI;
		logger.setLevel(Level.INFO);
	}

	public void start() {
		logger.log(Level.INFO, "PixelArt service initializing...");
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		/* static files by default searched in "webroot" directory */
		router.route("/static/*").handler(StaticHandler.create().setCachingEnabled(false));
		router.route().handler(BodyHandler.create());

		router.route(HttpMethod.POST, "/api/brushes").handler(this::createBrush);
		router.route(HttpMethod.GET, "/api/brushes").handler(this::getCurrentBrushes);
		router.route(HttpMethod.GET, "/api/brushes/:brushId").handler(this::getBrushInfo);
		router.route(HttpMethod.DELETE, "/api/brushes/:brushId").handler(this::destroyBrush);
		router.route(HttpMethod.POST, "/api/brushes/:brushId/move-to").handler(this::moveBrushTo);
		router.route(HttpMethod.POST, "/api/brushes/:brushId/change-color").handler(this::changeBrushColor);
		router.route(HttpMethod.POST, "/api/brushes/:brushId/select-pixel").handler(this::selectPixel);
		router.route(HttpMethod.GET, "/api/pixel-grid").handler(this::getPixelGridState);
		this.handleEventSubscription(server, "/api/pixel-grid/events");

		server.requestHandler(router).listen(port);

		logger.log(Level.INFO, "PixelArt Service ready - port: " + port);
	}

	/* List of handlers, mapping the API */
	
	protected void createBrush(RoutingContext context) {
		logger.log(Level.INFO, "CreateBrush request - " + context.currentRoute().getPath());

		JsonObject reply = new JsonObject();
		try {
			String brushId = pixelArtAPI.createBrush();
			reply.put("result", "ok");
			reply.put("brushId", brushId);
		} catch (Exception ex) {
			reply.put("result", "error");
		}
		sendReply(context, reply);
	}

	protected void getCurrentBrushes(RoutingContext context) {
		logger.log(Level.INFO, "GetCurrentBrushes request - " + context.currentRoute().getPath());

		JsonObject reply = new JsonObject();
		try {
			JsonArray brushes = pixelArtAPI.getCurrentBrushes();
			reply.put("result", "ok");
			reply.put("brushes", brushes);
		} catch (Exception ex) {
			reply.put("result", "error");
		}
		sendReply(context, reply);
	}

	protected void getBrushInfo(RoutingContext context) {
		logger.log(Level.INFO, "Get Brush info request: " + context.currentRoute().getPath());
		String brushId = context.pathParam("brushId");
		JsonObject reply = new JsonObject();
		try {
			JsonObject info = pixelArtAPI.getBrushInfo(brushId);
			reply.put("result", "ok");
			reply.put("brushInfo", info);
		} catch (Exception ex) {
			reply.put("result", "error");
		}
		sendReply(context, reply);
	}

	protected void moveBrushTo(RoutingContext context) {
		logger.log(Level.INFO, "MoveBrushTo request: " + context.currentRoute().getPath());
		String brushId = context.pathParam("brushId");
		JsonObject brushInfo = context.body().asJsonObject();
		logger.log(Level.INFO, "Body: " + brushInfo.encodePrettily());
		int x = brushInfo.getInteger("x");
		int y = brushInfo.getInteger("y");
		JsonObject reply = new JsonObject();
		try {
			pixelArtAPI.moveBrushTo(brushId, y, x);
			reply.put("result", "ok");
		} catch (Exception ex) {
			reply.put("result", "error");
		}
		sendReply(context, reply);
	}

	protected void changeBrushColor(RoutingContext context) {
		logger.log(Level.INFO, "ChangeBrushColor request: " + context.currentRoute().getPath());
		String brushId = context.pathParam("brushId");
		JsonObject brushInfo = context.body().asJsonObject();
		logger.log(Level.INFO, "Body: " + brushInfo.encodePrettily());
		int c = brushInfo.getInteger("color");
		JsonObject reply = new JsonObject();
		try {
			pixelArtAPI.changeBrushColor(brushId, c);
			reply.put("result", "ok");
		} catch (Exception ex) {
			reply.put("result", "error");
		}
		sendReply(context, reply);
	}

	protected void selectPixel(RoutingContext context) {
		logger.log(Level.INFO, "SelectPixel request: " + context.currentRoute().getPath());
		String brushId = context.pathParam("brushId");
		JsonObject reply = new JsonObject();
		try {
			pixelArtAPI.selectPixel(brushId);
			reply.put("result", "ok");
		} catch (Exception ex) {
			reply.put("result", "error");
		}
		sendReply(context, reply);
	}

	protected void destroyBrush(RoutingContext context) {
		logger.log(Level.INFO, "Destroy Brush request: " + context.currentRoute().getPath());
		String brushId = context.pathParam("brushId");
		JsonObject reply = new JsonObject();
		try {
			pixelArtAPI.destroyBrush(brushId);
			reply.put("result", "ok");
		} catch (Exception ex) {
			reply.put("result", "error");
		}
		sendReply(context, reply);
	}

	protected void getPixelGridState(RoutingContext context) {
		logger.log(Level.INFO, "Get Pixel Grid state request: " + context.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		try {
			JsonObject info = pixelArtAPI.getPixelGridState();
			reply.put("result", "ok");
			reply.put("pixelGrid", info);
		} catch (Exception ex) {
			reply.put("result", "error");
		}
		sendReply(context, reply);
	}

	@Override
	public void pixelColorChanged(int x, int y, int color) {
		logger.log(Level.INFO, "New PixelGrid event - pixel selected");
		EventBus eb = vertx.eventBus();
		JsonObject obj = new JsonObject();
		obj.put("event", "pixel-selected");
		obj.put("x", x);
		obj.put("y", y);
		obj.put("color", color);
		eb.publish(PIXEL_GRID_CHANNEL, obj);
	}

	protected void handleEventSubscription(HttpServer server, String path) {
		server.webSocketHandler(webSocket -> {
			if (webSocket.path().equals(path)) {
				webSocket.accept();
				logger.log(Level.INFO, "New PixelGrid subscription accepted.");
				JsonObject reply = new JsonObject();
				JsonObject grid = pixelArtAPI.getPixelGridState();
				reply.put("event", "subscription-started");
				reply.put("pixelGridCurrentState", grid);
				webSocket.writeTextMessage(reply.encodePrettily());
				EventBus eb = vertx.eventBus();
				eb.consumer(PIXEL_GRID_CHANNEL, msg -> {
					JsonObject ev = (JsonObject) msg.body();
					logger.log(Level.INFO, "Event: " + ev.encodePrettily());
					webSocket.writeTextMessage(ev.encodePrettily());
				});
			} else {
				logger.log(Level.INFO, "PixelGrid subscription refused.");
				webSocket.reject();
			}
		});
	}

	private void sendReply(RoutingContext request, JsonObject reply) {
		HttpServerResponse response = request.response();
		response.putHeader("content-type", "application/json");
		response.end(reply.toString());
	}
}
