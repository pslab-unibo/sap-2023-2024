package sap.escooters.data_source_layer;

import java.io.*;
import java.util.Optional;
import io.vertx.core.json.JsonObject;
import sap.layers.Layer;

public class DataSourceLayerImpl implements DataSourceLayer {

	private String USERS_PATH = "users";
	private String ESCOOTERS_PATH = "escooters";
	private String RIDES_PATH = "rides";
	
	private String dbaseFolder;
	
	public DataSourceLayerImpl(String dbaseFolder) {
		this.dbaseFolder =  dbaseFolder;
	}

	@Override
	public void init(Optional<Layer> layer) {
		makeDir(dbaseFolder);
		makeDir(dbaseFolder + File.separator + USERS_PATH);
		makeDir(dbaseFolder + File.separator + ESCOOTERS_PATH);
		makeDir(dbaseFolder + File.separator + RIDES_PATH);
	}	

	@Override
	public void saveUser(JsonObject user) throws DataSourceException {
		this.saveObj(USERS_PATH, user.getString("id"), user);
	}

	@Override
	public void saveEScooter(JsonObject escooter) throws DataSourceException {
		this.saveObj(ESCOOTERS_PATH, escooter.getString("id"), escooter);
	}

	@Override
	public void saveRide(JsonObject ride) throws DataSourceException {
		this.saveObj(RIDES_PATH, ride.getString("id"), ride);
	}

	private void saveObj(String db, String id, JsonObject obj) throws DataSourceException {
		try {									
			FileWriter fw = new FileWriter(dbaseFolder + File.separator + db + File.separator + id + ".json");
			java.io.BufferedWriter wr = new BufferedWriter(fw);				
			wr.write(obj.encodePrettily());
			wr.flush();
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new DataSourceException(ex.getMessage());
		}
	}
	
	private void makeDir(String name) {
		try {
			File dir = new File(name);
			if (!dir.exists()) {
				dir.mkdir();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
}
