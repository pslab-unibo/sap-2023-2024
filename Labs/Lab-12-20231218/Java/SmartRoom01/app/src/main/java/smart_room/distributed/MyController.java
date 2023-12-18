package smart_room.distributed;

import sap.smartroom.Controller;
import sap.smartroom.Event;

public class MyController implements Controller {

	@Override
	public void notifyEvent(Event ev) {
		System.out.println("New event: " + ev);
	}

	
}
