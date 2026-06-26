# movie-booking-aisi-project

This repository contains all the source code for the Advanced Information 
Systems Interoperability project at Algebra Bernays University (2025/2026).

## Project Structure

- `Movie_Booking_Application/` – Spring Boot REST API with JWT authentication and MySQL database
- `MovieBookingApplication-frontend/` – JavaFX desktop client
- `camel-project/` – Apache Camel integration project with ActiveMQ messaging
- `camunda/` – BPMN process diagram for the Camunda 8 workflow

## How to run
Prerequisites : Before running anything, make sure you have the following installed on your machine:
- Java 21+ (Eclipse Temurin JDK recommended)
- Maven
-	MySQL 8.0
-	Docker Desktop (for ActiveMQ)
-	IntelliJ IDEA
-	ngrok (if you want to run the Camunda process)

--Setting up the Database----------------------------------------------------------------------
-	Open MySQL and create a new database called moviedb
-	The tables will be created automatically by Spring Boot on first launch thanks to Hibernate
  
--Running the Spring Boot Backend--------------------------------------------------------------
-	Open the backend/ folder in IntelliJ IDEA
-	Go to src/main/resources/application.properties and update the database credentials if needed:
    spring.datasource.url=jdbc:mysql://localhost:3306/moviedb
    spring.datasource.username=root
    spring.datasource.password=your_password
- Open a terminal at the root of the backend/ folder and run: mvn clean install
- Run the MovieBookingApplication main class
-	The backend will start on http://localhost:8080
  
--Running the JavaFX Frontend------------------------------------------------------------------
-	Open the frontend/ folder in IntelliJ IDEA
-	Make sure the backend is already running before launching the frontend
-	Run the Main class
-	Log in with the admin credentials: username: user1 / password: user1
  
--Running Apache Camel and ActiveMQ with Docker-----------------------------------------------------------------
- Make sure the backend and Docker Desktop are running
-	Open a terminal at the root of the camel-project/ folder
- Run the following command: mvn clean install
-	Run the following command: docker compose up -d
- Run the CamelApplication main class
- The Camel routes will be available on https://localhost:8443/camel/movies
- You can trigger the routes from Postman using Basic Auth (username: user1 / password: user1)
-	The ActiveMQ broker will be available on http://localhost:8161 (admin/admin)

--Running the Camunda BPM Process--------------------------------------------------------------
-	Make sure the backend is running
-	Start ngrok to expose the backend publicly: ngrok http --url=reliably-onto-unify.ngrok-free.dev 8080
-	Copy the forwarding URL that ngrok gives you (e.g. https://xxxx.ngrok-free.dev)
-	Go to the Camunda Web Modeler at https://modeler.cloud.camunda.io
-	Import the .bpmn file and the forms from the camunda/ folder
-	Update all the Service Task URLs to use your new ngrok forwarding URL
-	Deploy all forms (this will start the camunda cluster)
-	Click Deploy & Run to start a new process instance
-	Go to Camunda Tasklist to interact with the User Tasks
-	Go to Camunda Operate to monitor the process execution


