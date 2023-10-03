package task_one;

public class Main {
  static public void main(String[] args) {
	  MyModel model = new MyModel();

	  MyView view = new MyView(model);

	  MyInputUI inputUI = new MyInputUI();

	  MyController controller = new MyController(model);

	  inputUI.addObserver(controller);
	  view.display();
	  inputUI.display();

  }	
  
}
