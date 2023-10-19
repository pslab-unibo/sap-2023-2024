package assigment03

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.Test


class MyArchitectureTest {

    @Test
    fun `some architecture rule`() {
        val importedClasses = importClassesFrom("sap.escooters")
        defineArchitectureRules().check(importedClasses)
    }

    private fun importClassesFrom(packageName: String): JavaClasses =
        ClassFileImporter().importPackages(packageName)

    private fun defineArchitectureRules(): ArchRule =
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("DataSource").definedBy("..data_source_layer..")
            .layer("Domain").definedBy("..domain_layer..")
            .layer("Service").definedBy("..service_layer..")
            .layer("Presentation").definedBy("..presentation_layer..")
            .layer("Main").definedBy("..launcher..")
            .whereLayer("Presentation").mayOnlyBeAccessedByLayers("Main")
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Presentation", "Main")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Service", "Main")
            .whereLayer("DataSource").mayOnlyBeAccessedByLayers("Domain", "Main")
}