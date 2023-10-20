package sap.escooters.business_logic_layer;

import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import sap.escooters.business_logic_layer.EScooter.EScooterState;

public class DomainModelImpl implements DomainModel {

	static private DataSourcePort dataSourcePort;
	
	private HashMap<String, User> users;
	private HashMap<String, EScooter> escooters;
	private HashMap<String, Ride> rides;
	private int rideCounter;
    static Logger logger = Logger.getLogger("[DomainModel]");	
	
	public DomainModelImpl() {
		users = new HashMap<String, User>();
		escooters = new HashMap<String, EScooter>();
		rides = new HashMap<String, Ride>();
	}
	
	public void init(DataSourcePort port) {
		this.dataSourcePort = port;
		rideCounter = 0;
	}
	
	public static DataSourcePort getDataSourcePort() {
		return dataSourcePort;
	}
	
	@Override
	public void addNewUser(String id, String name, String surname) {
		User user = new User(id, name, surname);
		users.put(id, user);				
		user.save();
		logger.log(Level.INFO, "New user registered: " + id);
	}

	@Override
	public void addNewEScooter(String id) {
		EScooter escooter = new EScooter(id);
		escooters.put(id, escooter);				
		escooter.save();
		logger.log(Level.INFO, "New escooter registered: " + id);
	}
	
	@Override
	public String startNewRide(User user, EScooter escooter) {
		escooter.updateState(EScooterState.IN_USE);
		rideCounter++;
		String rideId = "ride-" + rideCounter;		
		Ride ride = new Ride(rideId, user, escooter);
		rides.put(rideId, ride);				
		escooter.save();
		ride.save();		
		logger.log(Level.INFO, "Started ride: " + rideId);
		return rideId;
	}

	@Override
	public Optional<User> getUser(String userId) {
		return Optional.ofNullable(users.get(userId));
	}

	@Override
	public Optional<EScooter> getEScooter(String id) {
		return Optional.ofNullable(escooters.get(id));
	}

	@Override
	public Optional<Ride> getRide(String id) {
		return Optional.ofNullable(rides.get(id));
	}

	@Override
	public int getNumOnoingRides() {
		return rideCounter;
	}
}
