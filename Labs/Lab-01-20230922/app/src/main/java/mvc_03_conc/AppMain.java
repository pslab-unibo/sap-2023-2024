package mvc_03_conc;

public class AppMain {
  static public void main(String[] args) throws Exception {
	  
	MyModel model = new MyModel();
    MyView view = new MyView(model);
    MyInputUI inputUI = new MyInputUI();
	MyController controller = new MyController(model);
	inputUI.addObserver(controller);
	MyAutonomousController autController = new MyAutonomousController(model);
	autController.start();
	view.display();
	inputUI.display();
  }
  
}
