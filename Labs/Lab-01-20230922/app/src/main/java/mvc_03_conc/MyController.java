package mvc_03_conc;


public class MyController implements UserInputObserver {
	
	private final ModelInterface model;

	public MyController(ModelInterface model){
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
