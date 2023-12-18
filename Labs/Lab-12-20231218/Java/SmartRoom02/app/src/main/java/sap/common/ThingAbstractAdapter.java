package sap.common;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public abstract class ThingAbstractAdapter<ThingAPI>  {

	private Vertx vertx;
	
	private ThingAPI model;
	
	protected ThingAbstractAdapter(ThingAPI model, Vertx vertx) {
		this.model = model;
		this.vertx = vertx;
	}
	
	protected Vertx getVertx() {
		return vertx;
	}

	protected ThingAPI getModel() {
		return model;
	}
	
	abstract protected void setupAdapter(Promise<Void> startPromise);
	
}
