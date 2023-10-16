package assigment03.escooters.presentation_layer

import assigment03.escooters.service_layer.ServiceLayer
import assigment03.layers.Layer
import io.vertx.core.Vertx

class PresentationLayerImpl(private val port: Int) : PresentationLayer {

    private lateinit var serviceLayer: ServiceLayer

    override fun init(layer: Layer?) {
        serviceLayer = layer as ServiceLayer
        val vertx = Vertx.vertx()
        val myVerticle = EScooterManServer(port, serviceLayer)
        vertx.deployVerticle(myVerticle)
    }
}