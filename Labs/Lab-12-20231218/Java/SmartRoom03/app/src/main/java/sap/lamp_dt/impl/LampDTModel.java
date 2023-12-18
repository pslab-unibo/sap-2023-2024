package sap.lamp_dt.impl;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 
 * Model of the Lamp Digital Twin 
 * 
 * @author aricci
 *
 */
public class LampDTModel implements LampDTAppAPI, LampDTShadAPI {

	private Vertx vertx;

	private String state;
	private List<JsonObject> eventHistory;
	private String dtId;
	private long lastUpdatePATime;
	private long lastUpdateDTTime;
	

	public LampDTModel(String dtId) {
		this.dtId = dtId;		
	}
	
	public void setup(Vertx vertx) {
		this.vertx = vertx;
		eventHistory = new ArrayList<JsonObject>();
		log("setup.");
	}
	
	/* Application Interface */

	public Future<JsonObject> getState() {
		Promise<JsonObject> p = Promise.promise();
		JsonObject state = new JsonObject();
		synchronized (this) {
			state.put("state", this.state);
			state.put("lastUpdatePA", lastUpdatePATime);
			state.put("lastUpdateDT", lastUpdateDTTime);			
			p.complete(state);
		}
		return p.future();
	}

	public Future<JsonArray> getHistory() {
		Promise<JsonArray> p = Promise.promise();
		JsonArray array = new JsonArray();
		synchronized (this) {
			for (JsonObject e: eventHistory) {
				array.add(e);
			}
			p.complete(array);
		}
		return p.future();
	}
	
	/*
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
	*/
	
	public Future<Void> subscribe(Handler<JsonObject> h) {
		Promise<Void> p = Promise.promise();
		vertx.eventBus().consumer("events", ev -> {
			h.handle((JsonObject) ev.body());
		});	
		p.complete();
		return p.future();
	}
		

	public String getDTId() {
		return dtId;
	}

	
	/*	Shadowing interface	 	*/

	@Override
	public Future<String> updateState(String newState, long timeStamp) {
		Promise<String> p = Promise.promise();
		synchronized (this) {
			this.state = newState;
			this.lastUpdatePATime = timeStamp;
			this.lastUpdateDTTime = System.currentTimeMillis();
			p.complete(state);
			log("shadowing | state updated: " + newState + " - PA Time: " + this.lastUpdatePATime + " - DT Time: " + this.lastUpdateDTTime);
	    	this.notifyNewPropertyStatus();	    
		}
		return p.future();
	}


	/* aux */
	
	private void notifyNewPropertyStatus() {
	    JsonObject ev = new JsonObject();
		ev.put("event", "stateChanged");
	    JsonObject data = new JsonObject();
		data.put("state", state);
		data.put("timestamp", this.lastUpdatePATime);
		ev.put("data", data);			
		ev.put("timestamp", lastUpdateDTTime);
		eventHistory.add(ev);
		this.generateEvent(ev);
	}

	private void generateEvent(JsonObject ev) {
		vertx.eventBus().publish("events", ev);	
	}
	
	protected void log(String msg) {
		System.out.println("[LampDTModel]["+System.currentTimeMillis()+"] " + msg);
	}
		
}
