package assigment03.escooters.domain_layer

import io.vertx.core.json.JsonObject

class User(val id: String, val name: String, val surname: String) {
    fun save() {
        try {
            DomainLayerImpl.getDataSourceLayer()?.saveUser(toJson())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun toJson() = JsonObject().apply {
        put("id", id)
        put("name", name)
        put("surname", surname)
    }
}