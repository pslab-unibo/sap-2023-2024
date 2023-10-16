package assigment03.escooters.domain_layer

import assigment03.layers.Layer

interface DomainLayer: Layer {
    fun addNewUser(id: String, name: String, surname: String)
    fun getUser(userId: String): User?
    fun addNewEScooter(id: String)
    fun getEScooter(id: String): EScooter?
    fun startNewRide(user: User, escooter: EScooter): String
    fun getRide(rideId: String): Ride?
}