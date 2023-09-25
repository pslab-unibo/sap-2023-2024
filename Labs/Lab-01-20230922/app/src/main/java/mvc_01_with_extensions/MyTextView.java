package mvc_01_with_extensions;

import mvc_01_basic.*;

class MyTextView implements ModelObserver {

	private ModelObserverSource model;
	
	public MyTextView(ModelObserverSource model) {		
		this.model = model;		
	    model.addObserver(this);	    
	}

	public void notifyModelUpdated() {
		System.out.println("State: " + model.getState());
	}
		
	
}
