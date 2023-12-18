package smart_room.distributed;

import sap.smartroom.Event;

public class PresenceDetected extends Event {

	public PresenceDetected(long timestamp) {
		super(timestamp);
	}

}
