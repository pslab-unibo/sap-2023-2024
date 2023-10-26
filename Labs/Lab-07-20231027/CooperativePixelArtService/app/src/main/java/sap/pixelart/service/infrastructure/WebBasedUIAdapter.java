package sap.pixelart.service.infrastructure;

import java.util.logging.Level;
import java.util.logging.Logger;
import io.vertx.core.Vertx;
import sap.pixelart.service.application.*;
import sap.pixelart.service.domain.PixelGridEventObserver;

public class WebBasedUIAdapter implements PixelGridEventObserver {
    static Logger logger = Logger.getLogger("[WebUIAdapter]");	
	private int port;
	private PixelArtService service;
	
	public WebBasedUIAdapter(int port) {	
		this.port = port;
	}
		
	public void init(PixelArtAPI pixelGridAPI) {
    	Vertx vertx = Vertx.vertx();
		this.service = new PixelArtService(port, pixelGridAPI);
		vertx.deployVerticle(service);	
		pixelGridAPI.subscribePixelGridEvents(this);
	}

	@Override
	public void pixelColorChanged(int x, int y, int color) {
		logger.log(Level.INFO, "New PixelGrid event - pixel selected " + x + " " + y + " " + color);
		service.pixelColorChanged(x, y, color);
	}

}
