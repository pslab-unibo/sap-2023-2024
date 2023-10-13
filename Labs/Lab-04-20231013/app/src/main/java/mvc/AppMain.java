package mvc;

import mvc.model.*;
import mvc.view.*;
import mvc.controller.*;

public class AppMain {
  static public void main(String[] args) throws Exception {
	  
	MyModel model = new MyModel();
    MyView view = new MyView(model);
    MyInputUI inputUI = new MyInputUI();
	MyController controller = new MyController(model);
	inputUI.addObserver(controller);
	view.display();
	inputUI.display();
  }	
  
}
