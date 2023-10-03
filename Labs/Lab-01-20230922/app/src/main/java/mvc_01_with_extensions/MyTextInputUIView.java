package mvc_01_with_extensions;

import mvc_01_basic.*;
import java.io.BufferedReader;
import java.util.*;
import java.io.*;

class MyTextInputUIView implements UserInputSource {

	private final List<UserInputObserver> observers;
	
	public MyTextInputUIView() {
		observers = new ArrayList<>();
	}

	@Override
	public void addObserver(UserInputObserver obs){
		observers.add(obs);
	}

	public void startGettingInput(){
		new Thread(() -> {
			BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
			while (true){
				try {
					String cmd = rd.readLine();
					if (cmd.equals("update")){
						this.notifyNewUpdateRequested();
					}
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}
		}).start();
	}



	private void notifyNewUpdateRequested(){
		for (var obs: observers){
			obs.notifyNewUpdateRequested();
		}
	}
}
