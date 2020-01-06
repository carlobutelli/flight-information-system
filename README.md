# Flight System Information Simulator
Flight System Information Simulator developed for assessment due to Thales Group

# Environment
The software in order to run needs a few env vars to be set
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/assessment
export SPRING_DATASOURCE_USERNAME=tech
export SPRING_DATASOURCE_PASSWORD=d0nt4get
```

# Start up dev local
Fire up the database in Docker with compose by running
```bash
docker-compose up -d
```
from the root dir launch the app locally with
```bash
mvn spring-boot:run
```
OR
```bash
mvn dependency:tree
mvn package
java -jar target/flight-system-information-simulator.jar
```

# Documentation API
Application will be locally served at the following
```bash
http://localhost:8080/swagger
```

# Healthchecks available at
```bash
http://localhost:8080/actuator/health
```

# Requirements
- Docker 2.1+
- Maven 3.6+
- Java 11

# How it works
The database is design with PostgreSQL and contains three main tables and a many to many relationship:
- flight
- airport
- airline
- airline2airport (many to many)
### Relations
- many2many between airport and airline since in every airport more airlines can arrives and departs and of course 
every flight can have as source/destination more than one airport.
- the airline2airport relation is the outcome from the many to many between airline and airport and it contains two 
extra fields, numOfArrivals and numOfDepartures.
- flight and airline are linked as one to many since a flight belongs to a single airline while the airline can 
perform more flights.

### Steps to reproduce
- Every resource has the routes to CRUD operations
- Airport's objects are created at runtime just for the POC
- Add an airline through the API
- Add the numbers of arrivals/departures to/from a given airport
- In the simulation route generate data (it will generate flights based on the number of the arrival/departures for 
every airline on the given airport)
- run simulations based on a given airport providing the custom current time (not required by default)

#### Routes explanation
- Route to allow the user to insert a new airline
```bash
POST /airlines
```

- Route to set/update delayed/cancelled probabilities of a given airline
```bash
PATCH /airlines/{airlineId}
```

- Route to generate scheduled flights for the current day
```bash
POST /simulation/flights/generate
```

- Route to allow the user to delete all the scheduled flights in order to clean the flights' table
```bash
DELETE /simulation/flights/
```

- Route to actually start the simulation on a given airport. It takes *customTime* as (optional) argument to allow the 
system to evolve and update the table of scheduled flights. If such parameter is missing the system catch the current 
time.
```bash
POST /simulation/{airportId}/simulate
```

#### Nice & drawback
- The software is able to evolute the flights for the current day. By providing (or not) a custom time the flight's 
table will be updated and so the response.
- Delayed flights are added during the simulation process based on the probability set through th API.
- Cancelled flight are added based on probability.
- All the operations are available through the API documentation in Swagger.