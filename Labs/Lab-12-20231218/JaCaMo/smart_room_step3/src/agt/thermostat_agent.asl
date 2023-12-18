/*
 * THERMOSTAT AGENT 
 *
 * - achieve and keep the temperature
 * - react to changes the target temperature
 *
 */
tolerance(0.4).

temperature_in_range(T)
	:- not now_is_colder_than(T) & not now_is_warmer_than(T).

now_is_colder_than(T)
	:- temperature(C) & tolerance(DT) & (T - C) > DT.

now_is_warmer_than(T)
	:- temperature(C) & tolerance(DT) & (C - T) > DT.
	

!manage_room.

+!manage_room
	<- 	println("Setting up the room...");
		makeArtifact("temp_sensor_gui","acme.TemperatureSensor",[],Id);
		focus(Id);
		makeArtifact("air_conditioner","acme.AirConditioner",[]);
		makeArtifact("control_panel","acme.UserControlPanel",[],Panel);
		focus(Panel);
		println("room ready.").
	
+!achieve_and_keep_temperature(T)
	<- 	!temperature(T);
	 	println("temperature achieved.");
		+target_temperature(T).

+!temperature(T): temperature_in_range(T).
		
+!temperature(T): now_is_colder_than(T)
	<-  startHeating;
		!heat_until(T).

+!heat_until(T): temperature_in_range(T)
 	<- stopWorking.

+!heat_until(T): now_is_colder_than(T)
	<-  .wait(100);
	    !heat_until(T).

+!heat_until(T): now_is_warmer_than(T)
	<-  .wait(100);
	    !temperature(T).

+!temperature(T): now_is_warmer_than(T) 
	<-  println("It is too hot -> cooling...");
	    startCooling;
		!cool_until(T).
		
+!cool_until(T): temperature_in_range(T)
 	<- stopWorking.

+!cool_until(T): now_is_warmer_than(T) 
	<-  .wait(100);
	    !cool_until(T).

+!cool_until(T): now_is_colder_than(T) 
	<-  .wait(100);
	    !temperature(T).
//

@change_temp_plan [atomic]
+temperature(T) : target_temperature(T2) & not temperature_in_range(T2) & not .intend(temperature(T2))
    <- 	println("keep the temperature!");
		!temperature(T2);
		println("temperature restored.").

@change_target_temp_plan [atomic]
+target_temperature(T)
	<- 	.drop_intention(temperature(_));
	    println("new target temperature to achieve");
		!temperature(T);
	    println("new target temperature achieved").
		
		
		

