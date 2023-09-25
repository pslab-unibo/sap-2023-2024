package mvc_02_dist_mom;

import com.rabbitmq.client.*;

public class MyRemoteViewMain {

	  private final static String QUEUE_NAME = "mvc";

	  public static void main(String[] argv) throws Exception {
	    
		MyRemoteView view = new MyRemoteView();
		view.display();

		  
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    
	    Channel channel = connection.createChannel();

	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);

	    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	        String message = new String(delivery.getBody(), "UTF-8");
	        view.notifyModelUpdated(Integer.parseInt(message));
	    };
	    String consumerTag = channel.basicConsume(QUEUE_NAME, true, deliverCallback, /* cancellation callback */ consTag -> { });
	    
	    System.out.println("Remote Viewer installed. ");
	  }
}
