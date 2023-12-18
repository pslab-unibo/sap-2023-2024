package sap.lamp_thing.impl;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 
 * Behaviour of the Lamp Thing 
 * 
 * @author aricci
 *
 */
public class LampThingModel implements LampThingAPI {

	private Vertx vertx;

	private String state;
	
	private String thingId;
	private JsonObject td;
	private LampDeviceSimulator ld;
	

	public LampThingModel(String thingId) {
		log("Creating the light thing simulator.");
		this.thingId = thingId;
		
	    state = "off";
	    
		ld = new LampDeviceSimulator(thingId);
		ld.init();	    
	}
	
	public void setup(Vertx vertx) {
		this.vertx = vertx;
		td = new JsonObject();
		
		td.put("@context", "https://www.w3.org/2019/wot/td/v1");
		td.put("id", thingId);
		td.put("title", "MyLampThing");
		
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
		props.put("state", state);		
		state.put("type", "string");
		state.put("forms", new JsonArray());
				
		/* actions */
		
		JsonObject actions = new JsonObject();
		td.put("actions", actions);
		JsonObject on = new JsonObject();
		actions.put("on", on);		
		on.put("forms", new JsonArray());
		JsonObject off = new JsonObject();
		actions.put("off", off);		
		off.put("forms", new JsonArray());
		
		/* events */
		
		JsonObject events = new JsonObject();
		td.put("events", events);
		JsonObject stateChanged = new JsonObject();
		events.put("stateChanged", stateChanged);		
		JsonObject data = new JsonObject();
		stateChanged.put("data", data);
		JsonObject dataType = new JsonObject();
		data.put("type", dataType);
		dataType.put("state", "string");
		dataType.put("timestamp", "decimal"); // better would be: "time"
		stateChanged.put("forms",  new JsonArray());

	}

	public Future<JsonObject> getTD() {
		Promise<JsonObject> p = Promise.promise();
		p.complete(td);
		return p.future();
	}

	@Override
	public Future<String> getState() {
		Promise<String> p = Promise.promise();
		synchronized (this) {
			p.complete(state);
		}
		return p.future();
	}

	@Override
	public Future<Void> on() {
		Promise<Void> p = Promise.promise();
		ld.on();
	    state = "on";
    	this.notifyNewPropertyStatus();	    
		p.complete();
		return p.future();
	}

	@Override
	public Future<Void> off() {
		Promise<Void> p = Promise.promise();
		ld.off();
	    state = "off";
    	this.notifyNewPropertyStatus();	    
		p.complete();
		return p.future();
	}
	
	private void notifyNewPropertyStatus() {
	    JsonObject ev = new JsonObject();
		ev.put("event", "stateChanged");
	    JsonObject data = new JsonObject();
		data.put("state", state);
		ev.put("data", data);			
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
		System.out.println("[LampThingModel]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
