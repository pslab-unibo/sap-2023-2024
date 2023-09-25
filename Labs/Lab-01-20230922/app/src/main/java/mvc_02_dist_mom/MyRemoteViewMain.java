package mvc_02_dist_mom;

import com.rabbitmq.client.*;

public class MyRemoteViewMain {

	private final static String EXCHANGE_NAME = "mvc";

	public static void main(String[] argv) throws Exception {

		MyRemoteView view = new MyRemoteView();
		view.display();

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();

		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
	    String queueName = channel.queueDeclare().getQueue();
	    channel.queueBind(queueName, EXCHANGE_NAME, "");


		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");
			view.notifyModelUpdated(Integer.parseInt(message));
		};
	    
		channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });	    
		System.out.println("Remote Viewer installed. ");
	}
}
