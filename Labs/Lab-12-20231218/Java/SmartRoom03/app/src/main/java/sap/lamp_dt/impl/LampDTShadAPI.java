package sap.lamp_dt.impl;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Toy Lamp Digital Twin Java API for the Shadowing Layer
 *   
 * @author aricci
 *
 */
public interface LampDTShadAPI {
	
	
	Future<String> updateState(String newState, long timeStamp);
	

}
