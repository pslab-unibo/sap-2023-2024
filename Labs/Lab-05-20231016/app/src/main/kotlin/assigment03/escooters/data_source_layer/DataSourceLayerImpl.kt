package assigment03.escooters.data_source_layer

import assigment03.layers.Layer
import io.vertx.core.json.JsonObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class DataSourceLayerImpl(private val dbaseFolder: String) : DataSourceLayer {
    private val USERS_PATH = "users"
    private val ESCOOTERS_PATH = "escooters"
    private val RIDES_PATH = "rides"

    override fun init(layer: Layer?) {
        makeDir(dbaseFolder)
        makeDir("$dbaseFolder${File.separator}$USERS_PATH")
        makeDir("$dbaseFolder${File.separator}$ESCOOTERS_PATH")
        makeDir("$dbaseFolder${File.separator}$RIDES_PATH")
    }

    @Throws(DataSourceException::class)
    override fun saveUser(user: JsonObject) = saveObj(USERS_PATH, user.getString("id"), user)

    @Throws(DataSourceException::class)
    override fun saveEScooter(escooter: JsonObject) = saveObj(ESCOOTERS_PATH, escooter.getString("id"), escooter)

    @Throws(DataSourceException::class)
    override fun saveRide(ride: JsonObject) = saveObj(RIDES_PATH, ride.getString("id"), ride)

    private fun saveObj(db: String, id: String, obj: JsonObject) {
        try {
            FileWriter("$dbaseFolder${File.separator}$db${File.separator}$id.json").use { fw ->
                BufferedWriter(fw).use { wr ->
                    wr.write(obj.encodePrettily())
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw DataSourceException(ex.message ?: "Unknown Error")
        }
    }

    private fun makeDir(name: String) {
        try {
            File(name).takeIf { !it.exists() }?.mkdir()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}