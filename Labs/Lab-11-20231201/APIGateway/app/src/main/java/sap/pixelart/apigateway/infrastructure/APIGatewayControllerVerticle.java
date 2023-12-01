package sap.pixelart.apigateway.infrastructure;

import java.util.logging.Level;
import java.util.logging.Logger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import sap.pixelart.apigateway.application.*;
import sap.pixelart.apigateway.domain.*;
import sap.pixelart.library.PixelArtAsyncAPI;

/**
 * 
 * Verticle impementing the behaviour of a REST Adapter for the 
 * PixelArt microservice
 * 
 * @author aricci
 *
 */
public class APIGatewayControllerVerticle extends AbstractVerticle implements PixelGridEventObserver {

	private int port;
	private PixelArtAsyncAPI serviceAPI;
	static Logger logger = Logger.getLogger("[PixelArt Service]");
	static String PIXEL_GRID_CHANNEL = "pixel-grid-events";

	public APIGatewayControllerVerticle(int port, PixelArtAsyncAPI serviceAPI) {
		this.port = port;
		this.serviceAPI = serviceAPI;
		logger.setLevel(Level.INFO);
	}

	public void start() {
		logger.log(Level.INFO, "PixelArt Service initializing...");
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);

		/* configure the HTTP routes following a REST style */
		
		router.route(HttpMethod.POST, "/api/brushes").handler(this::createBrush);
		router.route(HttpMethod.GET, "/api/brushes").handler(this::getCurrentBrushes);
		router.route(HttpMethod.GET, "/api/brushes/:brushId").handler(this::getBrushInfo);
		router.route(HttpMethod.DELETE, "/api/brushes/:brushId").handler(this::destroyBrush);
		router.route(HttpMethod.POST, "/api/brushes/:brushId/move-to").handler(this::moveBrushTo);
		router.route(HttpMethod.POST, "/api/brushes/:brushId/change-color").handler(this::changeBrushColor);
		router.route(HttpMethod.POST, "/api/brushes/:brushId/select-pixel").handler(this::selectPixel);
		router.route(HttpMethod.GET, "/api/pixel-grid").handler(this::getPixelGridState);
		this.handleEventSubscription(server, "/api/pixel-grid/events");

		/* start the server */
		
		server
		.requestHandler(router)
		.listen(port);

