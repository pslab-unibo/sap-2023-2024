package sap.smartroom_agent;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Agent implementing the smart room behaviour.
 * 
 * @author aricci
 *
 */
public class SmartRoomAgent extends AbstractVerticle {
	
	private LightSensorThingAPI lightSensorThing;
	private PresDetectThingAPI presDetectThing;
	private LampThingAPI lampThing;


	private enum StateType { LIGHT_OFF, LIGHT_TURNING_ON, LIGHT_ON, LIGHT_TURNING_OFF, LIGHT_GOING_OFF }
	private StateType currentState;

	private double threshold;
	private Timer timer;

	private double currentLuminosity;
	private boolean presenceDetected;
	private String currentLampState;
	
	public SmartRoomAgent(
			LampThingAPI lampThing,
			LightSensorThingAPI lightSensorThing,  
			PresDetectThingAPI presDetectThing,  double threshold) throws Exception {

		this.threshold = threshold;
		this.lightSensorThing = lightSensorThing;
		this.lampThing = lampThing;
		this.presDetectThing = presDetectThing;
	}

	/**
	 * Main agent body.
	 */
	public void start(Promise<Void> startPromise) throws Exception {
		this.lampThing.subscribe(event -> {
			processEvent(event);
		});

		this.presDetectThing.subscribe(event -> {
			processEvent(event);
		});
		
		this.lightSensorThing.subscribe(event -> {
			processEvent(event);
		});
		
		this.getVertx().eventBus().consumer("events", evt -> {
			processEvent((JsonObject) (evt.body()));
		});

		currentState = StateType.LIGHT_OFF;
		
		currentLuminosity = 0;
		presenceDetected = false;
		currentLampState = "off";
		
		log("init ok.");
}

	protected void processEvent(JsonObject ev) {
		
		/* first check for updates about the environments */ 
		
		this.updateLocalBeliefs(ev);
		
		/* then decide what to do, depending on the current state */
		
		switch (currentState) {
		case LIGHT_OFF:
			if (currentLampState.equals("on")) {
				currentState = StateType.LIGHT_ON;
			} else if (presenceDetected && currentLuminosity < threshold) {
				log("turning on...");
				this.lampThing.on();
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
				this.lampThing.off();
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

	protected long setTimer(long ms, JsonObject ev) {
		return vertx.setTimer(ms, id -> processEvent(ev));
	}
	
	protected void log(String msg) {
		System.out.println("[SmartRoomAgent]["+System.currentTimeMillis()+"] " + msg);
	}
		
}
