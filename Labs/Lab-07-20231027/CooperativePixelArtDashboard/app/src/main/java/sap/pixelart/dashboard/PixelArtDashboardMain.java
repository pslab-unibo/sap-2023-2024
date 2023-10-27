package sap.pixelart.dashboard;

import sap.pixelart.dashboard.controller.Controller;
import sap.pixelart.library.PixelArtAsyncAPI;
import sap.pixelart.library.PixelArtServiceLib;

public class PixelArtDashboardMain {
	
	public static void main(String[] args) {

		PixelArtAsyncAPI proxy = PixelArtServiceLib.getInstance().getDefaultInterface();
		Controller controller = new Controller(proxy);
		controller.init();
		
	}
}
