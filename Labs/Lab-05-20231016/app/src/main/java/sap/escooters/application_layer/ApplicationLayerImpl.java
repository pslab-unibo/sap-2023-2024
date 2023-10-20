package sap.escooters.application_layer;

import java.util.Optional;

import io.vertx.core.json.JsonObject;
import sap.escooters.business_logic_layer.*;

public class ApplicationLayerImpl implements ApplicationAPI {

	private DomainModel domainLayer;
	private RideDashboardPort rideDashboardPort;
	
	public void init(DomainModel layer, RideDashboardPort adapter) {
		this.domainLayer = layer;
		this.rideDashboardPort = adapter;
	}

	@Override
	public void registerNewUser(String id, String name, String surname) throws UserIdAlreadyExistingException {
		Optional<User> user = domainLayer.getUser(id);
		if (user.isEmpty()) {
			domainLayer.addNewUser(id, name, surname);
		} else {
			throw new UserIdAlreadyExistingException();
		}
	}

	@Override
	public JsonObject getUserInfo(String id) throws UserNotFoundException  {
		Optional<User> user = domainLayer.getUser(id);
		if (user.isPresent()) {
			return user.get().toJson();
		} else {
			throw new UserNotFoundException();
		}
	}

	
	@Override
	public void registerNewEScooter(String id) throws UserIdAlreadyExistingException {
		domainLayer.addNewEScooter(id);
	}

	@Override
	public JsonObject getEScooterInfo(String id) throws EScooterNotFoundException  {
		Optional<EScooter> escooter = domainLayer.getEScooter(id);
		if (escooter.isPresent()) {
			return escooter.get().toJson();
		} else {
			throw new EScooterNotFoundException();
		}
	}

	@Override
	public String startNewRide(String userId, String escooterId) throws RideNotPossibleException {
		Optional<User> user = domainLayer.getUser(userId);
		Optional<EScooter> escooter = domainLayer.getEScooter(escooterId); 
		if (user.isPresent() && escooter.isPresent()) {
			EScooter sc = escooter.get();
			if (sc.isAvailable()) {
				String id = domainLayer.startNewRide(user.get(), escooter.get());
				this.rideDashboardPort.notifyNumOngoingRidesChanged(domainLayer.getNumOnoingRides());
				return id;
			} else {
				throw new RideNotPossibleException();
			}
		} else {
			throw new RideNotPossibleException();
		}
	}
	
	@Override
	public JsonObject getRideInfo(String id) throws RideNotFoundException  {
		Optional<Ride> ride = domainLayer.getRide(id);
		if (ride.isPresent()) {
			return ride.get().toJson();
		} else {
			throw new RideNotFoundException();
		}
	}

	@Override
	public void endRide(String rideId) throws RideNotFoundException, RideAlreadyEndedException {
		Optional<Ride> ride = domainLayer.getRide(rideId);
		if (ride.isPresent()) {
			Ride ri = ride.get();
			if (ri.isOngoing()) {
				ri.end();
				this.rideDashboardPort.notifyNumOngoingRidesChanged(domainLayer.getNumOnoingRides());
			} else {
				throw new RideAlreadyEndedException();
			}
		} else {
			throw new RideNotFoundException();
		}
	}
}
