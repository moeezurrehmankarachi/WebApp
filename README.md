#Web Application:<br>
   The web application is a Spring Application and is using Vaadin 14 Flow as the presentation layer. The app provides List, Create, Update, Delete functionalities as well as a Check URL feature that checks the url of the "external service".
   <br>The "external service" lists grid is updated regularly using a Poller mechanism which after the configured time in ms has passed requests the backend for updated data list. The time can be configured in ```application.properties``` file with property ```poller.time```<br/>
   <br>The url address of the backend application can be configured using ```backend.base``` property in ```application.properties``` file<br/>
   <br>If it is required to create the jar file use ```mvn clean package``` in the project directory. Use ```mvn clean package -DskipTests``` to make jar without running tests. The program can be run using ```java -jar target/WebApp-0.0.1-SNAPSHOT.jar```.
   <br/><br/>

<br>If needed to run in IDEs such as Intellij Idea, all the projects can be used using the 'Application' run configuration and pointing to the classes with ```@SpringBootApplication``` annotation namely: ```com.task.service_state_poller.ServiceStatePollerApplication.java``` for poller service, ```com.task.backend.BackendApplication``` for backend application and ```com.kry.WebApp.Application``` for web application.<br/>