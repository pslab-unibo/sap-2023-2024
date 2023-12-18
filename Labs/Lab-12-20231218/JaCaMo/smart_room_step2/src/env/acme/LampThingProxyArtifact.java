package acme;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import cartago.*;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;


public class LampThingProxyArtifact extends Artifact {

	
	private String host;
	private int port;
	private HttpClient client;
	private String uri;
	
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
		
		JsonObject temp = this.doGetBlocking(uri + PROPERTY_STATE_FULL_PATH);
		defineObsProperty("state", temp.getString("state"));
		
		/* subscribe */
		
		this.doSubscribe();
		
	}	

	@OPERATION void getCurrentState(OpFeedbackParam<String> state) {
		try {
			log("getting the state.");
			JsonObject obj = this.doGetBlocking(uri + PROPERTY_STATE_FULL_PATH);
			state.set(obj.getString("state"));
		} catch (Exception ex) {
			failed("");
		}
	}

	@OPERATION void on() {
		try {
			log("on...");
			this.doPostBlocking(uri + ACTION_ON_FULL_PATH,Optional.empty());
		} catch (Exception ex) {
			ex.printStackTrace();
			failed("");
		}
	}

	@OPERATION void off() {
		try {
			log("off.");
			this.doPostBlocking(uri + ACTION_OFF_FULL_PATH,Optional.empty());
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
		LampThingProxyArtifact art = this;
		log("Subscribing...");
		vertx.createHttpClient().websocket(port, host, EVENTS_FULL_PATH, ws -> {	
			/* handling ws msgs */
			log("Connected!");
			ws.handler(msg -> {
				try {
					JsonObject ev = new JsonObject(msg.toString());		    	  
					String msgType = ev.getString("event");
					if (msgType.equals("stateChanged")) {
						JsonObject data = ev.getJsonObject("data");
						String newState = data.getString("state");

						// log("updating artifact state with " + newTemperature + " " + newState);

						art.beginExtSession();							

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
