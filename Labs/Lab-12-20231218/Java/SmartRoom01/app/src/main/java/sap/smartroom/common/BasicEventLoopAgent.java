package sap.smartroom.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sap.smartroom.Controller;
import sap.smartroom.Event;

public abstract class BasicEventLoopAgent extends Thread implements Controller {
	
	private String agentName;
	private BlockingQueue<Event> queue;
	
	public BasicEventLoopAgent(String name) {
		agentName = name;
		queue = new LinkedBlockingQueue<Event>();
	}
	
	public void run() {
		while (true) {
			try {
				Event ev = queue.take();
				processEvent(ev);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	protected abstract void processEvent(Event ev);
	
	public void notifyEvent(Event ev) {
		try {
			queue.put(ev);
		} catch (Exception ex) {
		}
	}
	
	protected void log(String msg) {
		System.out.println("[" + agentName +"] " + msg);
	}

}
