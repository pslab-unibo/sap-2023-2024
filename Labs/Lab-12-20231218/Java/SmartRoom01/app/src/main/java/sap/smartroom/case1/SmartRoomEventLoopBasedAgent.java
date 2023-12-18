package sap.smartroom.case1;

import sap.smartroom.Event;
import sap.smartroom.common.BasicEventLoopAgent;
import sap.smartroom.common.Timer;
import sap.smartroom.common.TimerEvent;
import smart_room.centralized.*;

public class SmartRoomEventLoopBasedAgent extends BasicEventLoopAgent {
	
	private SinglelBoardSimulator board;
	
	private enum StateType { LIGHT_OFF, LIGHT_ON, LIGHT_GOING_OFF }
	private StateType currentState;

	private double threshold;
	private Timer timer;
	
	public SmartRoomEventLoopBasedAgent(
				String id, 
				SinglelBoardSimulator board,
				double threshold) {
		super(id);
		this.board = board;
		board.register(this);
		this.threshold = threshold;
		timer = new Timer();
		timer.register(this);
		
		currentState = StateType.LIGHT_OFF;
		board.off();
	}

	protected void processEvent(Event ev) {
		switch (currentState) {
			case LIGHT_OFF:
				if (ev instanceof PresenceDetected) {
					log("presence detected");
					double currentLight = board.getLuminosity();
					if (currentLight < threshold) {
						log("turn on");
						board.on();
						currentState = StateType.LIGHT_ON;
					}
				} else if (ev instanceof LightLevelChanged) {
					log("light level changed");
					LightLevelChanged lightEv = (LightLevelChanged) ev;
					if (lightEv.getNewLevel() < threshold && board.presenceDetected()) {
						log("turn on");
						board.on();
						currentState = StateType.LIGHT_ON;
					}
				}
				break;
			case LIGHT_ON:
				if (ev instanceof PresenceNoMoreDetected) {
					log("presence no more detected... wait for 5 secs");
					timer.init(5000);
					currentState = StateType.LIGHT_GOING_OFF;
				} 
				break;
			case LIGHT_GOING_OFF:	
				if (ev instanceof TimerEvent) {
					log("going off");
					board.off();
					currentState = StateType.LIGHT_OFF;
				} else if (ev instanceof PresenceDetected) {
					log("presence detected while waiting..going on");
					timer.cancel();
					currentState = StateType.LIGHT_ON;
				}
				break;
		}
	}

}
