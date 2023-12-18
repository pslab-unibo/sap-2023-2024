package sap.smartroom.case2;

import io.vertx.core.json.JsonObject;
import sap.smartroom.Event;
import sap.smartroom.LightSensorDevice;
import sap.smartroom.common.BasicEventLoopAgent;
import smart_room.distributed.*;

public class RunLightSensingAgent {
	
	private static final String FIXED_LIGHT_CONTROLLER_CHANNEL_NAME = "light-controller";

	public static void main(String[] args) throws Exception {

		LightSensorSimulator lsd = new LightSensorSimulator("Lum-Sensing-Agent");
		lsd.init();
		
		try {
			LightSensingAgent agent = new LightSensingAgent(lsd, FIXED_LIGHT_CONTROLLER_CHANNEL_NAME);
			agent.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
