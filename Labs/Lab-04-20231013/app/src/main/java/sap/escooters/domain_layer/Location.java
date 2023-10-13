package sap.escooters.domain_layer;

public class Location {
	private double latitude, longitude;
	
	public Location(double lat, double lon) {
		this.latitude = lat;
		this.longitude = lon;
	}
	
	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
