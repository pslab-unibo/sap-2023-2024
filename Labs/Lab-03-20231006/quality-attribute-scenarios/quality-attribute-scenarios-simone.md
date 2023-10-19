Feature: Concurrent e-scooter booking

when multiple users try to book the same e-scooter
caused by > 1 user
occur in booking management system
operating in normal condition
then the system only allows a single booking event to happen, rejecting the others
so that a single e-scooter is only booked by one person


Feature: Basic fraud security

when a scooter is left parked for 5 minutes
caused by the user not terminating the ride
operating in normal condition
then the ride should be automatically terminated
so that it is more difficult to steal an e-scooter

