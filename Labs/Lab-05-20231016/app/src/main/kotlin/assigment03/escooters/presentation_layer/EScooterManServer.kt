package assigment03.escooters.presentation_layer

import assigment03.escooters.service_layer.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.StaticHandler
import java.util.logging.Logger

class EScooterManServer(
    private val port: Int,
    private val serviceLayer: ServiceLayer
) : AbstractVerticle() {

    companion object {
        private val logger = Logger.getLogger("[EScooter Server]").apply {
            level = Logger.getLogger("").level
        }
    }

    override fun start() {
        logger.info("EScooterMan server initializing...")
        val server = vertx.createHttpServer()
        val router = Router.router(vertx)

        // Configuring routes
        router.configureRoutes()
        server.requestHandler(router).listen(port)

        logger.info("EScooterMan server ready - port: $port")
    }

    private fun Router.configureRoutes() {
        route("/static/*").handler(StaticHandler.create("Labs/Lab-04-20231013/app/webroot").setCachingEnabled(false))
        route().handler(BodyHandler.create())

        post("/api/users").handler(::registerNewUser)
        get("/api/users/:userId").handler(::getUserInfo)
        post("/api/escooters").handler(::registerNewEScooter)
        get("/api/escooters/:escooterId").handler(::getEScooterInfo)
        post("/api/rides").handler(::startNewRide)
        get("/api/rides/:rideId").handler(::getRideInfo)
        post("/api/rides/:rideId/end").handler(::endRide)
    }

    private fun registerNewUser(context: RoutingContext) {
        val userInfo = context.bodyAsJson
        val id = userInfo.getString("id")
        val name = userInfo.getString("name")
        val surname = userInfo.getString("surname")

        val reply = JsonObject().apply {
            try {
                serviceLayer.registerNewUser(id, name, surname)
                put("result", "ok")
            } catch (ex: UserIdAlreadyExistingException) {
                put("result", "user-id-already-existing")
            }
        }
        sendReply(context, reply)
    }

    private fun getUserInfo(context: RoutingContext) {
        val userId = context.pathParam("userId")
        val reply = JsonObject().apply {
            try {
                val info = serviceLayer.getUserInfo(userId)
                put("result", "ok").put("user", info)
            } catch (ex: UserNotFoundException) {
                put("result", "user-not-found")
            }
        }
        sendReply(context, reply)
    }

    private fun registerNewEScooter(context: RoutingContext) {
        val escooterInfo = context.bodyAsJson
        val id = escooterInfo.getString("id")

        val reply = JsonObject().apply {
            try {
                serviceLayer.registerNewEScooter(id)
                put("result", "ok")
            } catch (ex: UserIdAlreadyExistingException) {
                put("result", "escooter-id-already-existing")
            }
        }
        sendReply(context, reply)
    }

    private fun getEScooterInfo(context: RoutingContext) {
        val escooterId = context.pathParam("escooterId")
        val reply = JsonObject().apply {
            try {
                val info = serviceLayer.getEScooterInfo(escooterId)
                put("result", "ok").put("escooter", info)
            } catch (ex: EScooterNotFoundException) {
                put("result", "escooter-not-found")
            }
        }
        sendReply(context, reply)
    }

    private fun startNewRide(context: RoutingContext) {
        val rideInfo = context.bodyAsJson
        val userId = rideInfo.getString("userId")
        val escooterId = rideInfo.getString("escooterId")

        val reply = JsonObject().apply {
            try {
                val rideId = serviceLayer.startNewRide(userId, escooterId)
                put("result", "ok").put("rideId", rideId)
            } catch (ex: Exception) {
                put("result", "start-new-ride-failed")
            }
        }
        sendReply(context, reply)
    }

    private fun getRideInfo(context: RoutingContext) {
        val rideId = context.pathParam("rideId")
        val reply = JsonObject().apply {
            try {
                val info = serviceLayer.getRideInfo(rideId)
                put("result", "ok").put("ride", info)
            } catch (ex: RideNotFoundException) {
                put("result", "ride-not-found")
            }
        }
        sendReply(context, reply)
    }

    private fun endRide(context: RoutingContext) {
        val rideId = context.pathParam("rideId")
        val reply = JsonObject().apply {
            try {
                serviceLayer.endRide(rideId)
                put("result", "ok")
            } catch (ex: RideNotFoundException) {
                put("result", "ride-not-found")
            } catch (ex: RideAlreadyEndedException) {
                put("result", "ride-already-ended")
            }
        }
        sendReply(context, reply)
    }

    // Define other handlers similarly, utilizing Kotlin's concise syntax
    // ...

    private fun sendReply(context: RoutingContext, reply: JsonObject) {
        with(context.response()) {
            putHeader("content-type", "application/json")
            end(reply.encode())
        }
    }
}