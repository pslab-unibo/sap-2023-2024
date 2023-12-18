package sap.pres_detect_thing.impl;

import sap.common.EventSource;

public interface PresenceDetectionDevice extends EventSource {
	
	boolean presenceDetected();

}
