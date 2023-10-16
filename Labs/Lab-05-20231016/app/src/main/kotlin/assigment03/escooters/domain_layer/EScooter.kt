package assigment03.escooters.domain_layer

import io.vertx.core.json.JsonObject

class EScooter(val id: String) {
    enum class EScooterState { AVAILABLE, IN_USE, MAINTENANCE }
    var state = EScooterState.AVAILABLE
        private set
    private var loc: Location? = null

    fun isAvailable() = state == EScooterState.AVAILABLE
    fun updateState(state: EScooterState) {
        this.state = state
        save()
    }

    fun updateLocation(newLoc: Location) {
        loc = newLoc
        save()
    }

    fun getCurrentLocation() = loc
    fun save() {
        try {
            DomainLayerImpl.getDataSourceLayer()?.saveEScooter(toJson())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun toJson() = JsonObject().apply {
        put("id", id)
        put("state", state.toString())
        getCurrentLocation()?.let {
            put("location", JsonObject().put("latitude", it.latitude).put("longitude", it.longitude))
        } ?: putNull("location")
    }
}