package sap.escooters.domain_layer;

import java.util.Optional;

import sap.layers.Layer;

public interface DomainLayer extends Layer {

	void addNewUser(String id, String name, String surname);
	Optional<User> getUser(String userId);

	void addNewEScooter(String id);
	Optional<EScooter> getEScooter(String id);
	
	String startNewRide(User user, EScooter escooter);
	Optional<Ride> getRide(String rideId);
}
