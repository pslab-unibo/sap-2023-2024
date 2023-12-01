package sap.pixelart.apigateway;

/**
 * 
 * Cooperative PixelArt Service launcher
 * 
 * @author aricci
 *
 */
public class APIGatewayServiceLauncher {
		
    public static void main(String[] args) {

    	APIGatewayService service = new APIGatewayService();
    	service.launch();
    }
}
