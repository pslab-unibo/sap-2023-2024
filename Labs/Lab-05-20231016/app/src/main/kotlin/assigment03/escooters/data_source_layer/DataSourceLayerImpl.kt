package assigment03.escooters.data_source_layer

import assigment03.layers.Layer
import io.vertx.core.json.JsonObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class DataSourceLayerImpl(private val dbaseFolder: String) : DataSourceLayer {
    private val userPath = "users"
    private val escooterPath = "escooters"
    private val riderPath = "rides"

    override fun init(layer: Layer?) {
        makeDir(dbaseFolder)
        makeDir("$dbaseFolder${File.separator}$userPath")
        makeDir("$dbaseFolder${File.separator}$escooterPath")
        makeDir("$dbaseFolder${File.separator}$riderPath")
    }

    @Throws(DataSourceException::class)
    override fun saveUser(user: JsonObject) = saveObj(userPath, user.getString("id"), user)

    @Throws(DataSourceException::class)
    override fun saveEScooter(escooter: JsonObject) = saveObj(escooterPath, escooter.getString("id"), escooter)

    @Throws(DataSourceException::class)
    override fun saveRide(ride: JsonObject) = saveObj(riderPath, ride.getString("id"), ride)

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