package mvc_02_conc;

import java.util.ArrayList;
import java.util.List;

public class MyModel implements ModelInterface, ModelObserverSource {

	private List<ModelObserver> observers;
	private int state;
	
	public MyModel(){
		state = 0;
		observers = new ArrayList<ModelObserver>();
	}
	
	public synchronized void update(){
		state++;
		log("state updated: " + state);
		notifyObservers();
	}
	
	public synchronized int getState(){
		return state;
	}
	
	public synchronized void addObserver(ModelObserver obs){
		observers.add(obs);
	}
	
	private synchronized void notifyObservers(){
		for (ModelObserver obs: observers){
			obs.notifyModelUpdated();
		}
	}
	
	private synchronized void log(String msg) {
		System.out.println("[Model] " + msg);
	}	
}
