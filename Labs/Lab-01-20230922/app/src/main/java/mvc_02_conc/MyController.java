package mvc_02_conc;


public class MyController implements UserInputObserver {
	
	private MyModel model;

	public MyController(MyModel model){
		this.model = model;
	}
	
	public void notifyNewUpdateRequested() {
		log("New update requested by the user");
		model.update();
	}

	private void log(String msg) {
		System.out.println("[Controller] " + msg);
	}
}
