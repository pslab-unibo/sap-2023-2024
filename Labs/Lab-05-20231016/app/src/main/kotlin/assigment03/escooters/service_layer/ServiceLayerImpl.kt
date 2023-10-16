package assigment03.escooters.service_layer

import assigment03.escooters.domain_layer.DomainLayer
import assigment03.layers.Layer
import io.vertx.core.json.JsonObject

class ServiceLayerImpl : ServiceLayer {
    private lateinit var domainLayer: DomainLayer

    override fun init(layer: Layer?) {
        domainLayer = layer as DomainLayer
    }

    override fun registerNewUser(id: String, name: String, surname: String) {
        domainLayer.getUser(id)?.let { throw UserIdAlreadyExistingException() }
        domainLayer.addNewUser(id, name, surname)
    }

    override fun getUserInfo(id: String): JsonObject =
        domainLayer.getUser(id)?.toJson() ?: throw UserNotFoundException()

    override fun registerNewEScooter(id: String) {
        domainLayer.addNewEScooter(id)
    }

    override fun getEScooterInfo(id: String): JsonObject =
        domainLayer.getEScooter(id)?.toJson() ?: throw EScooterNotFoundException()

    override fun startNewRide(userId: String, escooterId: String): String {
        val user = domainLayer.getUser(userId)
        val escooter = domainLayer.getEScooter(escooterId)

        if (user != null && escooter != null && escooter.isAvailable()) {
            return domainLayer.startNewRide(user, escooter)
        }

        throw RideNotPossibleException()
    }

    override fun getRideInfo(id: String): JsonObject =
        domainLayer.getRide(id)?.toJson() ?: throw RideNotFoundException()

    override fun endRide(rideId: String) {
        domainLayer.getRide(rideId)?.let {
            if (it.ongoing) {
                it.end()
            } else {
                throw RideAlreadyEndedException()
            }
        } ?: throw RideNotFoundException()
    }
}