# Flight System Information Simulator
Flight System Information Simulator developed for assessment due to Thales Group

# Environment
The software in order to run needs a few env vars to be set
```$xslt
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/assessment
export SPRING_DATASOURCE_USERNAME=tech
export SPRING_DATASOURCE_PASSWORD=d0nt4get
```

# Start up dev local
Fire up the database in Docker with compose by running
```$xslt
docker-compose up -d
```
from the root dir launch the app locally with
```$xslt
mvn spring-boot:run
```
OR
```$xslt
mvn dependency:tree
mvn package
java -jar target/flight-system-information-simulator.jar
```

# Documentation API
Application will be locally served at the following
```$xslt
http://localhost:8080/swagger
```

# Healthchecks available at
```$xslt
http://localhost:8080/actuator/health
```

# Requirements
- Docker 2.1+
- Maven 3.6+
- Java 11