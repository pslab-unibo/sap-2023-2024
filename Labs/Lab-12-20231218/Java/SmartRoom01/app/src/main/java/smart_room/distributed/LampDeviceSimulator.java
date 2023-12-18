/**
 * Simulator/mock for a light device
 * 
 */
package smart_room.distributed;

import sap.smartroom.*;
import sap.smartroom.LampDevice;

public class LampDeviceSimulator implements LampDevice {

	private LampSimFrame frame;
	private String lightID;
	
	public LampDeviceSimulator(String lightID){
		this.lightID = lightID;
	}
	
	public void init() {
		frame = new LampSimFrame(lightID);
		frame.display();
	}
	
	@Override
	public void on() {
		frame.setOn(true);	
	}

	@Override
	public void off() {
		frame.setOn(false);	
	}
}