		logger.log(Level.INFO, "PixelArt Service ready - port: " + port);
	}

	/* List of handlers, mapping the API */
	
	protected void createBrush(RoutingContext context) {
		logger.log(Level.INFO, "CreateBrush request - " + context.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		serviceAPI
		.createBrush()
		.onSuccess((String brushId) -> {
			try {
				reply.put("brushId", brushId);
				sendReply(context.response(), reply);
			} catch (Exception ex) {
				sendServiceError(context.response());
			}
		})
		.onFailure((e) -> {
			sendServiceError(context.response());			
		});
	}

	protected void getCurrentBrushes(RoutingContext context) {
		logger.log(Level.INFO, "GetCurrentBrushes request - " + context.currentRoute().getPath());

		JsonObject reply = new JsonObject();
		serviceAPI
		.getCurrentBrushes()
		.onSuccess((JsonArray brushes) -> {
			try {
				reply.put("brushes", brushes);
				sendReply(context.response(), reply);
			} catch (Exception ex) {
				sendServiceError(context.response());
			}
		})
		.onFailure((e) -> {
			sendServiceError(context.response());			
		});
	}

	protected void getBrushInfo(RoutingContext context) {
		logger.log(Level.INFO, "Get Brush info request: " + context.currentRoute().getPath());
		String brushId = context.pathParam("brushId");
		JsonObject reply = new JsonObject();
		serviceAPI
		.getBrushInfo(brushId)
		.onSuccess((JsonObject info) -> {
			try {
				reply.put("brushInfo", info);
				sendReply(context.response(), reply);
			} catch (Exception ex) {
				sendServiceError(context.response());
			}
		})
		.onFailure((e) -> {
			sendServiceError(context.response());			
		});
	}

	protected void moveBrushTo(RoutingContext context) {
		logger.log(Level.INFO, "MoveBrushTo request: " + context.currentRoute().getPath());
		String brushId = context.pathParam("brushId");
		logger.log(Level.INFO, "Brush id: " + brushId);
		context.request().handler(buf -> {
			JsonObject brushInfo = buf.toJsonObject();
			int x = brushInfo.getInteger("x");
			int y = brushInfo.getInteger("y");
			JsonObject reply = new JsonObject();

			serviceAPI
			.moveBrushTo(brushId, y, x)
			.onSuccess((v) -> {
				try {
					sendReply(context.response(), reply);
				} catch (Exception ex) {
					sendServiceError(context.response());
				}
			})
			.onFailure((e) -> {
				sendServiceError(context.response());			
			});
		});
	}

	protected void changeBrushColor(RoutingContext context) {
		logger.log(Level.INFO, "ChangeBrushColor request: " + context.currentRoute().getPath());
		String brushId = context.pathParam("brushId");
		context.request().handler(buf -> {
			JsonObject brushInfo = buf.toJsonObject();
			logger.log(Level.INFO, "Body: " + brushInfo.encodePrettily());
			int c = brushInfo.getInteger("color");
			JsonObject reply = new JsonObject();
			serviceAPI
			.changeBrushColor(brushId, c)
			.onSuccess((v) -> {
				try {
					sendReply(context.response(), reply);
				} catch (Exception ex) {
					sendServiceError(context.response());
				}
			})
			.onFailure((e) -> {
				sendServiceError(context.response());			
			});
		});
	}

	protected void selectPixel(RoutingContext context) {
		logger.log(Level.INFO, "SelectPixel request: " + context.currentRoute().getPath());
		String brushId = context.pathParam("brushId");
		JsonObject reply = new JsonObject();
		serviceAPI
		.selectPixel(brushId)
		.onSuccess((v) -> {
			try {
				sendReply(context.response(), reply);
			} catch (Exception ex) {
				sendServiceError(context.response());
			}
		})
		.onFailure((e) -> {
			sendServiceError(context.response());			
		});
	}

	protected void destroyBrush(RoutingContext context) {
		logger.log(Level.INFO, "Destroy Brush request: " + context.currentRoute().getPath());
		String brushId = context.pathParam("brushId");
		JsonObject reply = new JsonObject();
		serviceAPI
		.destroyBrush(brushId)
		.onSuccess((v) -> {
			try {
				sendReply(context.response(), reply);
			} catch (Exception ex) {
				sendServiceError(context.response());
			}
		})
		.onFailure((e) -> {
			sendServiceError(context.response());			
		});
	}

	protected void getPixelGridState(RoutingContext context) {
		logger.log(Level.INFO, "Get Pixel Grid state request: " + context.currentRoute().getPath());
		JsonObject reply = new JsonObject();
		serviceAPI
		.getPixelGridState()
		.onSuccess((JsonObject info) -> {
			try {
				reply.put("pixelGrid", info);
				sendReply(context.response(), reply);
			} catch (Exception ex) {
				sendServiceError(context.response());
			}
		})
		.onFailure((e) -> {
			sendServiceError(context.response());			
		});
	}

	/* Handling subscribers using web sockets */
	
	protected void handleEventSubscription(HttpServer server, String path) {
		server.webSocketHandler(webSocket -> {
			if (webSocket.path().equals(path)) {
				webSocket.accept();
				logger.log(Level.INFO, "New PixelGrid subscription accepted.");
				
				JsonObject reply = new JsonObject();
				serviceAPI
				.subscribePixelGridEvents(this::pixelColorChanged)
				.onSuccess((JsonObject grid) -> {
					reply.put("event", "subscription-started");
					reply.put("pixelGridCurrentState", grid);
					webSocket.writeTextMessage(reply.encodePrettily());					

					EventBus eb = vertx.eventBus();
					eb.consumer(PIXEL_GRID_CHANNEL, msg -> {
						JsonObject ev = (JsonObject) msg.body();
						logger.log(Level.INFO, "Event: " + ev.encodePrettily());
						webSocket.writeTextMessage(ev.encodePrettily());
					});
				})
				.onFailure((e) -> {
					logger.log(Level.INFO, "PixelGrid subscription refused.");
					webSocket.reject();
				});
			} else {
				logger.log(Level.INFO, "PixelGrid subscription refused.");
				webSocket.reject();
			}
		});
	}
	
	/* This is notified by the application/domain layer */
	
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

	/* Aux methods */
	

	private void sendReply(HttpServerResponse response, JsonObject reply) {
		response.putHeader("content-type", "application/json");
		response.end(reply.toString());
	}
	
	private void sendBadRequest(HttpServerResponse response, JsonObject reply) {
		response.setStatusCode(400);
		response.putHeader("content-type", "application/json");
		response.end(reply.toString());
	}

	private void sendServiceError(HttpServerResponse response) {
		response.setStatusCode(500);
		response.putHeader("content-type", "application/json");
		response.end();
	}

}
