# Vehicle Position Simulator
A Microservice that simulates the Positional Reports from the Vehicles

This microservices basically churns out the latitude and longitude for random vehicles to a queue (currently ActiveMQ but this can be anything).
These coordinates will be used by a Vehicle Position Tracker Microservice which will store the history of a Vehicle journey in this trip and other 
activities which inturn can be consumed by any front-end and show these GPS coordinates in the Maps.
