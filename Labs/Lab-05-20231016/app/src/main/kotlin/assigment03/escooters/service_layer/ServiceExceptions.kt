package assigment03.escooters.service_layer

open class EScooterException(message: String) : Exception(message)

class EScooterNotFoundException : EScooterException("EScooter not found.")
class RideAlreadyEndedException : EScooterException("Ride has already ended.")
class RideNotFoundException : EScooterException("Ride not found.")
class RideNotPossibleException : EScooterException("Ride not possible.")
class UserIdAlreadyExistingException : EScooterException("User ID already exists.")
class UserNotFoundException : EScooterException("User not found.")