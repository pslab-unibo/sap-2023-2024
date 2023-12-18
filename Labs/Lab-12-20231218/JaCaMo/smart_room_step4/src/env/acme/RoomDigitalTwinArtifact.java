package acme;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import cartago.*;
import io.vertx.core.Vertx;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;


public class RoomDigitalTwinArtifact extends Artifact {

	
	private String host;
	private int port;
	private HttpClient client;
	private String uri;
	
	private static final String PROPERTY_TEMPERATURE = "/properties/temperature";
	private static final String PROPERTY_STATE = "/properties/state";
	private static final String ACTION_STARTHEATING = "/actions/startHeating";
	private static final String ACTION_STARTCOOLING = "/actions/startCooling";
	private static final String ACTION_STOPWORKING = "/actions/stopWorking";
	private static final String EVENTS = "/events";
	
	public void init(String host, int port) throws Exception {
		this.host = host;
		this.port = port;
		this.uri = "http://"+ host + ":" + port;
		client = HttpClient.newHttpClient();	
		bindToPhysicalThing();
		log("ready.");
	}


	private void bindToPhysicalThing() throws Exception {
		
		log("connecting to " + uri);

		/* read initial state */
		
		JsonObject temp = this.doGetBlocking(uri + PROPERTY_TEMPERATURE);
		defineObsProperty("temperature", temp.getDouble("temperature"));
		
		JsonObject status = this.doGetBlocking(uri + PROPERTY_STATE);
		defineObsProperty("state", status.getString("state"));

		/* subscribe */
		
		this.doSubscribe();
		
	}	

	@OPERATION void getCurrentTemperature(OpFeedbackParam<Double> temp) {
		try {
			log("getting the temperature.");
			JsonObject obj = this.doGetBlocking(uri + PROPERTY_TEMPERATURE);
			temp.set(obj.getDouble("temperature"));
		} catch (Exception ex) {
			failed("");
		}
	}

	@OPERATION void getCurrentState(OpFeedbackParam<String> state) {
		try {
			log("getting the state.");
			JsonObject obj = this.doGetBlocking(uri + PROPERTY_STATE);
			state.set(obj.getString("state"));
		} catch (Exception ex) {
			failed("");
		}
	}

	@OPERATION void startCooling() {
		try {
			log("start cooling.");
			this.doPostBlocking(uri + this.ACTION_STARTCOOLING,Optional.empty());
		} catch (Exception ex) {
			ex.printStackTrace();
			failed("");
		}
	}

	@OPERATION void startHeating() {
		try {
			log("start heating.");
			this.doPostBlocking(uri + this.ACTION_STARTHEATING,Optional.empty());
		} catch (Exception ex) {
			failed("");
		}
	}

	@OPERATION void stopWorking() {
		try {
			log("start working.");
			this.doPostBlocking(uri + this.ACTION_STOPWORKING,Optional.empty());
		} catch (Exception ex) {
			failed("");
		}
	}


	// aux actions

	private JsonObject doGetBlocking(String uri) throws Exception {
		try {
			var request = HttpRequest.newBuilder()
					.uri(URI.create(uri))
					.build();		

			var response = client.send(request,  BodyHandlers.ofString());
			return new JsonObject(response.body());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	private JsonObject doPostBlocking(String uri, Optional<JsonObject> body) throws Exception {
		HttpRequest req = null;
		// log("doing a post at " + "http://" + uri);
		if (!body.isEmpty()) {
			req  = HttpRequest.newBuilder()
					.uri(URI.create(uri))
					.POST(BodyPublishers.ofString(body.get().toString()))
					.build();		

		} else {
			req = HttpRequest.newBuilder()
					.uri(URI.create(uri))
					.POST(BodyPublishers.noBody())
					.build();		
		}

		var response = client.send(req, BodyHandlers.ofString());
		// log(" >> " + response.statusCode());
		return null; // new JsonObject(response.body());
	}
	
	private void doSubscribe() {
		Vertx vertx = Vertx.vertx();
		RoomDigitalTwinArtifact art = this;
		log("Subscribing...");
		vertx.createHttpClient().websocket(port, host, this.EVENTS, ws -> {	
			/* handling ws msgs */
			log("Connected!");
			ws.handler(msg -> {
				try {
					JsonObject ev = new JsonObject(msg.toString());		    	  
					String msgType = ev.getString("event");
					if (msgType.equals("propertyStatusChanged")) {
						JsonObject data = ev.getJsonObject("data");
						Double newTemperature = data.getDouble("temperature");
						String newState = data.getString("state");

						// log("updating artifact state with " + newTemperature + " " + newState);

						art.beginExtSession();							
						if (newTemperature != null) {
							ObsProperty tprop = getObsProperty("temperature");
							tprop.updateValue(newTemperature);
						}
						if (newState != null) {
							ObsProperty sprop = getObsProperty("state");
							sprop.updateValue(newState);
						}
						art.endExtSession();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});		
		});
		
	}

}
