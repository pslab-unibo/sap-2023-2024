package mvc_01_basic;

import java.util.ArrayList;
import java.util.List;

public class MyModel implements ModelInterface, ModelObserverSource {

	private List<ModelObserver> observers;
	private int state;
	
	public MyModel(){
		state = 0;
		observers = new ArrayList<ModelObserver>();
	}
	
	public void update(){
		state++;
		log("state updated: " + state);
		notifyObservers();
	}
	
	public int getState(){
		return state;
	}
	
	public void addObserver(ModelObserver obs){
		observers.add(obs);
	}
	
	private void notifyObservers(){
		for (ModelObserver obs: observers){
			obs.notifyModelUpdated();
		}
	}
	
	private void log(String msg) {
		System.out.println("[Model] " + msg);
	}	
}
