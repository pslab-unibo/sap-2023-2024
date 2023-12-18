package sap.smartroom_agent_refined;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Timer {
	
	private Vertx vertx;
	private long timerID = -1;
	
	public Timer(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void init(int nMilliseconds, JsonObject evt) {
		timerID = vertx.setTimer(nMilliseconds, id -> {
			vertx.eventBus().publish("events", evt);
			timerID = -1;
		});		
	}
	
	public void cancel() {
		if (timerID != -1) {
			vertx.cancelTimer(timerID);
		}
	}

}
