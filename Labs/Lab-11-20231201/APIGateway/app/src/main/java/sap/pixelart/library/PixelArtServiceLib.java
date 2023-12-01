package sap.pixelart.library;

/**
 * 
 * Library for interacting with the PixelArt service
 * 
 * - it is a singleton factory
 * 
 * @author aricci
 *
 */
public class PixelArtServiceLib {

	private final String DEFAULT_HOST = "localhost";
	private final int DEFAULT_PORT = 9000;
	
	private static PixelArtServiceLib instance;
	
	private PixelArtServiceLib() {
	}
	
	static public PixelArtServiceLib getInstance() {
		synchronized (PixelArtServiceLib.class) {
			if (instance == null) {
				instance = new PixelArtServiceLib();
			}
			return instance;
		}
	}
	
	public PixelArtAsyncAPI getDefaultInterface() {
		PixelArtServiceProxy serviceProxy = new PixelArtServiceProxy();
		serviceProxy.init(DEFAULT_HOST, DEFAULT_PORT);
		return serviceProxy;
	}
	
	public PixelArtAsyncAPI getHTTPInterface(String host, int port) {
		PixelArtServiceProxy serviceProxy = new PixelArtServiceProxy();
		serviceProxy.init(host, port);
		return serviceProxy;
	}
	
}
