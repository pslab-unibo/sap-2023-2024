package mvc_01_with_extensions;

import mvc_01_basic.*;

public class AppMain {
  static public void main(String[] args) throws Exception {
	  
	MyModel model = new MyModel();
    MyView view = new MyView(model);
    MyInputUI inputUI = new MyInputUI();
	MyController controller = new MyController(model);
	inputUI.addObserver(controller);
	view.display();
	inputUI.display();

	new MyTextView(model);
	MyTextInputUIView input2 = new MyTextInputUIView();
	input2.addObserver(controller);
	input2.startGettingInput();
  }	
  
}
