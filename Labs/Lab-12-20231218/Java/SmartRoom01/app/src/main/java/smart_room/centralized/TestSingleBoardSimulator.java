package smart_room.centralized;

import sap.smartroom.Event;

public class TestSingleBoardSimulator {

	public static void main(String[] args) throws Exception {

		SinglelBoardSimulator board = new SinglelBoardSimulator();
		board.init();
	
		board.register((Event ev) -> {
			System.out.println("New event: " + ev);
		});
		
		new Thread(() -> {
			while (true) {
				try {
					board.on();
					Thread.sleep(2000);
					board.off();
					Thread.sleep(2000);
				} catch (Exception ex) {}
			}
		}).start();
		
		while (true) {
			System.out.println("Pres Det: " + board.presenceDetected() + " - Light level: " + board.getLuminosity());
			Thread.sleep(1000);
		}
	}

}
