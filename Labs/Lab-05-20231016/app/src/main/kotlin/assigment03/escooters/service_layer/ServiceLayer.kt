package assigment03.escooters.service_layer

import assigment03.layers.Layer
import io.vertx.core.json.JsonObject

interface ServiceLayer : Layer {
    fun registerNewUser(id: String, name: String, surname: String)
    fun getUserInfo(id: String): JsonObject
    fun registerNewEScooter(id: String)
    fun getEScooterInfo(id: String): JsonObject
    fun startNewRide(userId: String, escooterId: String): String
    fun getRideInfo(id: String): JsonObject
    fun endRide(rideId: String)
}