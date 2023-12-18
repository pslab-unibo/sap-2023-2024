/*
 * THERMOSTAT AGENT 
 *
 * achieve a target temperature, specified in the goal.
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
		println("room ready.");
		// test
		.wait(2000);
		!temperature(21).
		
+!temperature(T): temperature_in_range(T)
	<- 	println("temperature achieved.").
		
+!temperature(T): now_is_colder_than(T)
	<-  println("It is too cold -> heating...");
	    startHeating;
		!heat_until(T).

+!heat_until(T): temperature_in_range(T)
 	<-  println("temperature achieved.");
	    stopWorking.
	
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
 	<- println("temperature achieved.");
	   stopWorking.

+!cool_until(T): now_is_warmer_than(T) 
	<-  .wait(100);
	    !cool_until(T).

+!cool_until(T): now_is_colder_than(T) 
	<-  .wait(100);
	    !temperature(T).

		
		

