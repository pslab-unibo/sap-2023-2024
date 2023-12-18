package smart_room.distributed;

import sap.smartroom.Event;

public class TestPresDetectDevice {

	public static void main(String[] args) throws Exception {

		PresDetectSensorSimulator pd = new PresDetectSensorSimulator("MyPIR");
		pd.init();
		
		pd.register((Event ev) -> {
			System.out.println("New event: " + ev);
		});

		while (true) {
			System.out.println(pd.presenceDetected());
			Thread.sleep(1000);
		}
	}

}
