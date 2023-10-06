package task_one.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyModel implements ModelObserverSource {

	private final List<ModelObserver> observers;
	private final AtomicInteger state;

	public MyModel() {
		state = new AtomicInteger(0);
		observers = new ArrayList<>();
	}

	public void update() {
		state.incrementAndGet();
		Logger.getLogger(MyModel.class.getName()).log(Level.INFO, "Model updated");
		notifyObservers();
	}

	public int getState() {
		return state.get();
	}

	public void addObserver(ModelObserver obs) {
		observers.add(obs);
	}

	private void notifyObservers() {
		for (ModelObserver obs : observers) {
			obs.notifyModelUpdated();
		}
	}
}
