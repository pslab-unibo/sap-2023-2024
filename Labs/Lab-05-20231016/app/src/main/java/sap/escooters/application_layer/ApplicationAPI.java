package sap.escooters.application_layer;

import io.vertx.core.json.JsonObject;

public interface ApplicationAPI  {
	
	/* about users */
	void registerNewUser(String id, String name, String surname) throws UserIdAlreadyExistingException;
	JsonObject getUserInfo(String id) throws UserNotFoundException;

	/* about escooters */
	void registerNewEScooter(String id) throws UserIdAlreadyExistingException;
	JsonObject getEScooterInfo(String id) throws EScooterNotFoundException;
	
	/* about rides */
	String startNewRide(String userId, String escooterId) throws RideNotPossibleException;
	JsonObject getRideInfo(String id) throws RideNotFoundException;
	void endRide(String rideId) throws RideNotFoundException, RideAlreadyEndedException;
}
