package mvc_02_dist_mom;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import mvc_01_basic.*;

class MyRemoteViewStub implements ModelObserver {

	private final static String EXCHANGE_NAME = "mvc";
	private Channel channel;

	private final ModelObserverSource model;
	
	public MyRemoteViewStub(ModelObserverSource model) {		
		this.model = model;		
	    model.addObserver(this);	    
	    try {
			ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost("localhost");
			Connection connection = factory.newConnection();
		    channel = connection.createChannel();
		    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		    System.out.println("Remote View Stub installed.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void notifyModelUpdated() {
		try {		    
		    String message = "" + model.getState();	  
	        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
		
	
}
