package sap.smartroom_agent_refined;

import io.vertx.core.json.JsonObject;
import sap.common.agent.ReactiveAgent;
import sap.common.agent.Thing;

public class SmartRoomAgent extends ReactiveAgent {
	
	private Thing lightSensorThing;
	private Thing presDetectThing;
	private Thing lampThing;


	private enum StateType { LIGHT_OFF, LIGHT_TURNING_ON, LIGHT_ON, LIGHT_TURNING_OFF, LIGHT_GOING_OFF }
	private StateType currentState;

	private double threshold;
	private Timer timer;

	private double currentLuminosity;
	private boolean presenceDetected;
	private String currentLampState;
	
	public SmartRoomAgent(
			Thing lampThing,
			Thing lightSensorThing,  
			Thing presDetectThing,  double threshold) throws Exception {

		super("SmartRoomAgent");
		this.threshold = threshold;
		this.lightSensorThing = lightSensorThing;
		this.lampThing = lampThing;
		this.presDetectThing = presDetectThing;
	}

	public void init()  {

		startObserving(lampThing);
		startObserving(lightSensorThing);
		startObserving(presDetectThing);
		
		currentState = StateType.LIGHT_OFF;
		
		currentLuminosity = 0;
		presenceDetected = false;
		currentLampState = "off";
		
		log("init ok.");
}

	protected void processEvent(JsonObject ev) {
		
		/* first check for updates about the environments */ 
			
		updateLocalBeliefs(ev);
		
		/* then decide what to do, depending on the current state */
		
		switch (currentState) {
		case LIGHT_OFF:
			if (currentLampState.equals("on")) {
				currentState = StateType.LIGHT_ON;
			} else if (presenceDetected && currentLuminosity < threshold) {
				log("turning on...");
				this.invokeActionOn(lampThing, "on");
				currentState = StateType.LIGHT_TURNING_ON;
			}
			break;
		case LIGHT_TURNING_ON:
			if (currentLampState.equals("on")) {
				currentState = StateType.LIGHT_ON;
			}
		case LIGHT_ON:
			if (currentLampState.equals("off")) {
				currentState = StateType.LIGHT_OFF;
			} else if (!presenceDetected) {
				log("presence no more detected... wait for 5 secs");
				timer = new Timer(this.getVertx());
				timer.init(5000, new JsonObject().put("event", "no-detection-in-5-secs"));
				currentState = StateType.LIGHT_GOING_OFF;
			} 
			break;
		case LIGHT_GOING_OFF:	
			String evType = ev.getString("event");
			if (evType.equals("no-detection-in-5-secs")) {
				log("going off");
				this.invokeActionOn(lampThing, "off");
				currentState = StateType.LIGHT_TURNING_OFF;
			} else if (presenceDetected && currentLuminosity < threshold) {
				log("presence detected while waiting..going on");
				timer.cancel();
				currentState = StateType.LIGHT_ON;
			} 
			break;
		case LIGHT_TURNING_OFF:
			if (currentLampState.equals("off")) {
				currentState = StateType.LIGHT_OFF;
			}
		}
	}

	protected void updateLocalBeliefs(JsonObject ev) {
		String evType = ev.getString("event");

		if (evType.equals("presenceDetected")) {
			log("presence detected");
			presenceDetected = true;
		} else if (evType.equals("presenceNoMoreDetected")) {
			log("presence no more detected");
			presenceDetected = false;
		} else if (evType.equals("lightLevelChanged")) {
			log("light level changed");
			currentLuminosity = ev.getJsonObject("data").getDouble("lightLevel");
		} else if (evType.equals("stateChanged")) {
			currentLampState = ev.getJsonObject("data").getString("state");			
			log("lamp state changed: " + currentLampState);
		}
	}
	
	protected void log(String msg) {
		System.out.println("[SmartRoomAgent]["+System.currentTimeMillis()+"] " + msg);
	}
		
}
