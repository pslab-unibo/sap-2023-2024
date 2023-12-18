package acme;

import cartago.*;

public class AirConditioner extends Artifact {

	private enum AirConditionerState {idle, heating, cooling}
	private AirConditionerState airConditionerState;
		
	void init(){
		defineObsProperty("state","idle");		
		airConditionerState = AirConditionerState.idle;
		log("ready.");
	}
	
	@OPERATION void startHeating(){
		airConditionerState = AirConditionerState.heating;
		getObsProperty("state").updateValue("heating");
		this.execInternalOp("heatingProc");
	}
	
	@OPERATION void stopWorking(){
		airConditionerState = AirConditionerState.idle;
		getObsProperty("state").updateValue("idle");
	}

	@OPERATION void startCooling(){
		airConditionerState = AirConditionerState.cooling;
		getObsProperty("state").updateValue("cooling");
		this.execInternalOp("coolingProc");
	}

	@INTERNAL_OPERATION void heatingProc(){
		while (airConditionerState.equals(AirConditionerState.heating)){
			log("heating..");
			changeTemperature(0.5);
			this.await_time(500);
		}
	}
	
	@INTERNAL_OPERATION void coolingProc(){
		while (airConditionerState.equals(AirConditionerState.cooling)){
			log("cooling...");
			changeTemperature(-0.5);
			this.await_time(500);
		}
	}
	
	private void changeTemperature(double delta){
		try {
			ArtifactId gui = this.lookupArtifact("temp_sensor_gui");
			execLinkedOp(gui, "updateTemperature", delta);
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
	}
	
}
