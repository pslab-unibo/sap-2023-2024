package sap.escooters.presentation_layer;

import java.util.Optional;

import io.vertx.core.Vertx;
import sap.escooters.service_layer.*;
import sap.layers.Layer;

public class PresentationLayerImpl implements PresentationLayer {

	private ServiceLayer serviceLayer;
	
	public PresentationLayerImpl() {	
	}
		
	@Override
	public void init(Optional<Layer> layer) {
		serviceLayer = (ServiceLayer) layer.get();
    	Vertx vertx = Vertx.vertx();
		EScooterManServer myVerticle = new EScooterManServer(8081, serviceLayer);
		vertx.deployVerticle(myVerticle);		
	}
}
