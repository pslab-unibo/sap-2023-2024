package mvc_03_conc;


public class MyAutonomousController extends Thread {
	
	private ModelInterface model;

	public MyAutonomousController(ModelInterface model){
		this.model = model;
	}
	
	public void run() {
		while (true) {
			log("Changing pro-actively the model...");
			model.update();
			try {
				Thread.sleep(2000);
			} catch (Exception ex) {}
		}
	}

	private void log(String msg) {
		System.out.println("[Autonomous Controller] " + msg);
	}
}
