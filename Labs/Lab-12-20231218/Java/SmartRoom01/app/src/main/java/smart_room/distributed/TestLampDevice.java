package smart_room.distributed;

public class TestLampDevice {

	public static void main(String[] args) throws Exception {

		LampDeviceSimulator ld = new LampDeviceSimulator("MyLight");
		ld.init();

		while (true) {
			ld.on();
			Thread.sleep(1000);
			ld.off();
			Thread.sleep(1000);
		}
		
	}

}
