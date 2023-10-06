package task_one.controller;


import task_one.model.ModelInterface;
import java.util.logging.Logger;

public class MyController implements UserInputObserver {
	private static final Logger logger = Logger.getLogger(MyController.class.getName());
	private final ModelInterface model;
	public MyController(ModelInterface model){
		this.model = model;
	}

	@Override
	public void notifyNewUpdateRequested() {
		logger.info("New update requested by the user");
		model.update();
	}
}
