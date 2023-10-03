package mvc_02_dist_rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import mvc_01_basic.*;

class MyRemoteViewProxy implements ModelObserver {

	private final ModelObserverSource model;
	private RemoteModelObserver remoteView;
	
	public MyRemoteViewProxy(ModelObserverSource model) {		
		this.model = model;		
	    model.addObserver(this);	    
	    try {
	        Registry registry = LocateRegistry.getRegistry(null);
	        remoteView = (RemoteModelObserver) registry.lookup("remote-view");
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	}

	public void notifyModelUpdated() {
		try {
			remoteView.notifyModelUpdated(model.getState());
	    } catch (Exception e) {
	    	System.err.println("Client exception: " + e.toString());
	        e.printStackTrace();
	    }	
	}
}
