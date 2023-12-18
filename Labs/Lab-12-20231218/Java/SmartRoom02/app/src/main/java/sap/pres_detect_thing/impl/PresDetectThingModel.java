package sap.pres_detect_thing.impl;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import sap.common.Event;

/**
 * 
 * Behaviour of the Presence Detection Sensor Thing 
 * 
 * @author aricci
 *
 */
public class PresDetectThingModel implements PresDetectThingAPI {

	private Vertx vertx;

	private boolean presenceDetected;
	
	private String thingId;
	private JsonObject td;
	private PresDetectSensorSimulator pd;
	

	public PresDetectThingModel(String thingId) {
		log("Creating the presence detection simulator.");
		this.thingId = thingId;
		pd = new PresDetectSensorSimulator(thingId);
	}
	
	public void setup(Vertx vertx) {
		this.vertx = vertx;
		td = new JsonObject();
		
		td.put("@context", "https://www.w3.org/2019/wot/td/v1");
		td.put("id", thingId);
		td.put("title", thingId);
		
		/* security section */

		JsonArray schemas = new JsonArray();
		td.put("security", schemas );
		JsonObject noSec = new JsonObject();
		noSec.put("scheme", "nosec");
		schemas.add(noSec);
		
		/* affordances */
		
		/* properties */
		
		JsonObject props = new JsonObject();
		td.put("properties", props);
		JsonObject state = new JsonObject();
		props.put("presenceDetected", state);		
		state.put("type", "boolean");
		state.put("forms", new JsonArray());
						
		/* events */
		
		JsonObject events = new JsonObject();
		td.put("events", events);
		
		JsonObject detected = new JsonObject();
		events.put("presenceDetected", detected);		
		JsonObject data = new JsonObject();
		detected.put("data", data);
		JsonObject dataType = new JsonObject();
		data.put("type", dataType);
		dataType.put("timestamp", "decimal"); // better would be: "time"
		detected.put("forms",  new JsonArray());

		JsonObject noMoreDetected = new JsonObject();
		events.put("presenceNoMoreDetected", noMoreDetected);		
		JsonObject data2 = new JsonObject();
		noMoreDetected.put("data", data2);
		JsonObject dataType2 = new JsonObject();
		data2.put("type", dataType2);
		dataType2.put("timestamp", "decimal"); // better would be: "time"
		noMoreDetected.put("forms",  new JsonArray());
		
		pd.init();
		this.presenceDetected = false;
		pd.register((Event ev) -> {
			synchronized (this) {
				if (ev instanceof PresenceDetected) {
					this.presenceDetected = true;
			    	this.notifyDetected();	    
				} else if (ev instanceof PresenceNoMoreDetected) {
					this.presenceDetected = false;
			    	this.notifyNoMoreDetected();	    
				}
			}
		});
		
		
	}

	public Future<JsonObject> getTD() {
		Promise<JsonObject> p = Promise.promise();
		p.complete(td);
		return p.future();
	}

	@Override
	public Future<Boolean> isDetected() {
		Promise<Boolean> p = Promise.promise();
		synchronized (this) {
			p.complete(presenceDetected);
		}
		return p.future();
	}

	private void notifyDetected() {
	    JsonObject ev = new JsonObject();
		ev.put("event", "presenceDetected");
		ev.put("timestamp", System.currentTimeMillis());
		this.generateEvent(ev);
	}

	private void notifyNoMoreDetected() {
	    JsonObject ev = new JsonObject();
		ev.put("event", "presenceNoMoreDetected");
		ev.put("timestamp", System.currentTimeMillis());
		this.generateEvent(ev);
	}

	private void generateEvent(JsonObject ev) {
		vertx.eventBus().publish("events", ev);	
	}
	
	public Future<Void> subscribe(Handler<JsonObject> h) {
		Promise<Void> p = Promise.promise();
		vertx.eventBus().consumer("events", ev -> {
			h.handle((JsonObject) ev.body());
		});	
		p.complete();
		return p.future();
	}
		
	protected void log(String msg) {
		System.out.println("[PresDetThingModel]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
