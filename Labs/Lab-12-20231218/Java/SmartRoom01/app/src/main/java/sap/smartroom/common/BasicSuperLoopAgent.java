package sap.smartroom.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class BasicSuperLoopAgent {
	
	private String agentName;
	private long period;
	private ScheduledExecutorService sched;
	private ScheduledFuture handler;
	
	public BasicSuperLoopAgent(String name, long period2) {
		agentName = name;
		this.period = period2;
		sched = Executors.newScheduledThreadPool(1);
	}
	
	public void start() {
		setup();
		handler = sched.scheduleAtFixedRate(() -> {
			try {
				loop();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}, 0, period, TimeUnit.MILLISECONDS);
	}
	
	protected abstract void setup();
	protected abstract void loop();
	
	public void stop() {
		handler.cancel(true);
	}
	
	protected void log(String msg) {
		System.out.println("[" + agentName +"] " + msg);
	}

	protected long getTime() {
		return System.currentTimeMillis();
	}
}
