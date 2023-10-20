package sap.escooters.business_logic_layer;

import java.util.Optional;

public interface DomainModel {

	void addNewUser(String id, String name, String surname);
	Optional<User> getUser(String userId);

	void addNewEScooter(String id);
	Optional<EScooter> getEScooter(String id);
	
	String startNewRide(User user, EScooter escooter);
	Optional<Ride> getRide(String rideId);
	
	int getNumOnoingRides();
}
