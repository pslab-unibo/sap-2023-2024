package sap.escooters.presentation_layer;

import java.util.Optional;

import io.vertx.core.Vertx;
import sap.escooters.service_layer.*;
import sap.layers.Layer;

public class PresentationLayerImpl implements PresentationLayer {

	private ServiceLayer serviceLayer;
	private int port;
	
	public PresentationLayerImpl(int port) {	
		this.port = port;
	}
		
	@Override
	public void init(Optional<Layer> layer) {
		serviceLayer = (ServiceLayer) layer.get();
    	Vertx vertx = Vertx.vertx();
		EScooterManServer myVerticle = new EScooterManServer(port, serviceLayer);
		vertx.deployVerticle(myVerticle);		
	}
}
