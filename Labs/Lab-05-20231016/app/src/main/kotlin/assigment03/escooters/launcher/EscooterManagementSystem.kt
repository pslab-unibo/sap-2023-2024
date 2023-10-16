package assigment03.escooters.launcher

import assigment03.escooters.data_source_layer.DataSourceLayerImpl
import assigment03.escooters.domain_layer.DomainLayerImpl
import assigment03.escooters.presentation_layer.PresentationLayerImpl
import assigment03.escooters.service_layer.ServiceLayerImpl

fun main() {
    val dataSourceLayer = DataSourceLayerImpl("dbase")
    val domainLayer = DomainLayerImpl()
    val serviceLayer = ServiceLayerImpl()
    val presentationLayer = PresentationLayerImpl(8081)

    dataSourceLayer.init(null)
    domainLayer.init(dataSourceLayer)
    serviceLayer.init(domainLayer)
    presentationLayer.init(serviceLayer)
}