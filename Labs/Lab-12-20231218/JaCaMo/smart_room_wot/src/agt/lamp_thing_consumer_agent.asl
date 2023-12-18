lamp_host("localhost").
lamp_port(8080).

!start.

/* Plans */

+!start 
    <- !createLampArtifact;
       println("Going to interact with the lamp") ;
       !interact.

+!createLampArtifact   
    <-  ?lamp_host(H);
        ?lamp_port(P);
        makeArtifact(lamp, "acme.LampThingProxyArtifact", [H,P], L);
        focus(L);
        println("artifact created").

+state(S)
    <- println("new perceived lamp state: ", S).
    
+!interact
    <- on.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
