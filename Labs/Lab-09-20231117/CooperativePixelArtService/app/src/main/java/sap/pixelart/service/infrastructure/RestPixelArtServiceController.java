package sap.pixelart.service.infrastructure;

import java.util.logging.Level;
import java.util.logging.Logger;
import io.vertx.core.Vertx;
import sap.pixelart.service.application.*;
import sap.pixelart.service.domain.PixelGridEventObserver;

/**
 * 
 * Adapter implementing a REST API  
 * 
 * - interacting with the application layer through the PixelArtAPI interface 
 * 
 * @author aricci
 *
 */
public class RestPixelArtServiceController implements PixelGridEventObserver {
    static Logger logger = Logger.getLogger("[WebUIAdapter]");	
	private int port;
	private RestPixelArtServiceControllerVerticle service;
	
	public RestPixelArtServiceController(int port) {	
		this.port = port;
	}
		
	public void init(PixelArtAPI pixelArtAPI) {
    	Vertx vertx = Vertx.vertx();
		this.service = new RestPixelArtServiceControllerVerticle(port, pixelArtAPI);
		vertx.deployVerticle(service);	
		pixelArtAPI.subscribePixelGridEvents(this);
	}

	/* called by the application layer */
	
	@Override
	public void pixelColorChanged(int x, int y, int color) {
		logger.log(Level.INFO, "New PixelGrid event - pixel selected " + x + " " + y + " " + color);
		service.pixelColorChanged(x, y, color);
	}

}
