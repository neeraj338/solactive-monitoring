Spring-Boot REST-API with swagger-ui
=====================================
A step-by-step introduction.

System Requirements:
--------------------
- OpenJDK for Java 1.8
- Git
- Maven 3.3.9 or higher

Building the example project:
-----------------------------

To build the fat JAR and run some tests:

    mvn clean install

To run:

    java -jar target/solactive-monitoring-0.0.1-SNAPSHOT.jar


Swagger UI:

    http://localhost:8080/swagger-ui.html

Assumptions/Improvements:
-------------------------
- Used the java collection api to hold the Tick object in memory, running a corn-scheduler for cleanup.
- To achieve constant time for GET api calls of statistics, All pre-computation is done while create (i.e Tick POST /ticks api call)
- Could be optimize some computation, by storing some temporary information about expiration of ticks.
- Test cases could have more strong, about more stress testing of an api.
- I assume distributed and scaling is not the concern for now, could be improve from that perspective.

References:
-----------

- 
