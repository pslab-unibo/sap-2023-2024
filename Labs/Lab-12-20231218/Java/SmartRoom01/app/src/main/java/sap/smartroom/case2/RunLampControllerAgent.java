package sap.smartroom.case2;

import smart_room.distributed.*;

public class RunLampControllerAgent {
	
	public static void main(String[] args) throws Exception {

		LampDeviceSimulator ld = new LampDeviceSimulator("MyLight");
		ld.init();
		
		try {
			LampControllerAgent agent = new LampControllerAgent(ld, 0.2);
			agent.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
