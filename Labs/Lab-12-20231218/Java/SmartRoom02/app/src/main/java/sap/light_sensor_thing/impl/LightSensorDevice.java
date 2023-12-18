package sap.light_sensor_thing.impl;

import sap.common.EventSource;

public interface LightSensorDevice extends EventSource {

	double getLuminosity();
		
}
