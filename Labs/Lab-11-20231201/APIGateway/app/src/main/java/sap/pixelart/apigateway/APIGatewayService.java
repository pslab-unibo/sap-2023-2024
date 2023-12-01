package sap.pixelart.apigateway;

import sap.pixelart.apigateway.infrastructure.APIGatewayController;
import sap.pixelart.library.PixelArtAsyncAPI;
import sap.pixelart.library.PixelArtServiceLib;

public class APIGatewayService {

	private static int DEFAULT_HTTP_PORT = 9001;

	private PixelArtAsyncAPI service;
	private APIGatewayController restBasedAdapter;
	private int restAPIPort; 
	
	public APIGatewayService() {
    	service = PixelArtServiceLib.getInstance().getDefaultInterface();
    	restAPIPort = DEFAULT_HTTP_PORT;
	}
	
	public void configure(int port) {
		restAPIPort = port;
	}
	
	public void launch() {
    	restBasedAdapter = new APIGatewayController(restAPIPort);	    	
    	restBasedAdapter.init(service);
	}
}
