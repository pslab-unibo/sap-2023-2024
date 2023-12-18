package sap.smartroom.case2;

import io.vertx.core.json.JsonObject;
import sap.smartroom.Event;
import sap.smartroom.LampDevice;
import sap.smartroom.common.BasicEventLoopAgent;
import sap.smartroom.common.Timer;
import sap.smartroom.common.TimerEvent;
import smart_room.distributed.*;

public class LampControllerAgent extends BasicEventLoopAgent {
	
	public static final String lightControllerChannelName = "light-controller";
	
	private LampDevice ld;
	private CommChannel channel;

	private enum StateType { LIGHT_OFF, LIGHT_ON, LIGHT_GOING_OFF }
	private StateType currentState;

	private double threshold;
	private Timer timer;

	private double currentLuminosity;
	private boolean presenceDetected;
	
	public LampControllerAgent(
				LampDevice ld, double threshold) throws Exception {
		super("luminosity-sensing-agent");
		channel = new CommChannel(lightControllerChannelName);
		channel.register(this);

		this.threshold = threshold;
		timer = new Timer();
		timer.register(this);
		
		this.ld = ld;
		currentState = StateType.LIGHT_OFF;
		ld.off();
		
		currentLuminosity = 0;
		presenceDetected = false;
		
		log("init ok.");
	}

	
	protected void processEvent(Event ev) {
		
		/* first check for updates about the environments */ 
		 
		if (ev instanceof MsgEvent) {
			MsgEvent mev = (MsgEvent) ev;
			JsonObject msg = mev.getMsg();
			String evType = msg.getString("event");
			
			if (evType.equals("presence-detected")) {
				log("presence detected");
				presenceDetected = true;
			} else if (evType.equals("presence-no-more-detected")) {
				log("presence no more detected");
				presenceDetected = false;
			} else if (evType.equals("light-level-changed")) {
				log("light level changed");
				currentLuminosity = msg.getDouble("newLevel");
			}
		}
		
		/* then decide what to do, depending on the current state */
		
		switch (currentState) {
		case LIGHT_OFF:
			if (presenceDetected && currentLuminosity < threshold) {
					log("turn on");
					ld.on();
					currentState = StateType.LIGHT_ON;
			}
			break;
		case LIGHT_ON:
			if (!presenceDetected) {
				log("presence no more detected... wait for 5 secs");
				timer.init(5000);
				currentState = StateType.LIGHT_GOING_OFF;
			} 
			break;
		case LIGHT_GOING_OFF:	
			if (ev instanceof TimerEvent) {
				log("going off");
				ld.off();
				currentState = StateType.LIGHT_OFF;
			} else if (presenceDetected) {
				log("presence detected while waiting..going on");
				timer.cancel();
				currentState = StateType.LIGHT_ON;
			}
			break;
		}
	}
}
