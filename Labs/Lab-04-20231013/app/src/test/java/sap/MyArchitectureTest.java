package sap;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class MyArchitectureTest {
    @Test
    public void some_architecture_rule() {
    	JavaClasses importedClasses = new ClassFileImporter().importPackages("sap.escooters");

    	ArchRule ruleLayered = layeredArchitecture()
        .consideringAllDependencies()
        .layer("DataSource").definedBy("..data_source_layer..")
        .layer("Domain").definedBy("..domain_layer..")
        .layer("Service").definedBy("..service_layer..")
        .layer("Presentation").definedBy("..presentation_layer..")
        .layer("Main").definedBy("..launcher..")

        .whereLayer("Presentation").mayOnlyBeAccessedByLayers("Main")
        .whereLayer("Service").mayOnlyBeAccessedByLayers("Presentation","Main")
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Service","Main")
        .whereLayer("DataSource").mayOnlyBeAccessedByLayers("Domain","Main");
        
        ruleLayered.check(importedClasses);    
    }
}
