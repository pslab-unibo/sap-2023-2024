package sap.common;

public abstract class AbstractEventSource implements EventSource {

	private Controller controller;
	
	public void register(Controller c) {
		controller = c;
	}
	
	protected void notifyEvent(Event ev) {
		if (controller != null) {
			controller.notifyEvent(ev);
		}
	}
}
