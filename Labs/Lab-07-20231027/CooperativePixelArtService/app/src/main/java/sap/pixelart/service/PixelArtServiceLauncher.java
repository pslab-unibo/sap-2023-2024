package sap.pixelart.service;

import sap.pixelart.service.application.*;
import sap.pixelart.service.infrastructure.WebBasedUIAdapter;

public class PixelArtServiceLauncher {
	
	private static int DEFAULT_PORT = 9000;
	
    public static void main(String[] args) {
    	PixelArtServiceImpl service = new PixelArtServiceImpl();
    	WebBasedUIAdapter ui = new WebBasedUIAdapter(DEFAULT_PORT);
    	service.init();
    	ui.init(service);
    }
}
