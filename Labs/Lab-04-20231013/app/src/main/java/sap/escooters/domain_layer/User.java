package sap.escooters.domain_layer;

import io.vertx.core.json.JsonObject;

public class User {

	private String id;
	private String name;
	private String surname;
	
	
	public User(String id, String name, String surname) {
		this.id = id;
		this.name = name;
		this.surname = surname;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void save() {
		try {
			DomainLayerImpl.getDataSourceLayer().saveUser(toJson());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public JsonObject toJson() {
		JsonObject userObj = new JsonObject();
		userObj.put("id", id);
		userObj.put("name", name);
		userObj.put("surname", surname);			
		return userObj;
	}
	
	
}
