package sap.escooters.domain_layer;

import java.util.Date;
import java.util.Optional;

import io.vertx.core.json.JsonObject;

public class Ride {

	private Date startedDate;
	private Optional<Date> endDate;
	private User user;
	private EScooter scooter;
	private boolean ongoing;
	private String id;
	
	public Ride(String id, User user, EScooter scooter) {
		this.id = id;
		this.startedDate = new Date();
		this.endDate = Optional.empty();
		this.user = user;
		this.scooter = scooter;
		ongoing = true;
	}
	
	public String getId() {
		return id;
	}
	
	public void end() {
		endDate = Optional.of(new Date());
		ongoing = false;
		save();
	}

	public Date getStartedDate() {
		return startedDate;
	}

	public boolean isOngoing() {
		return this.ongoing;
	}
	
	public Optional<Date> getEndDate() {
		return endDate;
	}

	public User getUser() {
		return user;
	}

	public EScooter getEScooter() {
		return scooter;
	}
	
	public void save() {
		try {
			DomainLayerImpl.getDataSourceLayer().saveRide(toJson());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public JsonObject toJson() {
		JsonObject rideObj = new JsonObject();
		rideObj.put("id", this.getId());
		rideObj.put("userId", this.getUser().getId());
		rideObj.put("escooterId", this.getEScooter().getId());
		rideObj.put("startDate", this.getStartedDate().toString());
		Optional<Date> endDate = this.getEndDate();
		
		if (endDate.isPresent()) {
			rideObj.put("endDate", endDate.get().toString());			
		} else {
			rideObj.putNull("location");			
		}			
		return rideObj;
	}

	
}
