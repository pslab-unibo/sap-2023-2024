package sap.escooters.domain_layer;

import java.util.Optional;

import io.vertx.core.json.JsonObject;

public class EScooter  {

	private String id;
	public enum EScooterState { AVAILABLE, IN_USE, MAINTENANCE}	
	private EScooterState state;
	private Optional<Location> loc;
	
	public EScooter(String id) {
		this.id = id;
		this.state = EScooterState.AVAILABLE;
		this.loc = Optional.empty();
	}
	
	public String getId() {
		return id;
	}

	public EScooterState getState() {
		return state;
	}
	
	public boolean isAvailable() {
		return state.equals(EScooterState.AVAILABLE);
	}

	public void updateState(EScooterState state) {
		this.state = state;
		save();
	}
	
	public void updateLocation(Location newLoc) {
		loc = Optional.of(newLoc);
		save();
	}
	
	public Optional<Location> getCurrentLocation(){
		return loc;
	}
	
	public void save() {
		try {
			DomainLayerImpl.getDataSourceLayer().saveEScooter(toJson());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public JsonObject toJson() {
		JsonObject scooterObj = new JsonObject();
		scooterObj.put("id", this.getId());
		scooterObj.put("state", this.getState().toString());
		Optional<Location> loc = this.getCurrentLocation();
		if (loc.isPresent()) {
			JsonObject locObj = new JsonObject();
			locObj.put("latitude", this.getCurrentLocation().get().getLatitude());
			locObj.put("longitude", this.getCurrentLocation().get().getLongitude());
			scooterObj.put("location", locObj);			
		} else {
			scooterObj.putNull("location");			
		}			
		return scooterObj;
	}

	
}
