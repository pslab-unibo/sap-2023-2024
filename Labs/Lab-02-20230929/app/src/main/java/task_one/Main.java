package task_one;


import task_one.controller.MyController;
import task_one.controller.MyInputUI;
import task_one.model.MyModel;
import task_one.view.MyView;
import task_one.view.MyWebSocketView;

public class Main {
	private static final int DEFAULT_PORT = 8080;
	public static void main(String[] args) {
		MyModel model = new MyModel();
		MyView view = new MyView(model);
		MyInputUI inputUI = new MyInputUI();
		MyController controller = new MyController(model);

		inputUI.addObserver(controller);
		view.display();
		inputUI.display();

		// Start WebSocket Server
		MyWebSocketView webSocketView = new MyWebSocketView(model,DEFAULT_PORT);
		webSocketView.start();
	}
}