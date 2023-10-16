package assigment03.escooters.domain_layer

import assigment03.escooters.data_source_layer.DataSourceLayer
import assigment03.layers.Layer
import java.util.logging.Level
import java.util.logging.Logger

class DomainLayerImpl : DomainLayer {
    private val users = mutableMapOf<String, User>()
    private val escooters = mutableMapOf<String, EScooter>()
    private val rides = mutableMapOf<String, Ride>()
    private var rideCounter = 0L

    companion object {
        private var dataSourceLayer: DataSourceLayer? = null
        fun getDataSourceLayer(): DataSourceLayer? = dataSourceLayer
        private val logger = Logger.getLogger("[DomainLayer]")
    }

    override fun addNewUser(id: String, name: String, surname: String) {
        val user = User(id, name, surname)
        users[id] = user
        user.save()
        logger.log(Level.INFO, "New user registered: $id")
    }

    override fun addNewEScooter(id: String) {
        val escooter = EScooter(id)
        escooters[id] = escooter
        escooter.save()
        logger.log(Level.INFO, "New escooter registered: $id")
    }

    override fun startNewRide(user: User, escooter: EScooter): String {
        escooter.updateState(EScooter.EScooterState.IN_USE)
        rideCounter++
        val rideId = "ride-$rideCounter"
        val ride = Ride(rideId, user, escooter)
        rides[rideId] = ride
        escooter.save()
        ride.save()
        logger.log(Level.INFO, "Started ride: $rideId")
        return rideId
    }

    override fun getUser(userId: String) = users[userId]
    override fun getEScooter(id: String) = escooters[id]
    override fun getRide(id: String) = rides[id]
    override fun init(layer: Layer?) {
        dataSourceLayer = layer as DataSourceLayer
    }

}