package sap.smartroom.common;

public class BasicAgent extends Thread {
	
	private String agentName;
	
	public BasicAgent(String name) {
		agentName = name;
	}
	
	protected void log(String msg) {
		System.out.println("[" + agentName +"] " + msg);
	}

	protected long getTime() {
		return System.currentTimeMillis();
	}
	
}
