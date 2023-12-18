package sap.light_sensor_thing.impl;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import sap.common.Event;

/**
 * 
 * Behaviour of the Light Sensor Thing 
 * 
 * @author aricci
 *
 */
public class LightSensorThingModel implements LightSensorThingAPI {

	private Vertx vertx;

	private double lightLevel;
	
	private String thingId;
	private JsonObject td;
	private LightSensorSimulator ls;
	

	public LightSensorThingModel(String thingId) {
		log("Creating the light sensor simulator.");
		this.thingId = thingId;
		ls = new LightSensorSimulator(thingId);
	}
	
	public void setup(Vertx vertx) {
		this.vertx = vertx;
		td = new JsonObject();
		
		td.put("@context", "https://www.w3.org/2019/wot/td/v1");
		td.put("id", thingId);
		td.put("title", "MyLightSensorThing");
		
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
		props.put("lightLevel", state);		
		state.put("type", "number");
		state.put("forms", new JsonArray());
						
		/* events */
		
		JsonObject events = new JsonObject();
		td.put("events", events);
		
		JsonObject lightLevelChanged = new JsonObject();
		events.put("lightLevelChanged", lightLevelChanged);		
		JsonObject data = new JsonObject();
		lightLevelChanged.put("data", data);
		JsonObject dataType = new JsonObject();
		data.put("type", dataType);
		dataType.put("lightLevel", "number");
		dataType.put("timestamp", "decimal"); // better would be: "time"
		lightLevelChanged.put("forms",  new JsonArray());
		
		ls.init();
		this.lightLevel = 0;
		ls.register((Event ev) -> {
			synchronized (this) {
				if (ev instanceof LightLevelChanged) {
					this.lightLevel = ((LightLevelChanged) ev).getNewLevel();
			    	this.notifyNewLevel();	    
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
	public Future<Double> getLightLevel() {
		Promise<Double> p = Promise.promise();
		synchronized (this) {
			p.complete(lightLevel);
		}
		return p.future();
	}

	private void notifyNewLevel() {
	    JsonObject ev = new JsonObject();
		ev.put("event", "lightLevelChanged");
	    JsonObject data = new JsonObject();
		data.put("lightLevel", lightLevel);
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
		System.out.println("[LightSensorThingModel]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
