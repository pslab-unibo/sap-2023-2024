/**
 * Simulator/mock for a luminosity sensor device
 * 
 */
package sap.light_sensor_thing.impl;

import sap.common.*;

public class LightSensorSimulator  extends AbstractEventSource implements LightSensorDevice {

	private double currentLuminosityLevel;
	private String sensorId;
	private LightSensorFrame frame;
	
	public LightSensorSimulator(String sensorId){
		this.sensorId = sensorId;
	}
	
	public void init() {
		frame = new LightSensorFrame(this,sensorId);
		frame.display();
	}
	
	@Override
	public synchronized double getLuminosity() {
		return currentLuminosityLevel;
	}

    synchronized void updateValue(double value) {
    	long ts = System.currentTimeMillis();
		this.currentLuminosityLevel = value;
		this.notifyEvent(new LightLevelChanged(ts, value));
	}

}
