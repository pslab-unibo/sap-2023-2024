package sap.escooters.business_logic_layer;

import io.vertx.core.json.JsonObject;

public interface DataSourcePort {

	void saveUser(JsonObject user) throws DataSourceException;
	void saveEScooter(JsonObject scooter) throws DataSourceException;
	void saveRide(JsonObject ride) throws DataSourceException;
}
