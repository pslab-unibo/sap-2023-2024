/**
 * Simulator/mock for a presence detection sensor device.
 * 
 */
package sap.pres_detect_thing.impl;

import sap.common.AbstractEventSource;

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
