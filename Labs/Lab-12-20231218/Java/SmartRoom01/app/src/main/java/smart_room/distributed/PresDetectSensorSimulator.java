/**
 * Simulator/mock for a presence detection sensor device.
 * 
 */
package smart_room.distributed;

import sap.smartroom.*;
import sap.smartroom.AbstractEventSource;
import sap.smartroom.PresenceDetectionDevice;

public class PresDetectSensorSimulator extends AbstractEventSource implements PresenceDetectionDevice {

	private boolean isPresenceDetected;
	private String sensorId;
	private PresenceDetectionFrame frame;
	
	public PresDetectSensorSimulator(String sensorId){
		this.sensorId = sensorId;
		isPresenceDetected = false;
	}
	
	public void init() {
		frame = new PresenceDetectionFrame(this, sensorId);
		frame.display();
	}
	
	@Override
	public synchronized boolean presenceDetected() {
		return isPresenceDetected;
	}

	synchronized void updateValue(boolean value) {
		long ts = System.currentTimeMillis();
		this.isPresenceDetected = value;
		if (value) {
			this.notifyEvent(new PresenceDetected(ts));
		} else {
			this.notifyEvent(new PresenceNoMoreDetected(ts));
		}
	}






}
