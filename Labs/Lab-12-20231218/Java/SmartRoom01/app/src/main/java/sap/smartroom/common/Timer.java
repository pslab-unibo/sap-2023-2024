package sap.smartroom.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import sap.smartroom.AbstractEventSource;

public class Timer extends AbstractEventSource {
	
	private ScheduledExecutorService sched;
	private ScheduledFuture handler;
	
	public Timer() {
		 sched = Executors.newScheduledThreadPool(1);
	}
	
	public synchronized void init(int nMilliseconds) {
		if (handler != null) {
			handler.cancel(true);
		}
		
		handler = sched.schedule(() -> {
			this.notifyEvent(new TimerEvent(System.currentTimeMillis()));
		}, nMilliseconds, TimeUnit.MILLISECONDS);
	}
	
	public synchronized void cancel() {
		if (handler != null) {
			handler.cancel(true);
		}
	}

}
