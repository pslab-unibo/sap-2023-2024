package sap.common.agent;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Base class for simple event-loop based agents using WoT things
 * 
 * @author aricci
 *
 */
public abstract class ReactiveAgent extends AbstractVerticle {
	
	private String name;
	
	protected ReactiveAgent(String name) {
		this.name = name;
	}

	public void start(Promise<Void> startPromise) throws Exception {
		this.getVertx().eventBus().consumer("events", evt -> {
			processEvent((JsonObject) (evt.body()));
		});
		init();
		startPromise.complete();
	}
	
	protected abstract void init();

	protected abstract void processEvent(JsonObject ev);
	
	protected void startObserving(Thing t) {
		t.subscribe(event -> {
			processEvent(event);
		});
	}

	protected void invokeActionOn(Thing t, String name) {
		t.invokeAction(name);
	}

	protected long setTimer(long ms, JsonObject ev) {
		return vertx.setTimer(ms, id -> processEvent(ev));
	}
	
	protected void log(String msg) {
		System.out.println("[" + name + "["+System.currentTimeMillis()+"] " + msg);
	}
		
}
