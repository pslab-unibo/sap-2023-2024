package sap.escooters.infrastructure_layer.ui;

import java.util.logging.Logger;
import io.vertx.core.Vertx;
import sap.escooters.application_layer.*;

public class WebBasedUIAdapter implements RideDashboardPort {
    static Logger logger = Logger.getLogger("[EScooter Server]");	
	private int port;
	private EScooterManServer server;
	
	public WebBasedUIAdapter(int port) {	
		this.port = port;
	}
		
	public void init(ApplicationAPI appAPI) {
    	Vertx vertx = Vertx.vertx();
		this.server = new EScooterManServer(port, appAPI);
		vertx.deployVerticle(server);		
	}

	@Override
	public void notifyNumOngoingRidesChanged(int nOngoingRides) {
		server.notifyNumOngoingRidesChanged(nOngoingRides);
	}
}
