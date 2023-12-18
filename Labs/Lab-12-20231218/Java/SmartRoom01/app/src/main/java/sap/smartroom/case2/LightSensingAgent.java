package sap.smartroom.case2;

import io.vertx.core.json.JsonObject;
import sap.smartroom.Event;
import sap.smartroom.LightSensorDevice;
import sap.smartroom.common.BasicEventLoopAgent;
import smart_room.distributed.*;

public class LightSensingAgent extends BasicEventLoopAgent {
	
	private CommChannel channel;
	public static final String LuminositySensingAgentChannelName = "luminosity-sensing";
	private  String lightControllerChannelName;

	public LightSensingAgent(LightSensorDevice lsd,  String lightControllerChannelName) throws Exception {
		super("luminosity-sensing-agent");
		this.lightControllerChannelName = lightControllerChannelName;
		lsd.register(this);
		channel = new CommChannel(LuminositySensingAgentChannelName);
		log("init ok.");

	}

	protected void processEvent(Event ev) {
		if (ev instanceof LightLevelChanged) {
			log("light level changed");
			JsonObject msg = new JsonObject();
			msg.put("event", "light-level-changed");
			msg.put("newLevel", ((LightLevelChanged) ev).getNewLevel());
			channel.sendMsg(lightControllerChannelName, msg);
		} 
	}
	
}
