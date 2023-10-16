package assigment03.escooters.domain_layer

import io.vertx.core.json.JsonObject
import java.util.*

class Ride(val id: String, val user: User, val scooter: EScooter) {
    private val startedDate = Date()
    private var endDate: Date? = null
    var ongoing = true
        private set

    fun end() {
        endDate = Date()
        ongoing = false
        save()
    }

    fun save() {
        try {
            DomainLayerImpl.getDataSourceLayer()?.saveRide(toJson())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun toJson() = JsonObject().apply {
        put("id", id)
        put("userId", user.id)
        put("escooterId", scooter.id)
        put("startDate", startedDate.toString())
        endDate?.let {
            put("endDate", it.toString())
        } ?: putNull("location")
    }
}





