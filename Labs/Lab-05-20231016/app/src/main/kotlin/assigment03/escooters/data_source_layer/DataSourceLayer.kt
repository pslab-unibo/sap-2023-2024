package assigment03.escooters.data_source_layer

import assigment03.layers.Layer
import io.vertx.core.json.JsonObject

interface DataSourceLayer : Layer {
    @Throws(DataSourceException::class)
    fun saveUser(user: JsonObject)

    @Throws(DataSourceException::class)
    fun saveEScooter(escooter: JsonObject)

    @Throws(DataSourceException::class)
    fun saveRide(ride: JsonObject)
}