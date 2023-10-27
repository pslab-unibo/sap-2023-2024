package sap.pixelart.service;

import sap.pixelart.service.application.PixelArtServiceImpl;
import sap.pixelart.service.infrastructure.RestPixelArtService;

public class PixelArtService {

	private static int DEFAULT_HTTP_PORT = 9000;

	private PixelArtServiceImpl service;
	private RestPixelArtService restBasedAdapter;
	private int restAPIPort; 
	
	public PixelArtService() {
    	service = new PixelArtServiceImpl();
    	restAPIPort = DEFAULT_HTTP_PORT;
	}
	
	public void configure(int port) {
		restAPIPort = port;
	}
	
	public void launch() {
    	service.init();
		restBasedAdapter = new RestPixelArtService(restAPIPort);	    	
    	restBasedAdapter.init(service);
	}
}
