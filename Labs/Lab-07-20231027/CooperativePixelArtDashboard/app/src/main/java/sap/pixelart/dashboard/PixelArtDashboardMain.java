package sap.pixelart.dashboard;

import sap.pixelart.dashboard.controller.Controller;
import sap.pixelart.library.*;

public class PixelArtDashboardMain {
	
	public static void main(String[] args) {

		PixelArtAsyncAPI proxy = PixelArtServiceLib.getInstance().getDefaultInterface();
		Controller controller = new Controller(proxy);
		controller.init();
		
	}
}
